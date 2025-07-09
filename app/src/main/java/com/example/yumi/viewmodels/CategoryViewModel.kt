package com.example.yumi.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.yumi.models.data.Category // Assuming this path

// AndroidViewModel provides Application context if needed, otherwise use ViewModel
class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "CategoryViewModel"

    fun addCategory(category: Category) {
        // TODO: Implement actual logic to add category (e.g., save to Room database)
        Log.d(TAG, "addCategory called with: ${category.name}, Color: ${category.color}, Icon: ${category.icon}")
        // For now, just logging. Later, this would interact with a repository/database.
    }

    // TODO: Add other ViewModel functionalities:
    // - LiveData for observing categories
    // - Methods to fetch, update, delete categories
}
