package com.example.bookproject.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.Date;

@Dao
public interface BookingDao {
    @Insert
    void insertBooking(Booking booking);

    @Query("SELECT * FROM bookings WHERE userId = :userId AND status != 'DIBATALKAN' AND endTime > :currentTime LIMIT 1")
    Booking getCurrentBooking(String userId, Date currentTime);

    @Query("UPDATE bookings SET status = :status WHERE id = :bookingId")
    void updateBookingStatus(long bookingId, String status);

    @Query("DELETE FROM bookings WHERE id = :bookingId")
    void deleteBooking(long bookingId);

    @Query("UPDATE bookings SET status = 'DIGUNAKAN' WHERE id = :bookingId")
    void markAsUsed(long bookingId);
}

