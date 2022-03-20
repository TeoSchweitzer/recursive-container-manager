package com.example.recursivecontainermanager.fragments

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.recursivecontainermanager.data.entities.Item
import com.example.recursivecontainermanager.databinding.ItemEditionFragmentBinding
import com.example.recursivecontainermanager.viewmodel.MainViewModel

class ItemEditionFragment: DialogFragment() {
    private var _binding: ItemEditionFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

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

    fun setupForAddition() {
        val d = dialog as AlertDialog?
        val positiveButton: Button = d!!.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            viewModel.addItem(
                binding.itemNameField.text.toString(),
                binding.itemOwnersField.text.toString(),
                binding.itemSubOwnersField.text.toString(),
                binding.itemReadOnlyField.text.toString(),
                binding.itemTagsField.text.toString(),
                binding.itemPositionField.text.toString()
            )
        }
    }

    fun setupForEdition(item: Item) {
        binding.itemNameField.setText(item.name)
        binding.itemOwnersField.setText(joinList(item.owners))
        binding.itemSubOwnersField.setText(joinList(item.subOwners))
        binding.itemReadOnlyField.setText(joinList(item.readonly))
        binding.itemTagsField.setText(joinList(item.tags))
        binding.itemPositionField.setText(item.position)

        val d = dialog as AlertDialog?
        val positiveButton: Button = d!!.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            viewModel.alterItem(
                binding.itemNameField.text.toString(),
                binding.itemOwnersField.text.toString(),
                binding.itemSubOwnersField.text.toString(),
                binding.itemReadOnlyField.text.toString(),
                binding.itemTagsField.text.toString(),
                binding.itemPositionField.text.toString()
            )
        }
    }

    private fun joinList(list: List<String>?): String {
        if (list == null) return ""
        return list.joinToString(", ")
    }
}