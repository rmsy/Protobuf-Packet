package net.anxuiz.protobuf.packet;

import javax.annotation.Nonnull;

import com.google.protobuf.Message;

public interface PacketManager<T extends Message> {
    int parse(@Nonnull T packet, @Nonnull MessageHandlerRegistry registry);

    @Nonnull T build(@Nonnull Message msg);
}
