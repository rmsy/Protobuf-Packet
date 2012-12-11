package net.anxuiz.protobuf.packet;

public class HandlerException extends Exception {
    private static final long serialVersionUID = 1L;

    public HandlerException() {
        super();
    }

    public HandlerException(String message) {
        super(message);
    }

    public HandlerException(String message, Throwable cause) {
        super(message, cause);
    }

    public HandlerException(Throwable cause) {
        super(cause);
    }
}
