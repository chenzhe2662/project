package util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String[] SIZE_NAMES = {"B","KB","MB","GB"};
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);


    public static String parseSize(Long size) {
        int count = 0;
        while (size >= 1024) {
            size = size/1024;
            count++;
        }
        return  size + SIZE_NAMES[count];
    }

    public static String paraseDate(Long lastModified) {
        return DATE_FORMAT.format(new Date(lastModified));
    }
}
