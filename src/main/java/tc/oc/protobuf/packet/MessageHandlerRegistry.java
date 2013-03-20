package tc.oc.protobuf.packet;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Message;

import javax.annotation.Nonnull;


public interface MessageHandlerRegistry {
    boolean register(@Nonnull Descriptor messageDescriptor, @Nonnull MessageHandler handler);

    boolean registerAll(@Nonnull MessageListener listener);

    boolean unRegister(@Nonnull Descriptor messageDescriptor, @Nonnull MessageHandler handler);

    boolean unRegisterAll(@Nonnull Descriptor messageDescriptor);

    boolean unRegisterAll(@Nonnull MessageListener listener);

    int handle(@Nonnull Message msg);
}
