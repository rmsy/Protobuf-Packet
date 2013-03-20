package tc.oc.protobuf.packet.base;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import tc.oc.protobuf.packet.*;
import tc.oc.protobuf.packet.protocols.TestExtendingMessage;
import tc.oc.protobuf.packet.protocols.TestGenericMessage;

import java.util.Random;

/**
 * Test suite for {@link SimplePacketManager}.
 */
@RunWith(JUnit4.class)
public class SimplePacketManagerTest implements MessageListener {
    private MessageHandlerRegistry handlerRegistry;
    private PacketManager<TestGenericMessage.GenericMessage> packetManager;
    private int extendedInt;
    private int parsedInt;
    private boolean handlerHasRun;

    /**
     * Called before any tests are invoked; initializes variables needed for testing.
     */
    @Before
    public void initialize() {
        ExtensionRegistry registry = ExtensionRegistry.newInstance();
        TestExtendingMessage.registerAllExtensions(registry);
        TestGenericMessage.GenericMessage message = TestGenericMessage.GenericMessage.newBuilder().setExtension(TestExtendingMessage.ExtendingMessage.extendingMessage, TestExtendingMessage.ExtendingMessage.newBuilder().setNumericalValue(extendedInt).build()).build();
        this.packetManager = new SimplePacketManager<>(message, registry);
    }

    /**
     * Test to verify that {@link SimplePacketManager#parse(com.google.protobuf.Message,
     * tc.oc.protobuf.packet.MessageHandlerRegistry)} is invoking handlers.
     */
    @Test
    public void handlerRunTest() {
        this.handlerHasRun = false;
        this.handlerRegistry = new SimpleMessageHandlerRegistry();
        handlerRegistry.registerAll(this);
        this.packetManager.parse(TestGenericMessage.GenericMessage.newBuilder().setExtension(TestExtendingMessage.ExtendingMessage.extendingMessage, TestExtendingMessage.ExtendingMessage.newBuilder().setNumericalValue(new Random().nextInt(15)).build()).build(), this.handlerRegistry);
        Assert.assertTrue("handle() did not run", this.handlerHasRun);
    }

    /**
     * Test to verify that {@link SimplePacketManager#parse(com.google.protobuf.Message,
     * tc.oc.protobuf.packet.MessageHandlerRegistry)} is properly parsing extended messages.
     */
    @Test
    public void handlerParseTest() {
        this.extendedInt = new Random().nextInt(15);
        this.parsedInt = -1;
        this.handlerRegistry = new SimpleMessageHandlerRegistry();
        handlerRegistry.registerAll(this);
        this.packetManager.parse(TestGenericMessage.GenericMessage.newBuilder().setExtension(TestExtendingMessage.ExtendingMessage.extendingMessage, TestExtendingMessage.ExtendingMessage.newBuilder().setNumericalValue(extendedInt).build()).build(), this.handlerRegistry);
        Assert.assertEquals("handle() incorrectly parsed ExtendingMessage (or got corrupt ExtendingMessage)", extendedInt, parsedInt);
    }

    /**
     * Test to verify that {@link SimplePacketManager#build(com.google.protobuf.Message)} is properly building messages
     * with extensions.
     */
    @Test
    public void buildTest() {
        this.extendedInt = new Random().nextInt(15);
        this.parsedInt = -1;
        parsedInt = packetManager.build(TestExtendingMessage.ExtendingMessage.newBuilder().setNumericalValue(extendedInt).build()).getExtension(TestExtendingMessage.ExtendingMessage.extendingMessage).getNumericalValue();
        Assert.assertEquals("build() did not return an equivalent message", extendedInt, parsedInt);
    }

    /**
     * Handler used in {@link #handlerParseTest()} and {@link #handlerRunTest()}.
     *
     * @param message The message to parse.
     * @throws HandlerException Currently never thrown.
     */
    @Handler
    public void handle(TestExtendingMessage.ExtendingMessage message) throws HandlerException {
        this.handlerHasRun = true;
        this.parsedInt = message.getNumericalValue();
    }
}
