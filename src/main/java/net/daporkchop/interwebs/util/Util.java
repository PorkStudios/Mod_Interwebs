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

import com.google.common.base.Equivalence;
import com.google.common.cache.CacheBuilder;
import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.binary.UTF8;

import java.lang.reflect.Field;

import static java.lang.Math.floor;

/**
 * @author DaPorkchop_
 */
public class Util {
    private static final String[] suffixes = {
            "",  //nothing
            "k", //thousands    (kilo)
            "M", //millions     (mega)
            "B", //billions     (giga)
            "T", //trillions    (tera)
            "P", //quadrillions (peta)
            "E"  //quintillions (exa)
    };

    //TODO: make this a lot better
    public static String valueOf(long count)    {
        if (count < 1000L)  {
            return String.valueOf(count);
        }
        int size = 1;
        long power = 1000L;
        while (count / power > 10L)   {
            size++;
            power *= 1000L;
        }
        return String.format("%.1f%s", floor((double) count / (double) power * 10.0d) * 0.1d, suffixes[size]);
    }

    public static void writeUTF(@NonNull ByteBuf buf, @NonNull String s)   {
        writeBytes(buf, s.getBytes(UTF8.utf8));
    }

    public static void writeBytes(@NonNull ByteBuf buf, @NonNull byte[] b) {
        buf.writeInt(b.length);
        buf.writeBytes(b);
    }

    public static String readUTF(@NonNull ByteBuf buf)  {
        return new String(readBytes(buf), UTF8.utf8);
    }

    public static byte[] readBytes(@NonNull ByteBuf buf)    {
        byte[] b = new byte[buf.readInt()];
        buf.readBytes(b);
        return b;
    }

    public static <K, V> CacheBuilder<K, V> setKeyEquivalence(@NonNull CacheBuilder<K, V> builder, @NonNull Equivalence<Object> equivalence) {
        try {
            Field field = CacheBuilder.class.getDeclaredField("keyEquivalence");
            field.setAccessible(true);
            field.set(builder, equivalence);
        } catch (IllegalAccessException
                | NoSuchFieldException e)   {
            throw new RuntimeException(e);
        }
        return builder;
    }
}
