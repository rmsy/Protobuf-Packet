package net.anxuiz.protobuf.packet;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;


public interface MessageHandlerRegistry {
    void register(FieldDescriptor desc, MessageHandler executor);
    void registerAll(MessageListener listener);

    void handle(Message msg);
}
