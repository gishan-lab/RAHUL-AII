package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "smart_reminders")
data class ReminderEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val timeLabel: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
