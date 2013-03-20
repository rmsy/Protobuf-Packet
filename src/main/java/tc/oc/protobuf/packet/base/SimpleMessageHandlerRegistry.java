package tc.oc.protobuf.packet.base;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Message;
import tc.oc.protobuf.packet.*;
import tc.oc.protobuf.packet.util.DescriptorUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple implementation of {@link MessageHandlerRegistry}.
 */
public class SimpleMessageHandlerRegistry implements MessageHandlerRegistry {
    @Nullable
    private final Logger logger;
    /**
     * A map of message types to their registered {@link MessageHandler}s.
     */
    @Nonnull
    private final Multimap<Descriptor, MessageHandler> handlers = ArrayListMultimap.create();

    /**
     * Creates a new SimpleMessageHandlerRegistry without a {@link Logger}.
     */
    public SimpleMessageHandlerRegistry() {
        this(null);
    }

    /**
     * Creates a new SimpleMessageHandlerRegistry with a {@link Logger} to log errors to.
     *
     * @param logger The {@link Logger} to log errors to.
     */
    public SimpleMessageHandlerRegistry(@Nullable Logger logger) {
        this.logger = logger;
    }

    /**
     * Registers only the {@link Handler} for the specified message type in the specified {@link MessageHandler}. This
     * can be used, for example, to register only specific {@link Handler}s depending on changing conditions.
     *
     * @param messageDescriptor The type of message to register the {@link MessageHandler}'s {@link Handler} for.
     * @param handler           The {@link MessageHandler} which contains the relevant {@link Handler}.
     * @return Whether or not the handler was already registered.
     */
    public boolean register(@Nonnull final Descriptor messageDescriptor, @Nonnull final MessageHandler handler) {
        return !this.handlers.put(Preconditions.checkNotNull(messageDescriptor, "register() got null messageDescriptor Descriptor"), Preconditions.checkNotNull(handler, "register() got null handler MessageHandler"));
    }

    /**
     * Registers all of the {@link Handler}s in the specified {@link MessageListener}.
     *
     * @param listener The {@link MessageListener} whose {@link Handler}s should be registered.
     * @return Whether or not the handler was already registered.
     */
    public boolean registerAll(@Nonnull final MessageListener listener) {
        return !this.handlers.putAll(this.getHandlersFromListener(Preconditions.checkNotNull(listener, "registerAll() got null listener MessageListener"), true));
    }

    /**
     * Un-registers only the {@link Handler} for the specified message type in the specified {@link MessageHandler}.
     * This can be used, for example, to un-register only specific {@link Handler}s depending on changing conditions.
     *
     * @param messageDescriptor The type of message to un-register the {@link MessageHandler}'s {@link Handler} for.
     * @param handler           The {@link MessageHandler} which contains the relevant {@link Handler}.
     * @return Whether or not the handler was already un-registered.
     */
    public boolean unRegister(@Nonnull final Descriptor messageDescriptor, @Nonnull final MessageHandler handler) {
        return !this.handlers.remove(Preconditions.checkNotNull(messageDescriptor, "unRegister() got null messageDescriptor Descriptor"), Preconditions.checkNotNull(handler, "unRegister() got null handler MessageHandler"));
    }

    /**
     * Un-registers all of the {@link Handler}s for the specified {@link Descriptor}.
     *
     * @param messageDescriptor The {@link Descriptor} whose {@link Handler}s should be unregistered.
     * @return Whether or not all of the descriptor's handlers were already unregistered.
     */
    public boolean unRegisterAll(@Nonnull final Descriptor messageDescriptor) {
        return this.handlers.removeAll(Preconditions.checkNotNull(messageDescriptor, "unRegisterAll() got null messageDescriptor Descriptor")).isEmpty();
    }

    /**
     * Un-registers all of the {@link MessageListener}'s {@link Handler}s.
     *
     * @param listener The {@link MessageListener} whose {@link Handler}s should be unregistered.
     */
    public boolean unRegisterAll(@Nonnull final MessageListener listener) {
        Preconditions.checkNotNull(listener, "unRegisterAll() got null listener MessageListener");
        boolean removed = false;
        for (Iterator<Map.Entry<Descriptor, MessageHandler>> it = this.handlers.entries().iterator(); it.hasNext(); ) {
            Map.Entry<Descriptor, MessageHandler> entry = it.next();
            if (entry.getValue() instanceof MethodMessageExecutor) {
                MethodMessageExecutor methodHandler = (MethodMessageExecutor) entry.getValue();
                if (listener.equals(methodHandler.getParent())) {
                    it.remove();
                    removed = true;
                }
            }
        }

        return removed;
    }

    /**
     * Handles the specified {@link Message}.
     *
     * @param message The {@link Message} to be handled.
     * @return The number of handlers that were invoked.
     */
    public int handle(@Nonnull Message message) {
        Preconditions.checkNotNull(message, "handle() got null Message");
        int numHandled = 0;
        for (Map.Entry<Descriptor, MessageHandler> entry : this.handlers.entries()) {
            if (entry.getKey().equals(message.getDescriptorForType())) {
                try {
                    entry.getValue().handle(message);
                    numHandled++;
                } catch (HandlerException e) {
                    this.logException("Exception while handling " + message, e);
                }
            }
        }
        return numHandled;
    }

    private Multimap<Descriptor, MethodMessageExecutor> getHandlersFromListener(MessageListener listener, boolean logErrors) {
        Multimap<Descriptor, MethodMessageExecutor> handlers = ArrayListMultimap.create();
        for (Method method : listener.getClass().getMethods()) {
            if (method.getAnnotation(Handler.class) != null) {
                Descriptor desc = null;
                try {
                    desc = DescriptorUtil.getDescriptor(method);
                } catch (IllegalArgumentException e) {
                    if (logErrors) {
                        if (e.getCause() != null) {
                            this.logException(e.getMessage(), e.getCause());
                        } else {
                            this.log(e.getMessage());
                        }
                    }
                }
                if (desc != null) {
                    handlers.put(desc, new MethodMessageExecutor(listener, method));
                }
            }
        }
        return handlers;
    }

    /**
     * Logs an error to {@link #logger}.
     *
     * @param message The error to be logged.
     */
    private void log(String message) {
        if (this.logger != null) {
            this.logger.log(Level.WARNING, message);
        }
    }

    /**
     * Logs an exception to {@link #logger}.
     *
     * @param message   The message to be displayed.
     * @param throwable The exception to be logged.
     */
    private void logException(String message, Throwable throwable) {
        if (this.logger != null) {
            this.logger.log(Level.SEVERE, message, throwable);
        }
    }
}
