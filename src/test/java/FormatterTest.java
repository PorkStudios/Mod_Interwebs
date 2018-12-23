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

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.daporkchop.interwebs.util.Util;
import org.junit.Test;

import java.util.Comparator;

/**
 * @author DaPorkchop_
 */
public class FormatterTest {
    @Test
    public void test() {
        Long2ObjectMap<String> map = new Long2ObjectOpenHashMap<String>() {
            {
                this.put(0L, "0");
                this.put(999L, "999");
                this.put(1000L, "1.0k");
                this.put(1040L, "1.0k");
                this.put(1060L, "1.0k");
                this.put(1099L, "1.0k");
                this.put(1100L, "1.1k");
                this.put(1000000L, "1.0M");
                this.put(1000000000L, "1.0B");
                this.put(1000000000000L, "1.0T");
                this.put(9999999999999L, "9.9T");
                this.put(1000000000000000L, "1.0P");
                this.put(1000000000000000000L, "1.0E");
                this.put(Long.MAX_VALUE, "9.2E");
            }
        };
        map.long2ObjectEntrySet().stream()
                .sorted(Comparator.comparingLong(Long2ObjectMap.Entry::getLongKey))
                .forEach(entry -> {
                    long key = entry.getLongKey();
                    String val = entry.getValue();
                    String formatted = Util.valueOf(key);
                    if (formatted.equals(val)) {
                        System.out.printf("% 20d -> %s\n", key, formatted);
                    } else {
                        throw new IllegalStateException(String.format("Invalid formatting of %d! Expected '%s' but found '%s'!", key, val, formatted));
                    }
                });
    }
}
