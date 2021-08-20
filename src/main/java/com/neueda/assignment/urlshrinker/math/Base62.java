package com.neueda.assignment.urlshrinker.math;

import java.util.List;

/**
 * Offers the capability to encode from decimal numbers into a representation of base 62 name, using the set of
 * available characters. This approach bring the simplicity of the numerical base conversion algorithm, while ensures
 * encoded values that are URL safe, hard to infer sequence and are impossible to clash.
 */
public class Base62 {

    public static final List<String> CHARACTERS = List.of(
        "D", "W", "f", "c", "8", "U", "M", "O", "K", "0", "a", "s", "T", "1", "Z", "v", "E", "F", "C", "w", "R", "m",
        "e", "j", "2", "3", "B", "J", "A", "z", "q", "7", "Q", "H", "o", "X", "V", "l", "n", "P", "L", "N", "b", "y",
        "4", "k", "Y", "9", "p", "g", "r", "5", "t", "d", "6", "I", "u", "G", "h", "x", "S", "i"
    );

    private Base62() { }

    /**
     * Encodes a decimal number into a base 62 representation, using the set of available characters. It employs the
     * traditional algorithm to convert numeric bases: The received value is divided by 62 and the remainder is
     * truncated to an integer that is used as offset to obtain the correspondent character from
     * {@link Base62#CHARACTERS} list. The process is repeated, now using the integer part of the aforementioned
     * division numerator, until the the obtained integer part of the division becomes equals to 0. This algorithm,
     * despite simple, results in a sequence that is unique, URL safe, impossible to clash, hard to infer its sequence
     * and fast enough to generate.
     *
     * @param value Value to be encoded. Must be a positive number.
     *
     * @return Received value encoded.
     */
    public static String encode(Long value) {
        if (value < CHARACTERS.size()) {
            return CHARACTERS.get(value.intValue());
        }

        double lastRemainder;
        long lastResult = value;
        StringBuilder result = new StringBuilder();

        do {
            lastRemainder = ((double)lastResult % 62.0D);
            lastResult = lastResult / 62;
            result.insert(0, CHARACTERS.get((int)lastRemainder));
        } while (lastResult > 0L);

        return result.toString();
    }

    /**
     * Decodes a prior encoded decimal number back to its decimal form.
     *
     * @param value Value to be decoded.
     *
     * @return Received value decoded to its decimal form.
     */
    public static Long decode(String value) {
        if (value.length() == 1) {
            return (long)CHARACTERS.indexOf(value);
        }

        long result = 0L;
        for (int index = 0; index < value.length(); index++) {
            double power = (double)value.length() - (index + 1);
            int baseNumber = CHARACTERS.indexOf(value.substring(index, index+1));
            result += baseNumber * (long)(Math.pow(62D, power));
        }
        return result;
    }

}
