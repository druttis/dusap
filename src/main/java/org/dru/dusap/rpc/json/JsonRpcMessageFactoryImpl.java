package org.dru.dusap.rpc.json;

import org.dru.dusap.json.JsonElement;
import org.dru.dusap.json.JsonSerializer;
import org.dru.dusap.json.JsonSerializerSupplier;

import static org.dru.dusap.rpc.json.JsonRpcConstants.*;

public final class JsonRpcMessageFactoryImpl implements JsonRpcMessageFactory {
    private final JsonSerializer serializer;

    public JsonRpcMessageFactoryImpl(final JsonSerializerSupplier supplier) {
        serializer = supplier.get();
    }

    @Override
    public JsonElement newRequestMessage(final long id, final String method, final Object[] params) {
        final JsonElement requestMessage = newNotifyMessage(method, params);
        requestMessage.put(ID, serializer.newNumber(id));
        return requestMessage;
    }

    @Override
    public JsonElement newNotifyMessage(final String method, final Object[] params) {
        final JsonElement notifyMessage = newMessage();
        notifyMessage.put(METHOD, serializer.newString(method));
        notifyMessage.put(PARAMS, serializer.objectToElement(params));
        return notifyMessage;
    }

    @Override
    public JsonElement newResultMessage(final long id, final Object result) {
        final JsonElement resultMessage = newMessage();
        resultMessage.put(RESULT, serializer.objectToElement(result));
        return resultMessage;
    }

    @Override
    public JsonElement newErrorMessage(final long id, final int code, final String message, final Object data) {
        final JsonElement errorMessage = newErrorMessage(id, code, message);
        errorMessage.put(ERROR_DATA, serializer.objectToElement(data));
        return errorMessage;
    }

    @Override
    public JsonElement newErrorMessage(final long id, final int code, final String message) {
        final JsonElement errorMessage = newMessage();
        errorMessage.put(ERROR_CODE, serializer.newNumber(code));
        errorMessage.put(ERROR_MESSAGE, serializer.newString(message));
        return errorMessage;
    }

    public JsonElement newMessage() {
        final JsonElement message = serializer.newObject();
        message.put(JSONRPC, serializer.newString(VERSION));
        return message;
    }
}
