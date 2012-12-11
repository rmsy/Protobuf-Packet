package net.anxuiz.protobuf.packet;

import com.google.protobuf.Message;

public interface PacketManager<T extends Message> {
    void parse(T packet, MessageHandlerRegistry registry);

    T build(Message msg);
}
