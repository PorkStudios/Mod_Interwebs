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
import net.daporkchop.interwebs.interweb.Interweb;
import net.daporkchop.interwebs.interweb.Interwebs;
import net.daporkchop.interwebs.util.stack.StackIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.compress.archivers.sevenz.CLI;

import java.util.UUID;

/**
 * Sent to add an item type to a network and/or update the count
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
@NoArgsConstructor
public class PacketItemData implements IMessage {
    public long count;
    @NonNull
    public StackIdentifier identifier;
    @NonNull
    public UUID networkId;

    @Override
    public void fromBytes(@NonNull ByteBuf buf) {
        this.count = buf.readLong();
        this.identifier = StackIdentifier.read(buf);
        this.networkId = new UUID(buf.readLong(), buf.readLong());
    }

    @Override
    public void toBytes(@NonNull ByteBuf buf) {
        buf.writeLong(this.count);
        this.identifier.write(buf);
        buf.writeLong(this.networkId.getMostSignificantBits());
        buf.writeLong(this.networkId.getLeastSignificantBits());
    }

    public static class Handler implements IMessageHandler<PacketItemData, IMessage>    {
        @Override
        public IMessage onMessage(PacketItemData message, MessageContext ctx) {
            if (message.identifier == null) {
                Minecraft.getMinecraft().player.sendMessage(new TextComponentString("\u00A7cReceived invalid item identifier!"));
            } else {
                Interweb interweb = Interwebs.getInstance(Side.CLIENT).getLoaded(message.networkId);
                if (interweb == null) {
                    Minecraft.getMinecraft().player.sendMessage(new TextComponentString(String.format("\u00A7cReceived invalid network id: %s", message.networkId)));
                } else {
                    interweb.getInventory().getCountAtomic(message.identifier).set(message.count);
                }
            }
            return null;
        }
    }
}
