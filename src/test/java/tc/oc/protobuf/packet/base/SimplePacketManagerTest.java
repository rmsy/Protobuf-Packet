package tc.oc.protobuf.packet.base;

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
 * File created at: 3/17/13 12:17 AM
 */
@RunWith(JUnit4.class)
public class SimplePacketManagerTest implements MessageListener {
    private MessageHandlerRegistry handlerRegistry;
    private PacketManager<TestGenericMessage.GenericMessage> packetManager;
    private int extendedInt;
    private int parsedInt;
    private boolean handlerHasRun;
    private boolean handlerHasParsed;

    @Before
    public void initialize() {
        this.handlerRegistry = new SimpleMessageHandlerRegistry();
        handlerRegistry.registerAll(this);
        this.packetManager = new SimplePacketManager<>(TestGenericMessage.GenericMessage.getDefaultInstance());
        this.handlerHasRun = false;
        this.handlerHasParsed = false;
        this.extendedInt = new Random().nextInt(15);
        this.parsedInt = -1;
    }

    @Test
    public void handlerRunTest() throws InterruptedException {
        this.packetManager.parse(TestGenericMessage.GenericMessage.getDefaultInstance(), this.handlerRegistry);
        Assert.assertTrue("handle() did not run", handlerHasRun);
    }

    @Test
    public void handlerNullCheckTest() {
        this.packetManager.parse(TestGenericMessage.GenericMessage.getDefaultInstance(), this.handlerRegistry);
        Assert.assertFalse("handle() continued even with a null ExtendingMessage", handlerHasParsed);
    }

    @Test
    public void handlerParseTest() {
        this.packetManager.parse(TestGenericMessage.GenericMessage.newBuilder().setExtension(TestExtendingMessage.ExtendingMessage.extendingMessage, TestExtendingMessage.ExtendingMessage.newBuilder().setNumericalValue(extendedInt).build()).build(), this.handlerRegistry);
        Assert.assertEquals("handle() incorrectly parsed ExtendingMessage (or got corrupt ExtendingMessage)", extendedInt, parsedInt);
    }

    public void buildTest() {
        //  TODO: Implementation.
    }

    @Handler
    public void handle(TestGenericMessage.GenericMessage message) throws HandlerException {
        TestExtendingMessage.ExtendingMessage extendingMessage = (TestExtendingMessage.ExtendingMessage) message.getExtension(TestExtendingMessage.ExtendingMessage.extendingMessage);
        if (extendingMessage != null) {
            this.parsedInt = extendingMessage.getNumericalValue();
            this.handlerHasParsed = true;
        }
        this.handlerHasRun = true;
    }
}
