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

package net.daporkchop.interwebs.proxy;

import lombok.NonNull;
import net.daporkchop.interwebs.ModInterwebs;
import net.daporkchop.interwebs.interweb.Interwebs;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author DaPorkchop_
 */
@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    @SubscribeEvent
    public static void onJoin(@NonNull PlayerEvent.PlayerLoggedInEvent event) {
        logger.debug("Logged in! World: %s, remote: %b\n", event.player.world, event.player.world != null && event.player.world.isRemote);
        ModInterwebs.INSTANCE.interwebs_clientInstance = new Interwebs(null);
    }

    @SubscribeEvent
    public static void onLeave(@NonNull PlayerEvent.PlayerLoggedOutEvent event) {
        logger.debug("Logged out! World: %s, remote: %b\n", event.player.world, event.player.world != null && event.player.world.isRemote);
        ModInterwebs.INSTANCE.interwebs_clientInstance = null;
    }

    @SubscribeEvent
    public static void registerModels(@NonNull ModelRegistryEvent event) {
    }
}
