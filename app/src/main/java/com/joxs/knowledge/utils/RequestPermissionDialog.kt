package com.joxs.knowledge.utils

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.joxs.knowledge.R
import com.joxs.knowledge.databinding.DialogRequestPermissionsBinding
import kotlin.properties.Delegates

private const val ARG_PERMISSION_IMAGE = "image"
private const val ARG_PERMISSION_DESCRIPTION = "description"


class RequestPermissionDialog : DialogFragment() {

    private lateinit var permissionListener: PermissionListener
    private lateinit var binding: DialogRequestPermissionsBinding
    private var image by Delegates.notNull<Int>()
    private var description: String? = null

    companion object {
        @JvmStatic
        fun newInstance(image: Int, description: String) =
            RequestPermissionDialog().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PERMISSION_IMAGE, image)
                    putString(ARG_PERMISSION_DESCRIPTION, description)
                }
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.apply {
            image = getInt(ARG_PERMISSION_IMAGE)
            description = getString(ARG_PERMISSION_DESCRIPTION)
        }
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog!!.window?.setLayout(
            width,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.backgroun_round)
        binding = DialogRequestPermissionsBinding.inflate(layoutInflater)

        binding.ivPermissionType.setImageResource(image)
        binding.txtPermissionDescription.text = description
        binding.btnAccept.setOnClickListener {
            dismiss()
            permissionListener.onDialogPositiveClick(this@RequestPermissionDialog)
        }

        return binding.root
    }

    fun setListener(permissionListener: PermissionListener){
        this.permissionListener = permissionListener
    }
}

interface PermissionListener {
    fun onDialogPositiveClick(dialog: DialogFragment)
}