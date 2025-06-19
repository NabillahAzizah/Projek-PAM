package com.example.bookproject.model;

public class Booking {
    private String bookingId;
    private int playstationId;
    private String playstationName;
    private String userEmail;
    private String userId;
    private String bookingDate;
    private String timeRange;
    private String duration;
    private int sessionNumber;
    private String status; // BOOKED, IN_USE, CANCELLED, EXPIRED
    private long createdAt;
    private long expiresAt; // createdAt + 10 minutes

    public Booking() {
        // Default constructor required for Firebase
    }

    public Booking(int playstationId, String playstationName, String userEmail, String userId,
                   String bookingDate, String timeRange, String duration, int sessionNumber) {
        this.playstationId = playstationId;
        this.playstationName = playstationName;
        this.userEmail = userEmail;
        this.userId = userId;
        this.bookingDate = bookingDate;
        this.timeRange = timeRange;
        this.duration = duration;
        this.sessionNumber = sessionNumber;
        this.status = "BOOKED";
        this.createdAt = System.currentTimeMillis();
        this.expiresAt = this.createdAt + (10 * 60 * 1000); // 10 minutes
    }

    // Getters and Setters
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public int getPlaystationId() { return playstationId; }
    public void setPlaystationId(int playstationId) { this.playstationId = playstationId; }

    public String getPlaystationName() { return playstationName; }
    public void setPlaystationName(String playstationName) { this.playstationName = playstationName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }

    public String getTimeRange() { return timeRange; }
    public void setTimeRange(String timeRange) { this.timeRange = timeRange; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public int getSessionNumber() { return sessionNumber; }
    public void setSessionNumber(int sessionNumber) { this.sessionNumber = sessionNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getExpiresAt() { return expiresAt; }
    public void setExpiresAt(long expiresAt) { this.expiresAt = expiresAt; }

    // Helper method to check if booking is expired
    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt && "BOOKED".equals(status);
    }

    // Helper method to get remaining time in minutes
    public long getRemainingMinutes() {
        if (isExpired()) return 0;
        return (expiresAt - System.currentTimeMillis()) / (60 * 1000);
    }
}