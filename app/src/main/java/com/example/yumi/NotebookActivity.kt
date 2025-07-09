package com.example.yumi

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.yumi.databinding.ActivityNotebookBinding

class NotebookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotebookBinding
    private lateinit var pageAdapter: com.example.yumi.adapters.NotebookPageAdapter // Corrected type
    private var currentCategoryId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotebookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentCategoryId = intent.getIntExtra("category_id", 0)
        val categoryName = intent.getStringExtra("category_name") ?: "Wishes"

        setupUI(categoryName)
        loadWishlistItems()
        setupPageTransformer()
    }

    private fun setupUI(categoryName: String) {
        binding.categoryTitle.text = "$categoryName Dreams"

        // pageAdapter = NotebookPageAdapter() // Instantiation with constructor params needed
        // Instantiating NotebookPageAdapter with click listeners
        pageAdapter = com.example.yumi.adapters.NotebookPageAdapter(
            onItemClick = { wish ->
                // TODO: Handle wish item click (e.g., open details)
                android.widget.Toast.makeText(this, "Clicked: ${wish.title}", android.widget.Toast.LENGTH_SHORT).show()
            },
            onItemLongClick = { wish ->
                // TODO: Handle wish item long click (e.g., show options menu)
                android.widget.Toast.makeText(this, "Long clicked: ${wish.title}", android.widget.Toast.LENGTH_SHORT).show()
                true // Return true if the event is consumed
            }
        )
        binding.notebookViewPager.adapter = pageAdapter

        // Vertical page flipping
        binding.notebookViewPager.orientation = ViewPager2.ORIENTATION_VERTICAL

        // FAB click listener
        binding.fabAddItem.setOnClickListener {
            showAddItemDialog()
        }

        // Swipe gesture for category switching
        binding.notebookContainer.setOnTouchListener(com.example.yumi.utils.SwipeGestureListener(this,
            onSwipeLeft = { switchToNextCategory() },
            onSwipeRight = { switchToPreviousCategory() }
        ))
    }

    private fun setupPageTransformer() {
        binding.notebookViewPager.setPageTransformer(PageFlipTransformer())
    }

    private fun loadWishlistItems() {
        // Load items from database
        // Using fully qualified name for WishlistItem and MediaType
        val items = listOf(
            com.example.yumi.models.data.WishlistItem(
                categoryId = currentCategoryId,
                title = "Visit Kyoto",
                description = "Walk through bamboo groves",
                mediaType = com.example.yumi.models.enums.MediaType.IMAGE,
                isCompleted = false // Added default for new field
            ),
            com.example.yumi.models.data.WishlistItem(
                categoryId = currentCategoryId,
                title = "Northern Lights",
                description = "See aurora borealis in Iceland",
                mediaType = com.example.yumi.models.enums.MediaType.NONE,
                isCompleted = true // Added default for new field
            )
        )

        pageAdapter.submitList(items) // pageAdapter is now non-null
    }

    private fun showAddItemDialog() {
        com.example.yumi.ui.bottomsheets.AddWishlistItemBottomSheet.newInstance(currentCategoryId)
            .show(supportFragmentManager, "add_wishlist_item_bottom_sheet")
    }

    private fun switchToNextCategory() {
        // Animate notebook sliding left
        binding.notebookContainer.animate()
            .translationX(-binding.notebookContainer.width.toFloat())
            .setDuration(300)
            .withEndAction {
                // Load next category
                finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            .start()
    }

    private fun switchToPreviousCategory() {
        // Similar animation for previous category
        // Animate notebook sliding right (example)
        binding.notebookContainer.animate()
            .translationX(binding.notebookContainer.width.toFloat())
            .setDuration(300)
            .withEndAction {
                // Load previous category
                finish()
                // Assuming slide_in_left and slide_out_right animations will be created or are standard.
                // For now, using existing ones as placeholders for transition effect.
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            }
            .start()
    }
}

// Custom Page Transformer for flip effect
class PageFlipTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.apply {
            when {
                position < -1 -> { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    alpha = 0f
                }
                position <= 0 -> { // [-1,0]
                    // Use the default slide transition when moving to the left page
                    alpha = 1f
                    translationY = 0f
                    translationZ = 0f
                    scaleX = 1f
                    scaleY = 1f

                    // Flip effect
                    rotationX = 90 * kotlin.math.abs(position) // Use abs for consistent rotation direction

                    // Adjust camera distance for better 3D effect
                    cameraDistance = 20000f * resources.displayMetrics.density // Adjust for density
                }
                position <= 1 -> { // (0,1]
                    // Fade the page out.
                    alpha = 1 - position

                    // Counteract the default slide transition
                    translationY = -position * height
                    translationZ = -1f // Keep pages from overlapping during animation

                    // Scale the page down (between MIN_SCALE and 1)
                    val scaleFactor = 0.75f + (1 - 0.75f) * (1 - kotlin.math.abs(position))
                    scaleX = scaleFactor
                    scaleY = scaleFactor

                    // Rotate the page for the flip effect
                    rotationX = 90 * kotlin.math.abs(position) // Use abs for consistent rotation direction
                    cameraDistance = 20000f * resources.displayMetrics.density // Adjust for density
                }
                else -> { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    alpha = 0f
                }
            }
        }
    }
}
