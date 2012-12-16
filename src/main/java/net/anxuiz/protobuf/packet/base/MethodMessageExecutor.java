package net.anxuiz.protobuf.packet.base;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.anxuiz.protobuf.packet.HandlerException;
import net.anxuiz.protobuf.packet.MessageHandler;

import com.google.common.base.Preconditions;
import com.google.protobuf.Message;

public class MethodMessageExecutor implements MessageHandler {
    private final @Nullable Object parent;
    private final @Nonnull Method method;

    public MethodMessageExecutor(@Nullable Object parent, @Nonnull Method method) {
        Preconditions.checkNotNull(method, "method");

        this.parent = parent;
        this.method = method;
    }

    public @Nullable Object getParent() {
        return this.parent;
    }

    public @Nonnull Method getMethod() {
        return this.method;
    }

    public void handle(@Nonnull Message msg) throws HandlerException {
        Preconditions.checkNotNull(msg, "message");

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
