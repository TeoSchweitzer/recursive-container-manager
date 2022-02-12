package com.example.recursivecontainermanager.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.recursivecontainermanager.databinding.MainMenuFragmentBinding

class MainMenuFragment: DialogFragment() {
    private var _binding: MainMenuFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = MainMenuFragmentBinding.inflate(layoutInflater, null, false)

        binding.signInButton.setOnClickListener { signIn() }
        binding.signUpButton.setOnClickListener { signUp() }
        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .setPositiveButton("ok") { _, _ -> }
            .create()
    }

    private fun signIn() { SignInFragment().show(parentFragmentManager, tag) }
    private fun signUp() { SignUpFragment().show(parentFragmentManager, tag) }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}