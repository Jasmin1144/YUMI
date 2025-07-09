package com.example.yumi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.yumi.R
import com.example.yumi.databinding.ItemNotebookPageBinding // Assuming this binding class
import com.example.yumi.models.data.WishlistItem // Assuming this path
import com.example.yumi.models.enums.MediaType // Assuming this path

class NotebookPageAdapter(
    private val onItemClick: (WishlistItem) -> Unit,
    private val onItemLongClick: (WishlistItem) -> Boolean
) : ListAdapter<WishlistItem, NotebookPageAdapter.PageViewHolder>(WishlistDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        // Assuming res/layout/item_notebook_page.xml exists
        val binding = ItemNotebookPageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind(getItem(position))

        // Add page entrance animation
        // Assuming R.anim.fade_in_up exists
        holder.itemView.animation = AnimationUtils.loadAnimation(
            holder.itemView.context,
            R.anim.fade_in_up
        )
    }

    inner class PageViewHolder(
        private val binding: ItemNotebookPageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }

            binding.root.setOnLongClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemLongClick(getItem(position))
                } else {
                    false
                }
            }
        }

        fun bind(item: WishlistItem) {
            with(binding) {
                // Set title and description
                itemTitle.text = item.title
                itemDescription.text = item.description ?: "A magical wish waiting to come true..."

                // Handle media display
                when (item.mediaType) {
                    MediaType.IMAGE -> {
                        mediaImageView.visibility = View.VISIBLE
                        placeholderAnimation.visibility = View.GONE // Assuming placeholderAnimation is a Lottie view

                        Glide.with(root.context)
                            .load(item.mediaPath)
                            .centerCrop()
                            .placeholder(R.drawable.gradient_ghibli_sky) // Ensure this drawable exists
                            .error(R.drawable.ic_doodle_star) // Ensure this drawable exists
                            .into(mediaImageView)
                    }
                    MediaType.VIDEO -> {
                        // For video, show thumbnail
                        mediaImageView.visibility = View.VISIBLE
                        placeholderAnimation.visibility = View.GONE

                        Glide.with(root.context)
                            .load(item.mediaPath) // Needs to be a path Glide can get a frame from
                            .frame(1000000) // 1 second frame for thumbnail
                            .centerCrop()
                            .placeholder(R.drawable.gradient_ghibli_sky)
                            .into(mediaImageView)
                    }
                    MediaType.NONE -> {
                        mediaImageView.visibility = View.GONE
                        placeholderAnimation.visibility = View.VISIBLE
                        placeholderAnimation.playAnimation() // Assuming it's a LottieAnimationView
                    }
                }

                // Add completed state
                // Assuming WishlistItem has isCompleted field and item_notebook_page.xml has completedStamp (Lottie?) and dateText (TextView)
                if (item.isCompleted) {
                    root.alpha = 0.7f
                    // completedStamp.visibility = View.VISIBLE
                    // completedStamp.playAnimation()
                    // Check if completedStamp is part of ItemNotebookPageBinding, if not, this will error.
                    // For now, I'll assume it might not be in the initial XML and comment it.
                } else {
                    root.alpha = 1f
                    // completedStamp.visibility = View.GONE
                }

                // Add date
                // dateText.text = formatDate(item.createdAt)
                // Check if dateText is part of ItemNotebookPageBinding.
                // For now, I'll assume it might not be in the initial XML and comment it.
            }
        }

        private fun formatDate(timestamp: Long): String {
            val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
            return dateFormat.format(java.util.Date(timestamp))
        }
    }

    class WishlistDiffCallback : DiffUtil.ItemCallback<WishlistItem>() {
        override fun areItemsTheSame(oldItem: WishlistItem, newItem: WishlistItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WishlistItem, newItem: WishlistItem): Boolean {
            // Ensure WishlistItem is a data class or implements equals()
            return oldItem == newItem
        }
    }
}
