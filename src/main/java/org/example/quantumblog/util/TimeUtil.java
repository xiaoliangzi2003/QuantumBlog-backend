package org.example.quantumblog.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author xiaol
 */
public class TimeUtil {
    public String timeStampToString(long timeStamp){
        Instant createInstant = Instant.ofEpochMilli(timeStamp);
        LocalDateTime createLocalDateTime = LocalDateTime.ofInstant(createInstant, ZoneId.systemDefault());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return createLocalDateTime.format(dateTimeFormatter);
    }
}
