package tc.oc.protobuf.packet;

import com.google.protobuf.Message;

import javax.annotation.Nonnull;

/**
 * Interface to automatically parse and build new packets based on a base packet.
 *
 * @param <T> The type of {@link Message}.
 */
public interface PacketManager<T extends Message> {
    /**
     * Parses the specified message, notifying the {@link MessageHandlerRegistry}'s registered {@link
     * tc.oc.protobuf.packet.Handler}s of the found extensions.
     *
     * @param packet            The message to be parsed.
     * @param extensionRegistry The registry to be used to handle found extensions.
     * @return The number of extensions handled, <b><i>not</i></b> the number of extensions parsed or the number of
     *         extensions that the message contained.
     */
    int parse(@Nonnull final T packet, @Nonnull final MessageHandlerRegistry extensionRegistry);

    /**
     * Partially builds a new message, setting the specified {@link Message}s to their relative extensions.
     *
     * @param messages The messages to be added as extensions.
     * @return The partially built message.
     */
    @Nonnull
    T build(@Nonnull Message... messages);
}
