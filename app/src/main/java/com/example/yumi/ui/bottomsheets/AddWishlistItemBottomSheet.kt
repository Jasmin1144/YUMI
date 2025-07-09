package com.example.yumi.ui.bottomsheets

import android.Manifest
import android.animation.Animator // Import for Animator.AnimatorListener
// import android.app.Activity // Not directly used
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
// import android.provider.MediaStore // Not directly used
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels // Ensure this dependency is available
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.yumi.R
import com.example.yumi.databinding.BottomSheetAddWishlistItemBinding // Assuming this binding class name
import com.example.yumi.models.data.WishlistItem // Assuming this path
import com.example.yumi.models.enums.MediaType // Assuming this path
import com.example.yumi.viewmodels.WishlistViewModel // Assuming this path, will create later
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddWishlistItemBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddWishlistItemBinding? = null
    private val binding get() = _binding!!

    // Assuming WishlistViewModel exists and is setup for Hilt/manual injection
    private val viewModel: WishlistViewModel by viewModels()

    private var categoryId: Int = 0
    private var selectedMediaUri: Uri? = null
    private var selectedMediaType: MediaType = MediaType.NONE
    private var currentPhotoPath: String? = null

    companion object {
        private const val ARG_CATEGORY_ID = "category_id"

        fun newInstance(categoryId: Int): AddWishlistItemBottomSheet {
            return AddWishlistItemBottomSheet().apply {
                arguments = Bundle().apply {
                    putInt(ARG_CATEGORY_ID, categoryId)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryId = it.getInt(ARG_CATEGORY_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Assuming layout file is res/layout/bottom_sheet_add_wishlist_item.xml
        _binding = BottomSheetAddWishlistItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupListeners()
        setupAnimations()
    }

    private fun setupUI() {
        // Initial UI state
        binding.mediaPreviewCard.visibility = View.GONE
        binding.addButton.isEnabled = false // title is initially empty
    }

    private fun setupListeners() {
        // Title input listener
        binding.wishTitleInput.addTextChangedListener { text ->
            binding.addButton.isEnabled = !text.isNullOrBlank()
        }

        // Media buttons
        binding.cameraButton.setOnClickListener {
            checkCameraPermission()
        }

        binding.galleryButton.setOnClickListener {
            openGallery()
        }

        binding.removeMediaButton.setOnClickListener {
            removeSelectedMedia()
        }

        // Action buttons
        binding.addButton.setOnClickListener {
            addWishlistItem()
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }

    private fun setupAnimations() {
        // Entrance animation
        binding.root.translationY = 100f // Initial position for slide-up
        binding.root.alpha = 0f

        binding.root.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(300)
            .setInterpolator(androidx.interpolator.view.animation.FastOutSlowInInterpolator())
            .start()
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            showPermissionDeniedMessage()
        }
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                showPermissionRationale()
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            currentPhotoPath?.let { path ->
                selectedMediaUri = Uri.fromFile(File(path)) // URI from file path
                selectedMediaType = MediaType.IMAGE
                displaySelectedMedia()
            }
        }
    }

    private fun openCamera() {
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: Exception) {
            // Error occurred while creating the File
            null
        }
        photoFile?.also {
            currentPhotoPath = it.absolutePath
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider", // Ensure this matches manifest
                it
            )
            takePictureLauncher.launch(photoURI)
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireContext().getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "YUMI_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            // Save the path for later use if needed, though currentPhotoPath is already set
        }
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedMediaUri = it
            selectedMediaType = determineMediaType(it) // Corrected: determine type from URI
            displaySelectedMedia()
        }
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*,video/*") // Allow both image and video selection
    }

    private fun determineMediaType(uri: Uri): MediaType {
        val mimeType = requireContext().contentResolver.getType(uri)
        return when {
            mimeType?.startsWith("image") == true -> MediaType.IMAGE
            mimeType?.startsWith("video") == true -> MediaType.VIDEO
            else -> MediaType.NONE
        }
    }

    private fun displaySelectedMedia() {
        binding.mediaPreviewCard.visibility = View.VISIBLE
        binding.mediaPreviewCard.alpha = 0f // For animation
        binding.mediaPreviewCard.scaleX = 0.8f
        binding.mediaPreviewCard.scaleY = 0.8f

        when (selectedMediaType) {
            MediaType.IMAGE -> {
                Glide.with(this)
                    .load(selectedMediaUri)
                    .centerCrop()
                    .into(binding.mediaPreviewImage)
                binding.videoPlayIcon.visibility = View.GONE
            }
            MediaType.VIDEO -> {
                Glide.with(this)
                    .load(selectedMediaUri)
                    .frame(1000000) // 1 sec thumbnail
                    .centerCrop()
                    .into(binding.mediaPreviewImage)
                binding.videoPlayIcon.visibility = View.VISIBLE
            }
            MediaType.NONE -> { // Should not happen if media is selected
                binding.mediaPreviewCard.visibility = View.GONE
            }
        }

        binding.mediaPreviewCard.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(300)
            .start()
    }

    private fun removeSelectedMedia() {
        selectedMediaUri = null
        selectedMediaType = MediaType.NONE

        binding.mediaPreviewCard.animate()
            .alpha(0f)
            .scaleX(0.8f)
            .scaleY(0.8f)
            .setDuration(200)
            .withEndAction {
                binding.mediaPreviewCard.visibility = View.GONE
            }
            .start()
    }

    private fun addWishlistItem() {
        val title = binding.wishTitleInput.text.toString().trim()
        val description = binding.wishDescriptionInput.text.toString().trim()

        if (title.isNotEmpty()) {
            // Assuming WishlistItem data class matches these fields
            val wishlistItem = WishlistItem(
                id = 0, // Assuming Room will auto-generate
                categoryId = categoryId,
                title = title,
                description = description.ifEmpty { null },
                mediaPath = selectedMediaUri?.toString(), // Save URI as string
                mediaType = selectedMediaType,
                createdAt = System.currentTimeMillis(),
                isCompleted = false // Default for new item
            )

            viewModel.addWishlistItem(wishlistItem) // Assuming WishlistViewModel has this method

            binding.successAnimation.apply { // Assuming this Lottie view exists in the layout
                visibility = View.VISIBLE
                playAnimation()

                addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationEnd(animation: Animator) {
                        dismiss()
                    }
                    override fun onAnimationStart(animation: Animator) {}
                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                })
            }
        }
    }

    private fun showPermissionDeniedMessage() {
        // TODO: Show a snackbar or dialog
        android.widget.Toast.makeText(context, "Camera permission denied.", android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun showPermissionRationale() {
        // TODO: Show explanation dialog
         android.app.AlertDialog.Builder(requireContext())
            .setTitle("Permission Needed")
            .setMessage("Camera permission is needed to take photos for your wishes.")
            .setPositiveButton("OK") { _, _ ->
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
