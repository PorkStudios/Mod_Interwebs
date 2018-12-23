/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.interwebs.net.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

/**
 * Sends an item to the interweb inventory
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
@NoArgsConstructor
public class PacketSendItem implements IMessage {
    public int slot; //the slot in the player's inventory
    public int count; //the number of items to be transferred
    public UUID networkId; //the ID of the network that the item(s) will be added to

    @Override
    public void fromBytes(@NonNull ByteBuf buf) {
        this.slot = buf.readInt();
        this.count = buf.readInt();

        this.networkId = new UUID(buf.readLong(), buf.readLong());
    }

    @Override
    public void toBytes(@NonNull ByteBuf buf) {
        buf.writeInt(this.slot);
        buf.writeInt(this.count);

        buf.writeLong(this.networkId.getMostSignificantBits());
        buf.writeLong(this.networkId.getLeastSignificantBits());
    }

    public static class Handler implements IMessageHandler<PacketSendItem, IMessage>    {
        @Override
        public IMessage onMessage(PacketSendItem message, MessageContext ctx) {
            //i don't care if this is handled concurrently, everything interweb-related is thread-safe!
            //TODO: the current behaviour could be exploited to allow users to send items to a network without physical access to a terminal

            return null;
        }
    }
}
