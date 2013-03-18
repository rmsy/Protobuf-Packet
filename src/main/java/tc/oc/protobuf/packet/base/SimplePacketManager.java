package tc.oc.protobuf.packet.base;

import com.google.common.base.Preconditions;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;
import tc.oc.protobuf.packet.MessageHandlerRegistry;
import tc.oc.protobuf.packet.PacketManager;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class SimplePacketManager<T extends Message> implements PacketManager<T> {
    private final Message.Builder builder;
    private final Map<Descriptors.Descriptor, ExtensionRegistry.ExtensionInfo> descriptorMapping = new HashMap<>();
    private final ExtensionRegistry reg;

    public SimplePacketManager(@Nonnull Message packet, @Nonnull final ExtensionRegistry reg) {
        this.builder = packet.newBuilderForType();
        Set<ExtensionRegistry.ExtensionInfo> extensionInfoSet = reg.getExtensions();
        for (ExtensionRegistry.ExtensionInfo extension : extensionInfoSet) {
            if (extension.descriptor.getJavaType().equals(Descriptors.FieldDescriptor.JavaType.MESSAGE)) {
                descriptorMapping.put(extension.descriptor.getExtensionScope(), extension);
            }
        }
        assert descriptorMapping != null;
        this.reg = reg;
    }

    /**
     * Not implemented. TODO: Implementation.
     */
    public int parse(@Nonnull T packet, @Nonnull MessageHandlerRegistry registry) {
        Preconditions.checkNotNull(packet, "packet");
        Preconditions.checkNotNull(registry, "handler registry");

        int numParsed = 0;
        for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : packet.getAllFields().entrySet()) {
            if (this.descriptorMapping.containsValue(entry.getKey())) {
                Message msg = (Message) entry.getValue();
                registry.handle(msg);
                numParsed++;
            }
        }

        return numParsed;
    }

    @Nonnull
    public T build(Message msg) {
        Preconditions.checkNotNull(msg, "message");
        Message.Builder packet = this.builder.clone();
        packet.setField(descriptorMapping.get(msg.getDescriptorForType()).descriptor, msg);

        return (T) packet.buildPartial();
    }

    /**
     * Gets the {@link ExtensionRegistry} for use in parsing packets.
     *
     * @return The {@link ExtensionRegistry} for use in parsing packets.
     */
    @Nonnull
    public ExtensionRegistry getExtensionRegistry() {
        return this.reg;
    }
}
