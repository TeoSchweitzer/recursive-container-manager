package com.example.recursivecontainermanager.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.allViews
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.recursivecontainermanager.databinding.EnterTokenFragmentBinding
import com.example.recursivecontainermanager.viewmodel.MainViewModel

class EnterTokenFragment: DialogFragment() {

    private var _binding: EnterTokenFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = EnterTokenFragmentBinding.inflate(layoutInflater, null, false)
        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .setTitle("Enter Access Code")
            .setPositiveButton("Confirm") {_,_->viewModel.getToken(binding.accessCodeField.text.toString())}
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}