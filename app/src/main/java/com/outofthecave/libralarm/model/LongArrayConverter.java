package com.outofthecave.libralarm.model;

import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.room.TypeConverter;

public class LongArrayConverter {
    private static final String DELIMITER = ",";
    private static final Pattern DELIMITER_PATTERN = Pattern.compile(Pattern.quote(DELIMITER));

    @NonNull
    @TypeConverter
    public static String longArrayToString(@NonNull long[] longArray) {
        StringBuilder sb = new StringBuilder();
        sb.append(longArray.length);
        sb.append(DELIMITER);
        for (long number : longArray) {
            sb.append(number);
            sb.append(DELIMITER);
        }
        return sb.toString();
    }

    @NonNull
    @TypeConverter
    public static long[] toLongArray(@NonNull String string) {
        Scanner scanner = new Scanner(string).useDelimiter(DELIMITER_PATTERN);
        try {
            int length = scanner.nextInt();

            long[] longArray = new long[length];
            for (int i = 0; i < length; ++i) {
                longArray[i] = scanner.nextLong();
            }
            return longArray;

        } catch (NoSuchElementException e) {
            return new long[0];
        }
    }
}
