package net.daporkchop.interwebs.net.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Sends an item to the interweb inventory
 *
 * @author DaPorkchop_
 */
public class PacketSendItem implements IMessage {
    public int slot;
    public int count;

    @Override
    public void fromBytes(ByteBuf buf) {
        this.slot = buf.readInt();
        this.count = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.slot);
        buf.writeInt(this.count);
    }

    public static class Handler implements IMessageHandler<PacketSendItem, IMessage>    {
        @Override
        public IMessage onMessage(PacketSendItem message, MessageContext ctx) {
            //i don't care if this is handled concurrently, everything interweb-related is thread-safe!
            return null;
        }
    }
}
