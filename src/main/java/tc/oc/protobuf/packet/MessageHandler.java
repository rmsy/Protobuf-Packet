package tc.oc.protobuf.packet;

import com.google.protobuf.Message;

/**
 * Interface for classes with extension listeners ({@link Handler}s).
 */
public interface MessageHandler {
    /**
     * Handles a message of the parameter type specified.
     *
     * @param msg The message to handle.
     * @throws HandlerException If there was a critical error in handling the message.
     */
    void handle(Message msg) throws HandlerException;
}
