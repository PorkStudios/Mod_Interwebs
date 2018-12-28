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

package net.daporkchop.interwebs;

import com.mojang.authlib.GameProfile;
import lombok.NonNull;
import net.daporkchop.interwebs.block.BlockTerminal;
import net.daporkchop.interwebs.block.InterwebsBlocks;
import net.daporkchop.interwebs.gui.GuiProxy;
import net.daporkchop.interwebs.interweb.Interweb;
import net.daporkchop.interwebs.interweb.Interwebs;
import net.daporkchop.interwebs.net.PacketHandler;
import net.daporkchop.interwebs.proxy.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author DaPorkchop_
 */
@Mod(
        modid = ModInterwebs.MOD_ID,
        name = ModInterwebs.MOD_NAME,
        version = ModInterwebs.VERSION
)
public class ModInterwebs {
    public static final String MOD_ID = "interwebs";
    public static final String MOD_NAME = "The Interwebs";
    public static final String VERSION = "0.0.1";

    public Interwebs interwebs_serverInstance;
    public Interwebs interwebs_clientInstance;

    @Mod.Instance(MOD_ID)
    public static ModInterwebs INSTANCE;

    @SidedProxy(
            clientSide = "net.daporkchop.interwebs.proxy.ClientProxy",
            serverSide = "net.daporkchop.interwebs.proxy.ServerProxy"
    )
    public static CommonProxy proxy;

    public static Logger logger;

    @Mod.EventHandler
    public void preInit(@NonNull FMLPreInitializationEvent event) {
        logger = event.getModLog();
        proxy.preInit(event);

        PacketHandler.register(MOD_ID);
    }

    @Mod.EventHandler
    public void init(@NonNull FMLInitializationEvent event) {
        proxy.init(event);

        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new GuiProxy());
    }

    @Mod.EventHandler
    public void postInit(@NonNull FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

}
