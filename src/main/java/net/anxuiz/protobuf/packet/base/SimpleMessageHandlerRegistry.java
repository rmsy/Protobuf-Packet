package net.anxuiz.protobuf.packet.base;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.anxuiz.protobuf.packet.*;
import net.anxuiz.protobuf.packet.util.DescriptorUtil;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Message;

public class SimpleMessageHandlerRegistry implements MessageHandlerRegistry {
    private final @Nullable Logger logger;
    private final @Nonnull Multimap<Descriptor, MessageHandler> handlers = ArrayListMultimap.create();

    public SimpleMessageHandlerRegistry() {
        this(null);
    }

    public SimpleMessageHandlerRegistry(@Nullable Logger logger) {
        this.logger = logger;
    }

    public boolean register(@Nonnull Descriptor messageDescriptor, @Nonnull MessageHandler handler) {
        Preconditions.checkNotNull(messageDescriptor, "message descriptor");
        Preconditions.checkNotNull(handler, "message handler");

        return this.handlers.put(messageDescriptor, handler);
    }

    public boolean registerAll(@Nonnull MessageListener listener) {
        Preconditions.checkNotNull(listener, "message listener");

        return this.handlers.putAll(this.getHandlersFromListener(listener, true));
    }

    public boolean unregister(@Nonnull Descriptor messageDescriptor, @Nonnull MessageHandler handler) {
        Preconditions.checkNotNull(messageDescriptor, "message descriptor");
        Preconditions.checkNotNull(handler, "message handler");

        return this.handlers.remove(messageDescriptor, handler);
    }

    public boolean unregisterAll(@Nonnull Descriptor messageDescriptor) {
        Preconditions.checkNotNull(messageDescriptor, "message descriptor");

        return !this.handlers.removeAll(messageDescriptor).isEmpty();
    }

    public boolean unregisterAll(@Nonnull MessageListener listener) {
        Preconditions.checkNotNull(listener, "message listener");

        boolean removed = false;

        for(Iterator<Map.Entry<Descriptor, MessageHandler>> it = this.handlers.entries().iterator(); it.hasNext(); ) {
            Map.Entry<Descriptor, MessageHandler> entry = it.next();
            if(entry.getValue() instanceof MethodMessageExecutor) {
                MethodMessageExecutor methodHandler = (MethodMessageExecutor) entry.getValue();
                if(listener.equals(methodHandler.getParent())) {
                    it.remove();
                    removed = true;
                }
            }
        }

        return removed;
    }

    public int handle(@Nonnull Message msg) {
        Preconditions.checkNotNull(msg, "message");

        int numHandled = 0;

        for(Map.Entry<Descriptor, MessageHandler> entry : this.handlers.entries()) {
            try {
                entry.getValue().handle(msg);
                numHandled++;
            } catch (HandlerException e) {
                this.logException("Exception while handling " + msg, e);
            }
        }

        return numHandled;
    }

    private Multimap<Descriptor, MethodMessageExecutor> getHandlersFromListener(MessageListener listener, boolean logErrors) {
        Multimap<Descriptor, MethodMessageExecutor> handlers = ArrayListMultimap.create();

        for(Method method : listener.getClass().getMethods()) {
            if(method.getAnnotation(Handler.class) != null) {
                Descriptor desc = null;
                try {
                    desc = DescriptorUtil.getDescriptor(method);
                } catch (IllegalArgumentException e) {
                    if(logErrors) {
                        if(e.getCause() != null) {
                            this.logException(e.getMessage(), e.getCause());
                        } else {
                            this.log(e.getMessage());
                        }
                    }
                }
                if(desc != null) {
                    handlers.put(desc, new MethodMessageExecutor(listener, method));
                }
            }
        }

        return handlers;
    }

    private void log(String message) {
        if(this.logger != null) {
            this.logger.log(Level.WARNING, message);
        }
    }

    private void logException(String message, Throwable t) {
        if(this.logger != null) {
            this.logger.log(Level.SEVERE, message, t);
        }
    }
}
