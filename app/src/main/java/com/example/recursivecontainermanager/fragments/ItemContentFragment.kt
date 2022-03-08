package com.example.recursivecontainermanager.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recursivecontainermanager.databinding.ItemContentFragmentBinding

class ItemContentFragment:  Fragment() {
    private var _binding: ItemContentFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater:LayoutInflater, container:ViewGroup?, bundle:Bundle?): View {
        _binding = ItemContentFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.nameTagFilterField.addTextChangedListener(textWatcher)
        //viewModel.itemContentList.observe(requireActivity()) {
        //    binding.itemContentRecycler.adapter = ItemListAdapter(it, ::viewModel.changeCurrentItem, false)
        //}
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun afterTextChanged(p0: Editable?) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val input = s.toString()
            //traiter input
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}