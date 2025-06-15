package com.example.bookproject.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "bookings")
public class Booking {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String userId;
    private String deviceId;
    private String deviceName;
    private Date startTime;
    private Date endTime;
    private String status;

    // Constructor
    public Booking(String userId, String deviceId, String deviceName, Date startTime, Date endTime, String status) {
        this.userId = userId;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    // Getter dan Setter
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }

    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
