// Category.kt
package com.example.yumi

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val color: String,
    val icon: String,
    val itemCount: Int = 0,
    val completedCount: Int = 0,
    val previewImages: List<String?> = emptyList()
)
