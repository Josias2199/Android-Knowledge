package com.joxs.knowledge.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import coil.load
import coil.transform.CircleCropTransformation
import com.joxs.knowledge.R
import com.joxs.knowledge.data.network.Resource
import com.joxs.knowledge.databinding.FragmentProfileBinding
import com.joxs.knowledge.presentation.ProfileViewModel
import com.joxs.knowledge.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

private const val FILE_NAME = "photo.jpg"
private const val CAMERA_PERMISSION_TAG = "cameraPermission"
private const val GALLERY_PERMISSION_TAG = "galleryPermission"
private const val PERMISSION_DENIED_TAG = "deniedPermission"

@AndroidEntryPoint
class ProfileFragment : Fragment(), PermissionListener {

    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var binding: FragmentProfileBinding
    private lateinit var imageBytes: ByteArray
    private val _isUpdatedImage = MutableLiveData<Boolean>()
    private val isUpdatedImage: LiveData<Boolean> get() = _isUpdatedImage
    private lateinit var requestCameraPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var requestGalleryPermissionLauncher: ActivityResultLauncher<String>
    private val galleryPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
    private val cameraPermission = Manifest.permission.CAMERA
    private lateinit var photoFile: File

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{

        binding = FragmentProfileBinding.inflate(inflater, container, false)

        uploadImage()
        getCurrentImageProfile()
        setupPermissionLauncher()

        return binding.root
    }

    private fun requestPermissionDialog(
        image: Int,
        description: String,
        TAG: String
    ){
        val dialog = RequestPermissionDialog.newInstance(image, description)
        dialog.setListener(this)
        dialog.isCancelable = false
        dialog.show(requireActivity().supportFragmentManager, TAG)
    }

    private fun requestPermission(
        permission: String,
        permissionTag: String,
    ){
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                when(permissionTag){
                    CAMERA_PERMISSION_TAG -> resultLauncherCameraImage.launch(cameraImageIntent())
                    GALLERY_PERMISSION_TAG -> resultLauncherGalleryImage.launch(galleryImageIntent())
                }
            }
            shouldShowRequestPermissionRationale(permission) -> {
                requestPermissionDialog(
                    image = R.drawable.placeholder_photo,
                    description = getString(R.string.msg_request_image_permission),
                    TAG = permissionTag
                )
            }
            else -> {
                requestPermissionDialog(
                    image = R.drawable.placeholder_photo,
                    description = getString(R.string.msg_request_image_permission),
                    TAG = permissionTag
                )
            }
        }
    }

    private fun setupPermissionLauncher(){
        requestCameraPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    resultLauncherCameraImage.launch(cameraImageIntent())
                } else {
                    requestPermissionDialog(
                        image = R.drawable.placeholder_error,
                        description = getString(R.string.msg_request_image_permission_denied),
                        TAG = PERMISSION_DENIED_TAG
                    )

                }
            }
        requestGalleryPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                resultLauncherGalleryImage.launch(galleryImageIntent())
            } else {
                requestPermissionDialog(
                    image = R.drawable.placeholder_error,
                    description = getString(R.string.msg_request_image_permission_denied),
                    TAG = PERMISSION_DENIED_TAG
                )

            }
        }
    }

    private fun uploadImage(){
        isUpdatedImage.observe(viewLifecycleOwner){ isUpdate ->
            if (isUpdate)
                uploadImage(imageBytes)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnCamera.setOnClickListener {
            requestPermission(cameraPermission, CAMERA_PERMISSION_TAG)
        }
        binding.btnGallery.setOnClickListener {
            requestPermission(galleryPermission, GALLERY_PERMISSION_TAG)
        }
    }

    private fun galleryImageIntent(): Intent {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        return  intent
    }

    private val resultLauncherGalleryImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.let { selectedImageIntent ->
                    selectedImageIntent.let {
                        try {
                            val inputStream: InputStream? = activity!!.contentResolver.openInputStream(it.data!!)
                            val bitmapImage: Bitmap = BitmapFactory.decodeStream(inputStream)
                            encodeBytesImage(bitmapImage)
                        }catch (e: Exception){
                            showToast(getString(R.string.msg_could_not_get_image))
                        }
                    }
                }
            }
        }

    private fun uploadImage(imageBytes: ByteArray){
        viewModel.uploadImage(imageBytes).observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> { binding.pbImage.show() }
                is Resource.Success -> {
                    showToast(getString(R.string.msg_image_uploaded_successfully))
                    binding.pbImage.hide()
                    getCurrentImageProfile()
                }
                is Resource.Error -> {
                    showToast(getString(R.string.msg_error_loading_image))
                    binding.pbImage.hide()
                }
            }
        })
    }

    private fun getCurrentImageProfile(){
        viewModel.getUpdateImage().observe(viewLifecycleOwner){
            binding.ivProfile.load(it){
                transformations(CircleCropTransformation())
            }
        }
    }

    private fun cameraImageIntent(): Intent {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = getPhotoFile()
        val fileProvider =
            FileProvider.getUriForFile(requireContext(), "com.joxs.knowledge.fileprovider", photoFile)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
        return takePictureIntent
    }

    private fun getPhotoFile(): File {
        val storageDirectory = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(FILE_NAME, ".jpg", storageDirectory)
    }


    private val resultLauncherCameraImage  = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val takenPhoto = BitmapFactory.decodeFile(photoFile.absolutePath)
            encodeBytesImage(takenPhoto)
        }
    }

    private fun encodeBytesImage(bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        imageBytes = data
        uploadImage(imageBytes)
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        when(dialog.tag){
            CAMERA_PERMISSION_TAG -> requestCameraPermissionLauncher.launch(cameraPermission)
            GALLERY_PERMISSION_TAG -> requestGalleryPermissionLauncher.launch(galleryPermission)
        }
    }

}