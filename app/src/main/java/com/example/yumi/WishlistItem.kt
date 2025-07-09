// WishlistItem.kt
package com.example.yumi

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.yumi.models.enums.MediaType // Updated import

@Entity(tableName = "wishlist_items")
data class WishlistItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val categoryId: Int,
    val title: String,
    val description: String?,
    val mediaPath: String?,
    val mediaType: MediaType,
    val createdAt: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false
)
