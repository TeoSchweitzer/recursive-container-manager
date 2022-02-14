package com.example.recursivecontainermanager.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.allViews
import androidx.fragment.app.DialogFragment
import com.example.recursivecontainermanager.databinding.EnterTokenFragmentBinding

class EnterTokenFragment: DialogFragment() {
    private var _binding: EnterTokenFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = EnterTokenFragmentBinding.inflate(layoutInflater, null, false)
        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .setTitle("Enter Access Code")
            .setPositiveButton("Confirm", null)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}