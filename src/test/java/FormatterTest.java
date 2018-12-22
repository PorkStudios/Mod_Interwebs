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
