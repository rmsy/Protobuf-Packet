package tc.oc.protobuf.packet;

import javax.annotation.Nonnull;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Message;


public interface MessageHandlerRegistry {
    boolean register(@Nonnull Descriptor messageDescriptor, @Nonnull MessageHandler handler);
    boolean registerAll(@Nonnull MessageListener listener);

    boolean unregister(@Nonnull Descriptor messageDescriptor, @Nonnull MessageHandler handler);
    boolean unregisterAll(@Nonnull Descriptor messageDescriptor);
    boolean unregisterAll(@Nonnull MessageListener listener);

    int handle(@Nonnull Message msg);
}
