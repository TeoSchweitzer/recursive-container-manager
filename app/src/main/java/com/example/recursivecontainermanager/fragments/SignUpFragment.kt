package com.example.recursivecontainermanager.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.recursivecontainermanager.databinding.SignUpFragmentBinding

class SignUpFragment: DialogFragment() {
    private var _binding: SignUpFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = SignUpFragmentBinding.inflate(layoutInflater, null, false)

        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .setPositiveButton("ok") { _, _ -> }
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}