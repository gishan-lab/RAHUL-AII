package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_memories")
data class MemoryEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val content: String,
    val category: String = "general",
    val timestamp: Long = System.currentTimeMillis()
)
