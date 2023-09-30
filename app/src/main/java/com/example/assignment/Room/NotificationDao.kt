package com.example.assignment.Room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface NotificationDao {
    @Insert
    fun insert(notification: NotificationEntity)

    @Update
    fun update(notification: NotificationEntity)

    @Delete
    fun delete(notification: NotificationEntity)

    @Query("SELECT * FROM notifications ORDER BY id DESC")
    fun getAllNotifications(): LiveData<List<NotificationEntity>>
}