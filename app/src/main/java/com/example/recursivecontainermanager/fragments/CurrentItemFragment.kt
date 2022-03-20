package com.example.recursivecontainermanager.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.recursivecontainermanager.R
import com.example.recursivecontainermanager.data.entities.Token
import com.example.recursivecontainermanager.databinding.CurrentItemFragmentBinding
import com.example.recursivecontainermanager.viewmodel.MainViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.days


class CurrentItemFragment: Fragment() {
    private var _binding: CurrentItemFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CurrentItemFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (viewModel.currentItem.value == null) setBlankItem()
        else setCurrentItem()
        binding.tokenSharingButton.setOnClickListener { shareToken() }
        binding.jsonSharingButton.setOnClickListener { shareText(viewModel.itemJsonFormat()) }
        binding.qrcodeSharingButton.setOnClickListener { shareQRCode() }
        binding.addItemButton.setOnClickListener { itemAddition() }
        binding.alterItemButton.setOnClickListener { itemEdition() }
        binding.deleteItemButton.setOnClickListener { deleteItem() }
    }

    private fun shareToken() {
        var checkedTime = Date().time
        var checkedOwnership = requireContext().resources.getStringArray(R.array.token_ownership)[2]

        AlertDialog.Builder(requireActivity())
            .setSingleChoiceItems(R.array.token_durations, 0) { _, which ->
                when (which) {
                    0 -> checkedTime = Date().time +1.days.inWholeMilliseconds
                    1 -> checkedTime = Date().time +3.days.inWholeMilliseconds
                    2 -> checkedTime = Date().time +7.days.inWholeMilliseconds
                    3 -> checkedTime = Date().time +30.days.inWholeMilliseconds
                }
            }
            .setSingleChoiceItems(R.array.token_ownership, 2) { _, which ->
                checkedOwnership = requireContext().resources.getStringArray(R.array.token_ownership)[which]
            }
            .setNeutralButton("Generate") {_,_->viewModel.sendNewToken(checkedTime, checkedOwnership)}
            .create().show()
    }

    private fun shareText(text: String) {
        if (text.isBlank()) return
        startActivity(Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }, null))
    }

    private fun shareQRCode() {
        val qrCode = viewModel.getQrCode(context ?:requireContext())
        AlertDialog.Builder(requireActivity())
            .setView(qrCode).setNeutralButton("Share") { _, _ ->

                val icon: Bitmap = qrCode.drawToBitmap()
                val uriToImage = requireContext().externalCacheDir.toString() + File.separator + "qrCode.jpg"
                try {
                    val f = File(uriToImage)
                    f.createNewFile()
                    val bytes = ByteArrayOutputStream()
                    icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                    val fo = FileOutputStream(f)
                    fo.write(bytes.toByteArray())
                } catch (e: IOException) {
                    Toast.makeText(context, "Saving QR Code Failed", Toast.LENGTH_LONG).show()
                }
                /* Partage marche pas
                    startActivity(Intent.createChooser(Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_STREAM, uriToImage)
                        type = "image/*"
                    }, "Share QR Code"))
                */*/
                AlertDialog.Builder(requireContext())
                    .setTitle("QR code location").setMessage(uriToImage).create().show()

            }
            .create()
            .show()
    }

    private fun setBlankItem() {
        binding.itemNameText.text = getString(R.string.blank_item_text)
        binding.itemOwnerText.visibility = View.INVISIBLE
        binding.itemTagsText.visibility = View.INVISIBLE
        binding.itemTokensText.visibility = View.INVISIBLE
    }

    private fun setCurrentItem() {
        val item = viewModel.currentItem.value!!
        binding.itemNameText.text = item.name
        binding.itemOwnerText.text = item.owners.joinToString(", ")
        if (item.tags == null)
            binding.itemTagsText.text = getString(R.string.no_tags_text)
        else
            binding.itemTagsText.text = item.tags!!.joinToString(", ")
        if (item.tokens == null)
            binding.itemTagsText.text = getString(R.string.no_tokens_text)
        else
            binding.itemTokensText.text = formatTokens(item.tokens!!)
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatTokens(tokens: List<Token>): String {
        var result = ""
        for (token in tokens) {
            result += "${token.tokenCode} (until ${SimpleDateFormat("yyyy/MM/dd").format(Date(token.end))}): for ${token.authorizationType},\n"
        }
        return result
    }

    private fun itemAddition() {
        if (viewModel.currentItem.value == null) return
        val frag = ItemEditionFragment()
        frag.showNow(parentFragmentManager, null)
        frag.setupForAddition()
    }

    private fun itemEdition() {
        if (viewModel.currentItem.value == null) return
        val frag = ItemEditionFragment()
        frag.showNow(parentFragmentManager, null)
        frag.setupForEdition(viewModel.currentItem.value!!)
    }

    private fun deleteItem() {
        AlertDialog.Builder(requireActivity())
            .setMessage("Are you sure want to delete this item, including its content if it has any ?")
            .setPositiveButton("Confirm") {_,_-> viewModel.deleteItem()}
            .setNegativeButton("Cancel", null)
            .create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}