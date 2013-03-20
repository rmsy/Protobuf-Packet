package tc.oc.protobuf.packet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Designates that annotated (and registered) methods should be invoked with their relative extension {@link
 * com.google.protobuf.Message}s for handling.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Handler {
}
