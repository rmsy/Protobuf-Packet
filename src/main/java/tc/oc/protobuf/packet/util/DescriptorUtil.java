package tc.oc.protobuf.packet.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Message;

public final class DescriptorUtil {
    private DescriptorUtil() { }

    public static @Nonnull Descriptor getDescriptor(@Nonnull Method method) throws IllegalArgumentException {
        Preconditions.checkNotNull(method, "method");

        // check params
        Class<?>[] params = method.getParameterTypes();
        if(params.length < 1) {
            throw new IllegalArgumentException("too few arguments to be a handler");
        } else if (params.length > 1) {
            throw new IllegalArgumentException("has too many arguments to be a handler");
        }

        // check to see if it is a message type
        Class<?> msgClass = params[0];
        if(Message.class.isAssignableFrom(msgClass)) {
            throw new IllegalArgumentException("parameter type " + msgClass + " is not a protobuf message type");
        }

        // try to get the method for getting the descriptor
        Method getDescriptor;
        try {
            getDescriptor = msgClass.getMethod("getDescriptor");
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("paramter type " + msgClass + " does not have the required getDescriptor() static method", e);
        } catch (SecurityException e) {
            throw new IllegalArgumentException("failed to fetch the getDescriptor() method for " + msgClass + " due to security constraints", e);
        }

        // try to invoke the method to get the descriptor
        Object rawDesc;
        try {
            rawDesc = getDescriptor.invoke(null);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("failed to invoke " + getDescriptor + " due to security constraints", e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException("exception when invoking " + getDescriptor, e);
        }

        // check to ensure the result is correct
        if(rawDesc instanceof Descriptor) {
            return (Descriptor) rawDesc;
        } else {
            throw new IllegalArgumentException("getDescriptor() for " + msgClass + " returned an object that was not a Descriptor");
        }
    }
}
