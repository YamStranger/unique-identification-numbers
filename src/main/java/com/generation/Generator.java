package com.generation;

import java.util.UUID;

/**
 * User: YamStranger
 * Date: 4/24/15
 * Time: 12:33 AM
 */
public class Generator {

    /**
     * Generates random string, with length 10 digits.
     *
     * @return string with random chars[0-9A-Z]
     */
    public String generate() {
        StringBuilder builder = new StringBuilder();
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        for (int j = 0; j < uuid.length(); j += 4) {
            String value = Long.toString(Long.parseLong(uuid.substring(j, j + 4)
                    , 16), 36);

            if (value.length() == 1) {
                builder.append(0);
            }
            builder.append(value);
        }
        while (builder.charAt(0) == '0') {
            builder.deleteCharAt(0);
        }
        if (builder.length() < 10) {
            builder.append(generate());
        }
        return builder.substring(0, 10).toString().toUpperCase();
    }

}

