package com.example.IOT_HELL.payload.response;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.Timestamp;
import lombok.Data;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@Data
public class IoTDataGet {
    private Long id;
    private double latitude;
    private double longitude;

    @JsonIgnore
    private Timestamp createDate;

    @JsonProperty("createDate")
    public String getCreateDateFormatted() {
        if (createDate == null) return null;
        return formatTimestamp(createDate.toDate());
    }

    private String formatTimestamp(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Bangkok")); // ✅ เปลี่ยนเป็น UTC+7
        return sdf.format(date);
    }
}
