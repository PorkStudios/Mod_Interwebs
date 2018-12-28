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

package net.daporkchop.interwebs.net;

import lombok.NonNull;
import net.daporkchop.interwebs.net.packet.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author DaPorkchop_
 */
public class PacketHandler {
    private static final AtomicInteger ID = new AtomicInteger(0);

    public static SimpleNetworkWrapper INSTANCE = null;

    public static void register(@NonNull String channelName)    {
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);

        //serverbound
        register(PacketSendItem.class, PacketSendItem.Handler.class, Side.SERVER);
        register(PacketBeginTrackingInterweb.class, PacketBeginTrackingInterweb.Handler.class, Side.SERVER);
        register(PacketStopTrackingInterweb.class, PacketStopTrackingInterweb.Handler.class, Side.SERVER);
        register(PacketRequestItem.class, PacketRequestItem.Handler.class, Side.SERVER);

        //clientbound
        register(PacketInterwebData.class, PacketInterwebData.Handler.class, Side.CLIENT);
        register(PacketItemData.class, PacketItemData.Handler.class, Side.CLIENT);
    }

    private static <P extends IMessage, R extends IMessage> void register(@NonNull Class<P> packetClass, @NonNull Class<? extends IMessageHandler<P, R>> handlerClass, @NonNull Side side) {
        INSTANCE.registerMessage(handlerClass, packetClass, ID.getAndIncrement(), side);
    }
}
