package com.example.recursivecontainermanager.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.recursivecontainermanager.adapters.ItemListAdapter
import com.example.recursivecontainermanager.databinding.ItemContentFragmentBinding
import com.example.recursivecontainermanager.viewmodel.MainViewModel

class ItemContentFragment:  Fragment() {
    private var _binding: ItemContentFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater:LayoutInflater, container:ViewGroup?, bundle:Bundle?): View {
        _binding = ItemContentFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.nameTagFilterField.addTextChangedListener(textWatcher)
        binding.minRecursionButton.setOnClickListener  {
            viewModel.newContentFilter(-2, binding.nameTagFilterField.text.toString())
        }
        binding.lessRecursionButton.setOnClickListener {
            viewModel.newContentFilter(-1, binding.nameTagFilterField.text.toString())
        }
        binding.moreRecursionButton.setOnClickListener {
            viewModel.newContentFilter(+1, binding.nameTagFilterField.text.toString())
        }
        binding.maxRecursionButton.setOnClickListener  {
            viewModel.newContentFilter(+2, binding.nameTagFilterField.text.toString())
        }

        viewModel.recursion.observe(requireActivity()) {
            binding.recursionLevelText.text = it.toString()
        }
        viewModel.itemContent.observe(requireActivity()) { list ->
            binding.itemContentRecycler.adapter = ItemListAdapter(list, {viewModel.changeCurrentItem(it)}, false)
        }
        viewModel.currentItem.observe(viewLifecycleOwner) {
            binding.nameTagFilterField.setText("")
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun afterTextChanged(p0: Editable?) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            viewModel.newContentFilter(0, s.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}