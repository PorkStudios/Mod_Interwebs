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

package net.daporkchop.interwebs.util;

import io.netty.channel.ChannelFutureListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleIndexedCodec;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.reflect.Field;
import java.util.EnumMap;

/**
 * @author DaPorkchop_
 */
public class CoolAndSimpleNetworkWrapper extends SimpleNetworkWrapper {
    protected final EnumMap<Side, FMLEmbeddedChannel> real_channels;
    protected final SimpleIndexedCodec real_packetCodec;

    @SuppressWarnings("unchecked")
    public CoolAndSimpleNetworkWrapper(String channelName) {
        super(channelName);

        try {
            Field field;

            field = SimpleNetworkWrapper.class.getDeclaredField("channels");
            field.setAccessible(true);
            this.real_channels = (EnumMap<Side, FMLEmbeddedChannel>) field.get(this);

            field = SimpleNetworkWrapper.class.getDeclaredField("packetCodec");
            field.setAccessible(true);
            this.real_packetCodec = (SimpleIndexedCodec) field.get(this);
        } catch (NoSuchFieldException
                | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendToAllWithoutFlush(IMessage message) {
        this.real_channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
        this.real_channels.get(Side.SERVER).write(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void sendToWithoutFlush(IMessage message, EntityPlayerMP player) {
        this.real_channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
        this.real_channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
        this.real_channels.get(Side.SERVER).write(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void sendToAllAroundWithoutFlush(IMessage message, NetworkRegistry.TargetPoint point) {
        this.real_channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
        this.real_channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
        this.real_channels.get(Side.SERVER).write(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void sendToAllTrackingWithoutFlush(IMessage message, NetworkRegistry.TargetPoint point) {
        this.real_channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TRACKING_POINT);
        this.real_channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
        this.real_channels.get(Side.SERVER).write(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void sendToAllTrackingWithoutFlush(IMessage message, Entity entity) {
        this.real_channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TRACKING_ENTITY);
        this.real_channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(entity);
        this.real_channels.get(Side.SERVER).write(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void sendToDimensionWithoutFlush(IMessage message, int dimensionId) {
        this.real_channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
        this.real_channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimensionId);
        this.real_channels.get(Side.SERVER).write(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void sendToServerWithoutFlush(IMessage message) {
        this.real_channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        this.real_channels.get(Side.CLIENT).write(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }
}
