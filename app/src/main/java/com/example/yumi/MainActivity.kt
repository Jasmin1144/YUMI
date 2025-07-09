package com.example.yumi

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.yumi.adapters.CategoryAdapter // Corrected import
import com.example.yumi.companion.YumiCompanion
import com.example.yumi.databinding.ActivityMainBinding
import com.example.yumi.models.data.Category // Corrected import

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var categoryAdapter: CategoryAdapter // Made non-nullable
    private lateinit var companion: YumiCompanion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        loadCategories()
        setupAnimations()
        setupCompanion()
    }

    private fun setupUI() {
        // Setup RecyclerView
        categoryAdapter = CategoryAdapter { category ->
            openNotebook(category)
        }

        binding.categoriesRecyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = categoryAdapter

            // Add item decoration for spacing
            addItemDecoration(com.example.yumi.utils.GridSpacingItemDecoration(2, 16, true))
        }

        // FAB click listener
        binding.fabAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }
    }

    private fun loadCategories() {
        // Load from database or create default categories
        // Ensure Category model fields (itemCount, completedCount, previewImages) are handled if these are loaded from DB
        val defaultCategories = listOf(
            Category(id = 1, name = "Travel", color = "#87CEEB", icon = "travel", itemCount = 5, completedCount = 2, previewImages = listOf(null, null, null)),
            Category(id = 2, name = "Food", color = "#FFB366", icon = "food", itemCount = 3, completedCount = 1, previewImages = listOf(null, null)),
            Category(id = 3, name = "Books", color = "#90EE90", icon = "books", itemCount = 10, completedCount = 0, previewImages = emptyList()),
            Category(id = 4, name = "Movies", color = "#E6E6FA", icon = "movies", itemCount = 2, completedCount = 2, previewImages = listOf(null))
        )

        categoryAdapter?.submitList(defaultCategories) // Used safe call due to nullable type
    }

    private fun setupAnimations() {
        // Title animation
        val titleAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_down)
        binding.appTitle.startAnimation(titleAnimation)

        // Subtitle animation
        val subtitleAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_up)
        binding.subtitle.startAnimation(subtitleAnimation)

        // FAB entrance animation
        binding.fabAddCategory.apply {
            scaleX = 0f
            scaleY = 0f
            animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(500)
                .setStartDelay(300)
                .setInterpolator(androidx.interpolator.view.animation.FastOutSlowInInterpolator())
                .start()
        }
    }

    private fun openNotebook(category: Category) {
        val intent = Intent(this, NotebookActivity::class.java).apply {
            putExtra("category_id", category.id)
            putExtra("category_name", category.name)
        }

        // Custom transition animation
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun showAddCategoryDialog() {
        // Implementation for adding new category
        com.example.yumi.ui.bottomsheets.AddCategoryBottomSheet().show(supportFragmentManager, "add_category_bottom_sheet")
    }

    private fun setupCompanion() {
        companion = YumiCompanion(this)
        // The companion's LottieAnimationView is already in the XML (companionAnimation)
        // We need to replace it or add the one from YumiCompanion instance if it's dynamically created
        // For now, let's assume the XML one is the one to be used and we just update its animation.
        // If YumiCompanion.mascotView is meant to be added programmatically:
        // binding.companionContainer.addView(companion.mascotView)
        // However, YumiCompanion.mascotView itself sets an animation. Let's use the existing one.
        binding.companionAnimation.setAnimation(companion.mascotView.animation) // Use animation from companion's view
        binding.companionAnimation.playAnimation()


        val greeting = companion.appearWithGreeting()
        binding.companionSpeech.text = greeting
        binding.companionSpeech.visibility = View.VISIBLE
        binding.companionSpeech.alpha = 0f
        binding.companionSpeech.animate()
            .alpha(1f)
            .setDuration(300)
            .setStartDelay(500) // Delay a bit for app to load
            .withEndAction {
                // Hide speech bubble after a few seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.companionSpeech.animate()
                        .alpha(0f)
                        .setDuration(300)
                        .withEndAction { binding.companionSpeech.visibility = View.GONE }
                        .start()
                }, 3000)
            }
            .start()

        // Example of reacting to an action (e.g. when FAB is clicked)
        // This should be tied to actual user actions like adding a wish.
        // For now, let's simulate an ADD_WISH action when fabAddCategory is clicked.
        binding.fabAddCategory.setOnClickListener {
            showAddCategoryDialog() // Original action
            // companion.reactToUserAction(com.example.yumi.models.enums.UserAction.ADD_WISH)
            // Note: reactToUserAction in YumiCompanion logs and tries to show a reaction.
            // We need a callback mechanism from YumiCompanion to MainActivity to show the reaction speech.
            // For now, this call will log. The speech bubble logic needs to be centralized.
        }
    }
}