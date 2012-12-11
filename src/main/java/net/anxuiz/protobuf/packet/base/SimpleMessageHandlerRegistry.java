package net.anxuiz.protobuf.packet.base;

import java.lang.reflect.Method;
import java.util.Map;

import net.anxuiz.protobuf.packet.*;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;

public class SimpleMessageHandlerRegistry implements MessageHandlerRegistry {
    private final Multimap<FieldDescriptor, MessageHandler> handlers = ArrayListMultimap.create();

    public void register(FieldDescriptor desc, MessageHandler handler) {
        this.handlers.put(desc, handler);
    }

    public void registerAll(final MessageListener listener) {
        for(Method method : listener.getClass().getMethods()) {
            if(method.getAnnotation(Handler.class) != null) {
                method.getParameterTypes();

                new MethodMessageExecutor(listener, method);
            }
        }
    }

    public void handle(Message msg) {
        for(Map.Entry<FieldDescriptor, MessageHandler> entry : this.handlers.entries()) {
            try {
                entry.getValue().handle(msg);
            } catch (HandlerException e) {
                e.printStackTrace();
            }
        }
    }
}
