package net.daporkchop.interwebs.util;

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
}
