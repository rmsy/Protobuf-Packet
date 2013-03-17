package tc.oc.protobuf.packet.util;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;

public final class ExtensionUtil {
    private ExtensionUtil() { }

    public static FieldDescriptor getExtensionFor(Descriptor packet, Message msg) {
        for(FieldDescriptor ext : packet.getExtensions()) {
            if(msg.getDescriptorForType().equals(ext.getExtensionScope())) {
                return ext;
            }
        }
        return null;
    }
}
