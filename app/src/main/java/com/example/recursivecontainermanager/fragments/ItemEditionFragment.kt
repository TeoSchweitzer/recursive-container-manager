package com.example.recursivecontainermanager.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.recursivecontainermanager.data.entities.Item
import com.example.recursivecontainermanager.databinding.ItemEditionFragmentBinding

class ItemEditionFragment: DialogFragment() {
    private var _binding: ItemEditionFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = ItemEditionFragmentBinding.inflate(layoutInflater, null, false)
        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .setTitle("Item Edition")
            .setPositiveButton("Confirm", null)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setupForAddition(parent: Item) {
        TODO("Not yet implemented")
    }

    fun setupForEdition(item: Item) {
        TODO("Not yet implemented")
    }
}