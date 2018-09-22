package com.zn.expirytracker.data.typeconv;

import android.arch.persistence.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringListTypeConverter {

    // https://stackoverflow.com/questions/599161/best-way-to-convert-an-arraylist-to-a-string
    @TypeConverter
    public static String fromStringList(List<String> strings) {
        if (strings == null) {
            // For when coming from RTD
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (String string : strings) {
            builder.append(string);
            builder.append("\t");
        }
        return builder.toString().trim();
    }

    // https://stackoverflow.com/questions/7347856/how-to-convert-a-string-into-an-arraylist
    @TypeConverter
    public static List<String> fromStrings(String string) {
        List<String> list = new ArrayList<>(Arrays.asList(string.split("\t")));
        if (list.size() == 1 && list.get(0).trim().isEmpty()) {
            // If there are no images, return an empty array list instead of a list with just an
            // empty string
            return new ArrayList<>();
        } else {
            return list;
        }
    }
}
