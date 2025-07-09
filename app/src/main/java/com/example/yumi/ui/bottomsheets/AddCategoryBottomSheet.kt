package com.example.yumi.ui.bottomsheets

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
// import android.widget.Toast // Not used in provided code
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels // Ensure this dependency is available
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView // Explicit import
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.yumi.R
import com.example.yumi.databinding.BottomSheetAddCategoryBinding // Assuming this binding class name
import com.example.yumi.models.data.Category // Assuming this path
import com.example.yumi.viewmodels.CategoryViewModel // Assuming this path, will create later

class AddCategoryBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddCategoryBinding? = null
    private val binding get() = _binding!!

    // Assuming CategoryViewModel exists and is setup for Hilt/manual injection
    private val viewModel: CategoryViewModel by viewModels()

    private val colors = listOf(
        "#87CEEB", "#FFB366", "#90EE90", "#E6E6FA",
        "#FFB6C1", "#98FB98", "#DDA0DD", "#F0E68C",
        "#B0E0E6", "#FFDAB9", "#D8BFD8", "#AFEEEE"
    )

    private val icons = listOf(
        "travel", "food", "books", "movies", "music",
        "sports", "shopping", "health", "education",
        "hobby", "work", "custom"
    )

    private var selectedColor = colors[0]
    private var selectedIcon = icons[0]

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Assuming layout file is res/layout/bottom_sheet_add_category.xml
        _binding = BottomSheetAddCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupListeners()
        setupAnimations()
    }

    private fun setupUI() {
        // Setup color picker
        binding.colorPickerRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 6)
            // Assuming item_color_picker.xml layout exists
            adapter = ColorPickerAdapter(colors) { color ->
                selectedColor = color
                updatePreview()
            }
        }

        // Setup icon picker
        binding.iconPickerRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 4)
            // Assuming item_icon_picker.xml layout exists and IconPickerAdapter is implemented
            adapter = IconPickerAdapter(icons) { icon ->
                selectedIcon = icon
                updatePreview()
            }
        }

        // Initial preview
        updatePreview()
    }

    private fun setupListeners() {
        // Category name input
        binding.categoryNameInput.addTextChangedListener { text ->
            binding.createButton.isEnabled = !text.isNullOrBlank()
            updatePreview()
        }

        // Create button
        binding.createButton.setOnClickListener {
            createCategory()
        }

        // Cancel button
        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }

    private fun setupAnimations() {
        // Slide up animation
        binding.root.translationY = 100f
        binding.root.alpha = 0f

        binding.root.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(300)
            .start()
    }

    private fun updatePreview() {
        binding.previewCard.setCardBackgroundColor(android.graphics.Color.parseColor(selectedColor))
        binding.previewIcon.setImageResource(getIconResource(selectedIcon)) // previewIcon is an ImageView
        binding.previewName.text = binding.categoryNameInput.text.toString().ifEmpty { "Category Name" }
    }

    private fun getIconResource(icon: String): Int {
        // These need to be actual drawable resources
        return when (icon) {
            "travel" -> R.drawable.ic_travel // Already provided
            "food" -> R.drawable.ic_doodle_star // Placeholder - needs ic_category_food
            "books" -> R.drawable.ic_doodle_star // Placeholder - needs ic_category_books
            "movies" -> R.drawable.ic_doodle_star // Placeholder - needs ic_category_movies
            // Add other cases for music, sports, etc.
            else -> R.drawable.ic_doodle_star // Default placeholder
        }
    }

    private fun createCategory() {
        val name = binding.categoryNameInput.text.toString().trim()

        if (name.isNotEmpty()) {
            // Assuming Category data class has id, name, color, icon, and potentially other fields like itemCount, completedCount
            val category = Category(
                id = 0, // Or generate appropriately if not auto-generated by Room
                name = name,
                color = selectedColor,
                icon = selectedIcon,
                itemCount = 0, // Default for new category
                completedCount = 0, // Default for new category
                previewImages = emptyList() // Default for new category
            )

            viewModel.addCategory(category) // Assuming CategoryViewModel has addCategory method

            // Success animation
            // Assuming successAnimation is a LottieAnimationView in bottom_sheet_add_category.xml
            binding.successAnimation.apply {
                visibility = View.VISIBLE
                playAnimation()

                addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationEnd(anim: Animator) { // Corrected parameter name
                        dismiss()
                    }
                    override fun onAnimationStart(anim: Animator) {} // Corrected parameter name
                    override fun onAnimationCancel(anim: Animator) {} // Corrected parameter name
                    override fun onAnimationRepeat(anim: Animator) {} // Corrected parameter name
                })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Color Picker Adapter (Should be in its own file or as a static nested class if preferred)
class ColorPickerAdapter(
    private val colors: List<String>,
    private val onColorSelected: (String) -> Unit
) : RecyclerView.Adapter<ColorPickerAdapter.ColorViewHolder>() {

    private var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        // Assuming R.layout.item_color_picker exists
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_color_picker, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(colors[position], position == selectedPosition)
    }

    override fun getItemCount() = colors.size

    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Assuming item_color_picker.xml has R.id.colorView and R.id.checkMark
        private val colorView: View = itemView.findViewById(R.id.colorView)
        private val checkMark: View = itemView.findViewById(R.id.checkMark)

        init {
            itemView.setOnClickListener {
                val oldPosition = selectedPosition
                selectedPosition = bindingAdapterPosition
                if (oldPosition != RecyclerView.NO_POSITION) notifyItemChanged(oldPosition)
                if (selectedPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(selectedPosition)
                    onColorSelected(colors[selectedPosition])
                }
            }
        }

        fun bind(color: String, isSelected: Boolean) {
            colorView.setBackgroundColor(android.graphics.Color.parseColor(color))
            checkMark.visibility = if (isSelected) View.VISIBLE else View.GONE

            if (isSelected) {
                itemView.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200).start()
            } else {
                itemView.animate().scaleX(1f).scaleY(1f).setDuration(200).start()
            }
        }
    }
}

// Icon Picker Adapter (Should be in its own file or as a static nested class)
// Needs full implementation, similar to ColorPickerAdapter
// It will inflate R.layout.item_icon_picker and display icons.
class IconPickerAdapter(
    private val icons: List<String>,
    private val onIconSelected: (String) -> Unit
) : RecyclerView.Adapter<IconPickerAdapter.IconViewHolder>() {

    private var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        // Assuming R.layout.item_icon_picker exists
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_icon_picker, parent, false)
        return IconViewHolder(view)
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        holder.bind(icons[position], position == selectedPosition)
    }

    override fun getItemCount(): Int = icons.size

    inner class IconViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Assuming item_icon_picker.xml has an ImageView R.id.iconView and a View R.id.checkMarkIcon
        private val iconView: android.widget.ImageView = itemView.findViewById(R.id.iconView)
        private val checkMark: View = itemView.findViewById(R.id.checkMarkIcon)


        init {
            itemView.setOnClickListener {
                val oldPosition = selectedPosition
                selectedPosition = bindingAdapterPosition
                if (oldPosition != RecyclerView.NO_POSITION) notifyItemChanged(oldPosition)
                if (selectedPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(selectedPosition)
                    onIconSelected(icons[selectedPosition])
                }
            }
        }

        fun bind(iconName: String, isSelected: Boolean) {
            // This needs to map iconName to a drawable resource
            iconView.setImageResource(getIconResource(iconName))
            checkMark.visibility = if (isSelected) View.VISIBLE else View.GONE

            if (isSelected) {
                itemView.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200).start()
            } else {
                itemView.animate().scaleX(1f).scaleY(1f).setDuration(200).start()
            }
        }

        private fun getIconResource(icon: String): Int {
            // Duplicated from AddCategoryBottomSheet, ideally this logic is centralized or passed in
            return when (icon) {
                "travel" -> R.drawable.ic_travel
                "food" -> R.drawable.ic_doodle_star // Placeholder
                "books" -> R.drawable.ic_doodle_star // Placeholder
                "movies" -> R.drawable.ic_doodle_star // Placeholder
                // Add other cases
                else -> R.drawable.ic_doodle_star
            }
        }
    }
}
