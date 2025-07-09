package com.example.yumi.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.yumi.R
import com.example.yumi.databinding.ItemCategoryCardBinding
import com.example.yumi.models.data.Category // Assuming this path, will create Category.kt later if needed
import com.example.yumi.utils.ColorUtils // Assuming this path, will create ColorUtils.kt later
import android.animation.ObjectAnimator
import android.view.animation.OvershootInterpolator
import android.view.View // Added for View.GONE

class CategoryAdapter(
    private val onCategoryClick: (Category) -> Unit
) : ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoryViewHolder(
        private val binding: ItemCategoryCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // Scale animation on click
                    animateClick()
                    onCategoryClick(getItem(position))
                }
            }
        }

        fun bind(category: Category) {
            with(binding) {
                // Set category name
                categoryName.text = category.name

                // Set item count
                // Assuming Category model has itemCount, completedCount, previewImages
                itemCount.text = "${category.itemCount} magical wishes"

                // Set progress
                val progress = if (category.itemCount > 0) {
                    category.completedCount.toFloat() / category.itemCount.toFloat() * 100
                } else {
                    0f
                }
                progressRing.setProgress(progress.toInt()) // Assuming progressRing is a custom view with setProgress
                progressText.text = "${progress.toInt()}%"

                // Set category icon animation
                val animationRes = getCategoryAnimation(category.icon)
                categoryIconAnimation.setAnimation(animationRes) // Assuming this is a LottieAnimationView
                categoryIconAnimation.playAnimation()

                // Set live background based on category
                liveBackground.setCategory(category.icon)


                // Set preview images
                loadPreviewImages(category)

                // Apply color theme - Color is handled by CategoryLiveBackground or glass_morphism_background in the XML.
                // No explicit setCardBackgroundColor here.


                // Add sparkle effect on bind
                sparkleOverlay.startSparkle()
            }
        }

        private fun getCategoryAnimation(icon: String): Int {
            // Placeholder for actual Lottie raw resources
            return when (icon) {
                "travel" -> R.raw.dream_animation // Placeholder, replace with R.raw.category_travel if available
                "food" -> R.raw.dream_animation   // Placeholder, replace with R.raw.category_food if available
                "books" -> R.raw.dream_animation  // Placeholder, replace with R.raw.category_books if available
                "movies" -> R.raw.dream_animation // Placeholder, replace with R.raw.category_movies if available
                else -> R.raw.dream_animation
            }
        }

        private fun loadPreviewImages(category: Category) {
            val previews = listOf(binding.preview1, binding.preview2, binding.preview3)

            // Assuming category.previewImages is List<String?> or similar
            val images = category.previewImages ?: emptyList()

            images.take(3).forEachIndexed { index, imageUrl ->
                if (index < previews.size && imageUrl != null) {
                    previews[index].visibility = View.VISIBLE
                    Glide.with(binding.root.context)
                        .load(imageUrl)
                        .circleCrop()
                        .placeholder(R.drawable.ic_doodle_star) // Ensure ic_doodle_star is present
                        .into(previews[index])
                } else if (index < previews.size) {
                     previews[index].visibility = View.GONE
                }
            }

            // Hide unused preview images
            for (i in images.size until previews.size) {
                if (i < previews.size) previews[i].visibility = View.GONE
            }
        }

        private fun animateClick() {
            val scaleX = ObjectAnimator.ofFloat(binding.root, "scaleX", 1f, 0.95f, 1f)
            val scaleY = ObjectAnimator.ofFloat(binding.root, "scaleY", 1f, 0.95f, 1f)

            scaleX.interpolator = OvershootInterpolator()
            scaleY.interpolator = OvershootInterpolator()

            scaleX.duration = 300
            scaleY.duration = 300

            scaleX.start()
            scaleY.start()
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            // Ensure Category is a data class or implements equals()
            return oldItem == newItem
        }
    }
}
