/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
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
import net.daporkchop.interwebs.net.PacketHandler;
import net.daporkchop.interwebs.util.CoolAtomicLong;
import net.daporkchop.interwebs.util.stack.StackIdentifier;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

/**
 * @author DaPorkchop_
 */
@AllArgsConstructor
@NoArgsConstructor
public class PacketRequestItem implements IMessage {
    public int targetSlot;
    @NonNull
    public StackIdentifier identifier;
    @NonNull
    public UUID networkId;

    @Override
    public void fromBytes(@NonNull ByteBuf buf) {
        this.targetSlot = buf.readInt();
        this.identifier = StackIdentifier.read(buf);
        this.networkId = new UUID(buf.readLong(), buf.readLong());
    }

    @Override
    public void toBytes(@NonNull ByteBuf buf) {
        buf.writeInt(this.targetSlot);
        this.identifier.write(buf);
        buf.writeLong(this.networkId.getMostSignificantBits());
        buf.writeLong(this.networkId.getLeastSignificantBits());
    }

    public static class Handler implements IMessageHandler<PacketRequestItem, IMessage> {
        @Override
        public IMessage onMessage(PacketRequestItem message, MessageContext ctx) {
            //TODO: the current behaviour could be exploited to allow users to receive items from a network without physical access to a terminal
            if (message.identifier == null) {
                ctx.getServerHandler().disconnect(new TextComponentString("Received invalid item!"));
            } else {
                Interweb interweb = Interwebs.getInstance(Side.SERVER).getFastOrPossiblyLoad(message.networkId);
                if (interweb == null) {
                    ctx.getServerHandler().disconnect(new TextComponentString(String.format("Received invalid network: %s", message.networkId)));
                } else {
                    CoolAtomicLong count = interweb.getInventory().getCountAtomic(message.identifier);
                    if (count != null && count.get() >= 0L) {
                        ItemStack stack = message.identifier.getAsItemStack();
                        int maxStackSize = message.identifier.getItem().getItemStackLimit(stack);
                        int size = (int) (count.get() - count.updateAndGet(l -> l - Math.min(l, maxStackSize)));
                        stack.setCount(size);
                        FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
                            if (message.targetSlot == -999) {
                                ctx.getServerHandler().player.inventory.setItemStack(stack);
                            } else if (message.targetSlot == -1) {
                                ctx.getServerHandler().player.inventory.addItemStackToInventory(stack);
                            } else {
                                ctx.getServerHandler().player.inventory.setInventorySlotContents(message.targetSlot, stack);
                            }
                            ctx.getServerHandler().player.sendContainerToPlayer(ctx.getServerHandler().player.inventoryContainer);
                            PacketHandler.INSTANCE.sendTo(new PacketItemData(count.get(), message.identifier, message.networkId), ctx.getServerHandler().player);
                        });
                        interweb.markDirty();
                    }
                }
            }
            return null;
        }
    }
}
