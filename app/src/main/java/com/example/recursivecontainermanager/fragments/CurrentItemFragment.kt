package com.example.recursivecontainermanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.recursivecontainermanager.databinding.CurrentItemFragmentBinding

class CurrentItemFragment: Fragment() {
    private var _binding: CurrentItemFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CurrentItemFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.deleteItemButton.setOnClickListener { deleteItem() }
        binding.qrcodeSharingButton.setOnClickListener { showSharable(binding.qrcodeSharingButton) }
        binding.tokenSharingButton.setOnClickListener { showSharable(binding.tokenSharingButton) }
        binding.jsonSharingButton.setOnClickListener { showSharable(binding.jsonSharingButton) }
        binding.addItemButton.setOnClickListener { itemEdition(binding.addItemButton) }
        binding.alterItemButton.setOnClickListener { itemEdition(binding.alterItemButton) }
    }

    private fun itemEdition(pressedEditionButton: ImageButton) {
        ItemEditionFragment().show(parentFragmentManager, null)
    }

    private fun showSharable(pressedSharingButton: ImageButton) {
        AlertDialog.Builder(requireActivity())
                //for QR or JSON: add qr code or JSON from model
                //for tokens: add token settings option:.setMultiChoiceItems()
                //for tokens: also add a generate token button
            .setNeutralButton("Share", null)
            .create().show()
    }

    private fun deleteItem() {
        AlertDialog.Builder(requireActivity())
            .setMessage("Are you sure want to delete this item, including its content if it has any ?")
            .setPositiveButton("Confirm", null)
            .setNegativeButton("Cancel", null)
            .create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}