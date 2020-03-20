package org.dru.dusap.store;

import org.dru.dusap.reflection.ReflectionUtils;
import org.dru.dusap.util.JumpConsistentHash;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class DistributedStore<K, V> extends AbstractStore<K, V> {
    private final List<Bucket<K, V>> buckets;

    public DistributedStore(final List<Bucket<K, V>> buckets) {
        this.buckets = buckets;
    }

    @Override
    protected Map<K, V> getAllImpl(final Set<K> keys) {
        final Map<K, BucketRow<K, V>> rows = selectAll(keys, false);
        final Map<K, V> result = new HashMap<>();
        final Map<FromTo<K, V>, Map<K, Row<V>>> movers = new HashMap<>();
        final Map<Bucket<K, V>, Map<K, Row<V>>> erasers = new HashMap<>();
        for (final K key : keys) {
            final BucketRow<K, V> row = rows.getOrDefault(key, BucketRow.create());
            if (row.value() != null) {
                result.put(key, row.value());
            }
            final Bucket<K, V> bucket = getBucket(key);
            if (bucket != row.bucket()) {
                if (row.bucket() != null) {
                    final FromTo<K, V> fromTo = new FromTo<>(row.bucket(), bucket);
                    movers.computeIfAbsent(fromTo, ($) -> new HashMap<>())
                            .put(key, Row.create(row.value(), row.modified()));
                } else {
                    erasers.computeIfAbsent(bucket, ($) -> new HashMap<>())
                            .put(key, Row.create(null, row.modified()));
                }
            }
        }
        movers.forEach((fromTo, map) -> {
            final Bucket<K, V> ideal = fromTo.to;
            ideal.begin();
            map.forEach((key, row) -> ideal.upsert(key, row.value(), row.modified() + 1L));
            ideal.commit();
        });
        erasers.forEach((bucket, map) -> {
            bucket.begin();
            map.forEach((key, row) -> bucket.delete(key, row.modified() + 1L));
            bucket.commit();
        });
        //
        return result;
    }

    @Override
    protected Map<K, V> computeAllImpl(final Set<K> keys, final BiFunction<K, V, V> operator) {
        final Set<Bucket<K, V>> locked = new HashSet<>();
        try {
            final Map<K, BucketRow<K, V>> rows = selectAll(keys, true);
            final Map<K, V> result = new HashMap<>();
            for (final K key : keys) {
                final BucketRow<K, V> row = rows.getOrDefault(key, BucketRow.create());
                final V value = operator.apply(key, ReflectionUtils.copyInstance(row.value()));
                if (value != null) {
                    result.put(key, value);
                }
                final Bucket<K, V> bucket = getBucket(key);
                locked.add(bucket);
                if (bucket != row.bucket()) {
                    bucket.upsert(key, value, row.modified() + 1L);
                    if (row.bucket() != null) {
                        locked.add(row.bucket());
                        row.bucket().delete(key, row.modified() + 1L);
                    }
                } else if ((value == null && row.value() != null) || (value != null && !value.equals(row.value()))) {
                    bucket.update(key, value, row.modified() + 1L);
                }

            }
            locked.forEach(Bucket::commit);
            return result;
        } catch (final Exception exc) {
            locked.forEach(Bucket::rollback);
            throw exc;
        }
    }

    public int getNumBuckets() {
        return buckets.size();
    }

    public void addBucket(final int numBuckets, Function<Integer, Bucket<K, V>> bucketProvider) {
        for (int bucketNum = 0; bucketNum < numBuckets; bucketNum++) {
            buckets.add(bucketProvider.apply(buckets.size()));
        }
    }

    public Map<Bucket<K, V>, Map<K, Long>> inspectAll(final Set<K> keys) {
        final Map<K, BucketRow<K, V>> rows = selectAll(keys, false);
        final Map<Bucket<K, V>, Map<K, Long>> result = new HashMap<>();
        rows.forEach((key, row) ->
                result.computeIfAbsent(row.bucket(), ($) -> new HashMap<>()).put(key, row.modified())
        );
        return result;
    }

    public Bucket<K, V> getBucket(final int bucketNum) {
        return buckets.get(bucketNum);
    }

    public Bucket<K, V> getBucket(final K key) {
        final int bucketNum = getBucketNum(key, getNumBuckets());
        return getBucket(bucketNum);
    }

    public int getBucketNum(final K key, final int numBuckets) {
        if (numBuckets > getNumBuckets()) {
            throw new IllegalArgumentException("specified numBuckets is greater than actual numBuckets: "
                    + numBuckets + " > " + getNumBuckets());
        }
        return JumpConsistentHash.hash(key, numBuckets);
    }

    public Map<Integer, Set<K>> groupKeysByBucketNums(final Collection<K> keys, final int numBuckets) {
        final Map<Integer, Set<K>> keysByShardNum = new HashMap<>();
        for (final K key : keys) {
            final int bucketNum = getBucketNum(key, numBuckets);
            keysByShardNum.computeIfAbsent(bucketNum, ($) -> new HashSet<>()).add(key);
        }
        return keysByShardNum;
    }

    public Map<K, BucketRow<K, V>> selectAll(final Collection<K> keys, final boolean lock) {
        final Map<K, BucketRow<K, V>> result = new HashMap<>();
        final Set<Bucket<K, V>> locked = new HashSet<>();
        final Set<Bucket<K, V>> resulting = new HashSet<>();
        final Map<Bucket<K, V>, Set<K>> excludeKeys = new HashMap<>();
        //
        // create remaining keys by initially set it to all specified keys.
        final Set<K> remainingKeys = new HashSet<>(keys);
        //
        // loop descendant from number of shards to 1 shard and stop if there's no remaining keys.
        for (int forNumShards = getNumBuckets(); !remainingKeys.isEmpty() && forNumShards > 0; forNumShards--) {
            //
            // group keys by shard num ideal for the current num shards.
            final Map<Integer, Set<K>> keysGroupedByBucketNum = groupKeysByBucketNums(remainingKeys, forNumShards);
            //
            // get highest and lowest shard num in the grouping above.
            final int highestBucketNum = keysGroupedByBucketNum.keySet().stream().max(Integer::compare).orElse(0);
            final int lowestBucketNum = keysGroupedByBucketNum.keySet().stream().min(Integer::compare).orElse(0);
            //
            // prepare missing keys.
            final Set<K> missingKeys = new HashSet<>();
            //
            // loop descendant from highestBucketNum to lowest in the grouping above.
            for (int currentBucketNum = highestBucketNum; currentBucketNum >= lowestBucketNum; currentBucketNum--) {
                //
                // if there's no keys grouped in current shardNum then continue with next shard.
                final Set<K> selectKeys = keysGroupedByBucketNum.get(currentBucketNum);
                if (selectKeys == null) {
                    continue;
                }
                //
                // add missing keys to keys to select and select it from the current shard.
                selectKeys.addAll(missingKeys);
                final Bucket<K, V> currentBucket = getBucket(currentBucketNum);
                final Set<K> exclude = excludeKeys.computeIfAbsent(currentBucket, ($) -> new HashSet<>());
                if (lock && locked.add(currentBucket)) {
                    currentBucket.begin();
                }
                final Map<K, Row<V>> rows;
                try {
                    final Set<K> reducedKeys = selectKeys.stream()
                            .filter(key -> !exclude.contains(key))
                            .collect(Collectors.toSet());
                    if (reducedKeys.isEmpty()) {
                        continue;
                    }
                    rows = currentBucket.select(reducedKeys, lock);
                    exclude.addAll(selectKeys);
                    if (!rows.isEmpty()) {
                        resulting.add(currentBucket);
                    } else if (lock && !resulting.contains(currentBucket) && locked.remove(currentBucket)) {
                        currentBucket.rollback();
                        continue;
                    }
                } catch (final Exception exc) {
                    if (lock && !resulting.contains(currentBucket) && locked.remove(currentBucket)) {
                        currentBucket.rollback();
                    }
                    exc.printStackTrace();
                    continue;
                }
                final Map<K, BucketRow<K, V>> bucketRows = rows.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, (e) -> {
                                    final Row<V> row = e.getValue();
                                    return BucketRow.create(currentBucket, row.value(), row.modified());
                                }
                        ));
                //
                // add hits to result.
                result.putAll(bucketRows);
                //
                // remove hits from remaining keys
                remainingKeys.removeAll(bucketRows.keySet());
                //
                // remove hits from missing keys.
                missingKeys.removeAll(bucketRows.keySet());
                //
                // add misses to missing keys.
                missingKeys.addAll(selectKeys.stream()
                        .filter(key -> !bucketRows.containsKey(key))
                        .collect(Collectors.toSet())
                );
            }
        }
        //
        // Keys in remainingKeys means, they do not exist in the cluster.
        return result;
    }

    private static final class FromTo<K, V> {
        private final Bucket<K, V> from;
        private final Bucket<K, V> to;

        public FromTo(final Bucket<K, V> from, final Bucket<K, V> to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (!(o instanceof DistributedStore.FromTo)) return false;
            final FromTo<?, ?> that = (FromTo<?, ?>) o;
            return from.equals(that.from) &&
                    to.equals(that.to);
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to);
        }
    }
}
