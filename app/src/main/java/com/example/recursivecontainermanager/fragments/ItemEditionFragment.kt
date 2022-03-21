package com.example.recursivecontainermanager.fragments

import android.app.Dialog
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.recursivecontainermanager.data.entities.Item
import com.example.recursivecontainermanager.databinding.ItemEditionFragmentBinding
import com.example.recursivecontainermanager.viewmodel.MainViewModel
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException

class ItemEditionFragment: DialogFragment() {
    private var _binding: ItemEditionFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = ItemEditionFragmentBinding.inflate(layoutInflater, null, false)
        binding.importButton.setOnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            if (!clipboard.hasPrimaryClip())
                Toast.makeText(context,"No data in Clipboard",Toast.LENGTH_LONG).show()
            else if (!clipboard.primaryClipDescription!!.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))
                Toast.makeText(context,"Invalid data type in clipboard: a JSON string is required",Toast.LENGTH_LONG).show()
            else {
                try {
                    setItem(viewModel.importItem(clipboard.primaryClip!!.getItemAt(0).text.toString()))
                } catch (e: JsonProcessingException) {
                    Toast.makeText(context, "Text in clipboard is not JSON",Toast.LENGTH_LONG).show()}
                catch (e: JsonMappingException) {
                    Toast.makeText(context, "JSON in clipboard is not in Item format",Toast.LENGTH_LONG).show()}
                catch (e: MissingKotlinParameterException) {
                    Toast.makeText(context, "At least a name and owner must be specified",Toast.LENGTH_LONG).show()}
            }
        }
        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .setTitle("Item Edition")
            .setPositiveButton("Confirm", null)
            .create()
    }

    fun setupForAddition() {
        binding.itemOwnersField.setText(viewModel.currentUser)
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
        setItem(item)
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

    private fun setItem(item: Item) {
        binding.itemNameField.setText(item.name)
        binding.itemOwnersField.setText(joinList(item.owners))
        binding.itemSubOwnersField.setText(joinList(item.subOwners))
        binding.itemReadOnlyField.setText(joinList(item.readonly))
        binding.itemTagsField.setText(joinList(item.tags))
        binding.itemPositionField.setText(item.position)
    }

    private fun joinList(list: List<String>?): String {
        if (list == null) return ""
        return list.joinToString(", ")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}