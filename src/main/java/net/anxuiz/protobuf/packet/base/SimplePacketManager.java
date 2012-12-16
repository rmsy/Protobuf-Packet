package net.anxuiz.protobuf.packet.base;

import java.util.Map;

import javax.annotation.Nonnull;

import net.anxuiz.protobuf.packet.MessageHandlerRegistry;
import net.anxuiz.protobuf.packet.PacketManager;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.Type;
import com.google.protobuf.*;
import com.google.protobuf.Message.Builder;

public final class SimplePacketManager<T extends Message> implements PacketManager<T> {
    private final Builder factory;
    // type descriptor to field descriptor
    private final BiMap<Descriptor, FieldDescriptor> descriptorMapping = HashBiMap.create();
    private final ExtensionRegistry reg = ExtensionRegistry.newInstance();

    public SimplePacketManager(@Nonnull Message packet) {
        Preconditions.checkNotNull(packet, "packet");

        this.factory = packet.newBuilderForType();

        for(FieldDescriptor desc : this.factory.getDescriptorForType().getExtensions()) {
            if(desc.getType() == Type.MESSAGE) {
                this.descriptorMapping.put(desc.getContainingType(), desc);
                this.reg.add(desc);
            }
        }
    }

    public int parse(@Nonnull T packet, @Nonnull MessageHandlerRegistry registry) {
        Preconditions.checkNotNull(packet, "packet");
        Preconditions.checkNotNull(registry, "handler registry");

        int numParsed = 0;
        for(Map.Entry<FieldDescriptor, Object> entry : packet.getAllFields().entrySet()) {
            if(this.descriptorMapping.containsValue(entry.getKey())) {
                Message msg = (Message) entry.getValue();
                registry.handle(msg);
                numParsed++;
            }
        }

        return numParsed;
    }

    @SuppressWarnings("unchecked")
    public @Nonnull T build(Message msg) {
        Preconditions.checkNotNull(msg, "message");

        Builder packet = this.factory.clone();
        FieldDescriptor desc = this.descriptorMapping.get(msg.getDescriptorForType());
        packet.setField(desc, msg);

        return (T) packet.buildPartial();
    }
}
