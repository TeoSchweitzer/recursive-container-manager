package com.example.recursivecontainermanager.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.allViews
import androidx.fragment.app.DialogFragment
import com.example.recursivecontainermanager.databinding.MainMenuFragmentBinding


open class MainMenuFragment: DialogFragment() {
    private var _binding: MainMenuFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = MainMenuFragmentBinding.inflate(layoutInflater, null, false)

        val b = binding
        b.showSignInButton.setOnClickListener {showViews(b.usernameField, b.passwordField) }
        b.showSignUpButton.setOnClickListener { showViews(b.usernameField, b.passwordField, b.passwordRepeatField) }
        b.showAddressButton.setOnClickListener { showViews(b.serverAddressField) }

        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .setTitle("Main Settings")
            .setPositiveButton("Confirm", null)
            .create()
    }

    private fun showViews(vararg views: View){
        binding.root.allViews.forEach {
            if (it.id != View.NO_ID && it.parent == binding.root) {
                if (!views.contains(it)) it.visibility = View.GONE
                else it.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}