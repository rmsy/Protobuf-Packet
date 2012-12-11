package net.anxuiz.protobuf.packet.base;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.anxuiz.protobuf.packet.HandlerException;
import net.anxuiz.protobuf.packet.MessageHandler;

import com.google.common.base.Preconditions;
import com.google.protobuf.Message;

public class MethodMessageExecutor implements MessageHandler {
    private final Object parent;
    private final Method method;

    public MethodMessageExecutor(final Object parent, final Method method) {
        Preconditions.checkNotNull(parent, "parent");
        Preconditions.checkNotNull(method, "method");

        this.parent = parent;
        this.method = method;
    }

    public void handle(Message msg) throws HandlerException {
        try {
            this.method.invoke(this.parent, msg);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            throw new HandlerException(e.getCause());
        }
    }
}
