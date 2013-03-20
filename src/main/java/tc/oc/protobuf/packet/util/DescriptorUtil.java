package tc.oc.protobuf.packet.util;

import com.google.common.base.Preconditions;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Message;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Utility class for {@link Descriptor}s.
 */
public final class DescriptorUtil {
    private DescriptorUtil() {
    }

    /**
     * Gets the {@link Descriptor} for the specified {@link Method}'s {@link Message} parameter type.
     *
     * @param method The Method containing the parameter.
     * @return The {@link Descriptor} of the Method's parameter type.
     * @throws IllegalArgumentException For the following reasons:<ul><li>The method has too few arguments to be a
     *                                  {@link tc.oc.protobuf.packet.Handler}</li><li>The method has too many arguments
     *                                  to be a {@link tc.oc.protobuf.packet.Handler}</li><li>The method does not have a
     *                                  {@link Message} parameter</li><li>The method's {@link Message} parameter type
     *                                  does not have getDescriptor()</li><li>The method's {@link Message}'s parameter
     *                                  type's getDescriptor() method could not be retrieved due to security
     *                                  constraints</li><li>The method's {@link Message}'s parameter type's
     *                                  getDescription() method does not have return type of {@link
     *                                  Descriptor}</li><li>The method's {@link Message}'s parameter type's
     *                                  getDescriptor() method could not be invoked due to security
     *                                  constraints</li><li>The method's {@link Message}'s parameter type's
     *                                  getDescription() method threw an {@link InvocationTargetException}</li><li>The
     *                                  method's {@link Message}'s parameter type's getDescription() method threw an
     *                                  {@link InvocationTargetException}</li><li>The method's {@link Message}'s
     *                                  parameter type's getDescription() method threw an {@link
     *                                  InvocationTargetException}</li></ul>
     */
    @Nonnull
    public static Descriptor getDescriptor(@Nonnull Method method) throws IllegalArgumentException {
        Class<?>[] params = Preconditions.checkNotNull(method, "getDescriptor() got null Method").getParameterTypes();
        if (params.length < 1) {
            throw new IllegalArgumentException("Has too few parameters to be a handler");
        } else if (params.length > 1) {
            throw new IllegalArgumentException("Has too many parameters to be a handler");
        }
        Class<?> msgClass = params[0];
        if (!Message.class.isAssignableFrom(msgClass)) {
            throw new IllegalArgumentException("Parameter type " + msgClass + " is not a Message");
        }
        Method getDescriptor;
        try {
            getDescriptor = msgClass.getMethod("getDescriptor");
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Parameter type " + msgClass + " does not have the required getDescriptor() static method", e);
        } catch (SecurityException e) {
            throw new IllegalArgumentException("Failed to fetch the getDescriptor() method for " + msgClass + " due to security constraints", e);
        }
        if (!getDescriptor.getReturnType().equals(Descriptor.class)) {
            throw new IllegalArgumentException("getDescriptor() for " + msgClass + " does not return the required type of Descriptor");
        }
        Descriptor descriptor;
        try {
            descriptor = (Descriptor) getDescriptor.invoke(null);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Failed to invoke the getDescriptor() method for " + getDescriptor + " due to security constraints", e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException("The getDescriptor() method for  " + msgClass + " threw an InvocationTargetException", e);
        }
        return descriptor;
    }
}
