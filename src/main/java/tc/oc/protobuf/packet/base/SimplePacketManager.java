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

/**
 * Simple implementation of {@link PacketManager}.
 *
 * @param <T> The type of {@link Message} that will be extended.
 */
public final class SimplePacketManager<T extends Message> implements PacketManager<T> {
    /**
     * The default builder for the message's type.
     */
    private final Message.Builder builder;
    /**
     * A map of the message's extended message field types and their {@link com.google.protobuf.ExtensionRegistry.ExtensionInfo}
     * objects.
     */
    private final Map<Descriptors.Descriptor, ExtensionRegistry.ExtensionInfo> descriptorMapping = new HashMap<>();

    /**
     * Creates a new {@link SimplePacketManager}, using the specified {@link Message} as a reference of sorts for
     * automatically building and parsing new messages.
     *
     * @param packet            The base {@link Message}.
     * @param extensionRegistry The {@link ExtensionRegistry} containing the specified {@link Message}'s extensions.
     *                          <b>Important</b>: Extensions not registered in this {@link ExtensionRegistry} will be
     *                          disregarded.
     */
    public SimplePacketManager(@Nonnull final T packet, @Nonnull final ExtensionRegistry extensionRegistry) {
        this.builder = Preconditions.checkNotNull(packet, "SimplePacketManager constructor got null Message").newBuilderForType();
        Set<ExtensionRegistry.ExtensionInfo> extensionInfoSet = Preconditions.checkNotNull(extensionRegistry, "SimplePacketManager constructor got null ExtensionRegistry").getExtensions();
        for (ExtensionRegistry.ExtensionInfo extension : extensionInfoSet) {
            if (extension.descriptor.getJavaType().equals(Descriptors.FieldDescriptor.JavaType.MESSAGE)) {
                descriptorMapping.put(extension.descriptor.getMessageType(), extension);
            }
        }
    }

    /**
     * Parses the specified message, notifying the {@link MessageHandlerRegistry}'s registered {@link
     * tc.oc.protobuf.packet.Handler}s of the found extensions.
     *
     * @param packet   The message to be parsed.
     * @param registry The registry to be used to handle found extensions.
     * @return The number of extensions handled, <b><i>not</i></b> the number of extensions parsed or the number of
     *         extensions that the message contained.
     */
    public int parse(@Nonnull final T packet, @Nonnull final MessageHandlerRegistry registry) {
        Preconditions.checkNotNull(registry, "parse() got null MessageHandlerRegistry");
        Preconditions.checkNotNull(packet, "parse() got null Message");
        int numParsed = 0;
        for (ExtensionRegistry.ExtensionInfo extension : descriptorMapping.values()) {
            if (packet.hasField(extension.descriptor)) {
                numParsed += registry.handle((Message) packet.getField(extension.descriptor));
            }
        }
        return numParsed;
    }

    /**
     * Partially builds a new message, setting the specified {@link Message}s to their relative extensions.
     *
     * @param messages The messages to be added as extensions.
     * @return The partially built message.
     */
    @Nonnull
    public T build(@Nonnull final Message... messages) {
        Preconditions.checkNotNull(messages, "build() got null Messages[]");
        Message.Builder packet = this.builder.clone();
        for (Message message : messages) {
            packet.setField(descriptorMapping.get(message.getDescriptorForType()).descriptor, message);
        }
        return (T) packet.buildPartial();
    }
}
