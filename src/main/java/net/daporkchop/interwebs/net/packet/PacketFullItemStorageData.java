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
import net.daporkchop.lib.binary.NettyByteBufUtil;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author DaPorkchop_
 */
@AllArgsConstructor
@NoArgsConstructor
public class PacketFullItemStorageData implements IMessage {
    @NonNull
    public Map<StackIdentifier, AtomicLong> values;
    @NonNull
    public UUID networkId;

    @Override
    public void fromBytes(@NonNull ByteBuf buf) {
        this.networkId = new UUID(buf.readLong(), buf.readLong());

        this.values = new HashMap<>();
        try (DataIn in = NettyByteBufUtil.wrapIn(buf)) {
            for (long l = buf.readLong() - 1L; l >= 0L; l--) {
                StackIdentifier identifier = StackIdentifier.read(in);
                long count = in.readLong();
                if (identifier != null) {
                    this.values.put(identifier, new AtomicLong(count));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void toBytes(@NonNull ByteBuf buf) {
        buf.writeLong(this.networkId.getMostSignificantBits());
        buf.writeLong(this.networkId.getLeastSignificantBits());

        buf.writeLong(this.values.size());
        try (DataOut out = NettyByteBufUtil.wrapOut(buf))   {
            for (Map.Entry<StackIdentifier, AtomicLong> entry : this.values.entrySet()) {
                entry.getKey().write(out);
                out.writeLong(entry.getValue().get());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Handler implements IMessageHandler<PacketFullItemStorageData, IMessage> {
        @Override
        public IMessage onMessage(PacketFullItemStorageData message, MessageContext ctx) {
            Interweb interweb = Interwebs.getInstance(Side.CLIENT).computeIfAbsent(message.networkId);
            interweb.getInventory().getStacks().clear();
            interweb.getInventory().getStacks().putAll(message.values);
            return null;
        }
    }
}
