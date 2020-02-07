package org.dru.dusap.rpc.json;

import org.dru.dusap.json.JsonElement;

public interface JsonRpcMessageFactory {
    JsonElement newRequestMessage(long id, String method, Object[] params);

    JsonElement newNotifyMessage(String method, Object[] params);

    JsonElement newResultMessage(long id, Object result);

    JsonElement newErrorMessage(long id, int code, String message, Object data);

    JsonElement newErrorMessage(long id, int code, String message);
}
