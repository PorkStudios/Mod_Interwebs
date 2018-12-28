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
import net.daporkchop.interwebs.block.InterwebsBlocks;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * @author DaPorkchop_
 */
@Mod.EventBusSubscriber
public class CommonProxy {
    public static Logger logger;

    @SubscribeEvent
    public static void registerItems(@NonNull RegistryEvent.Register<Item> event) throws IllegalAccessException {
        for (Field field : InterwebsBlocks.class.getDeclaredFields()) {
            if (Block.class.isAssignableFrom(field.getType())) {
                Block block = (Block) field.get(null);
                ResourceLocation registryName = block.getRegistryName();
                if (registryName == null) {
                    throw new NullPointerException();
                }
                event.getRegistry().register(new ItemBlock(block).setRegistryName(registryName));
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void registerBlocks(@NonNull RegistryEvent.Register<Block> event) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        for (Field field : InterwebsBlocks.class.getDeclaredFields()) {
            try {
                if (Block.class.isAssignableFrom(field.getType())) {
                    Constructor<? extends Block> constructor = ((Class<? extends Block>) field.getType()).getConstructor();
                    event.getRegistry().register(constructor.newInstance());
                }
            } catch (NoSuchMethodException e) {
                //ignore
            }
        }
    }

    public void preInit(@NonNull FMLPreInitializationEvent event) {
        logger = ModInterwebs.logger;
    }

    public void init(@NonNull FMLInitializationEvent event) {
    }

    public void postInit(@NonNull FMLPostInitializationEvent event) {
    }
}
