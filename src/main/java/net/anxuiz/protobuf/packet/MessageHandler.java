package net.anxuiz.protobuf.packet;

import com.google.protobuf.Message;

public interface MessageHandler {
    void handle(Message msg) throws HandlerException;
}
