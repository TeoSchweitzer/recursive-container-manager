package com.example.recursivecontainermanager.fragments

import android.app.Service
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recursivecontainermanager.adapters.ItemListAdapter
import com.example.recursivecontainermanager.data.entities.Item
import com.example.recursivecontainermanager.databinding.SearchFragmentBinding
import com.example.recursivecontainermanager.viewmodel.MainViewModel


class SearchFragment: Fragment() {
    private var _binding: SearchFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inf: LayoutInflater, container: ViewGroup?, bundle: Bundle?): View {
        _binding = SearchFragmentBinding.inflate(inf, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.nameSearchField.addTextChangedListener(nameSearchWatcher)
        binding.tagFilterField.addTextChangedListener(tagSearchWatcher)
        viewModel.itemSearch.observe(requireActivity()) { list ->
            binding.searchRecycler.adapter = ItemListAdapter(list, {viewModel.changeCurrentItem(it)}, false)
        }
    }

    fun focusSearchBar() {
        binding.nameSearchField.requestFocus()
        (requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
            .showSoftInput(binding.nameSearchField, InputMethodManager.SHOW_IMPLICIT)
    }

    private val nameSearchWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun afterTextChanged(p0: Editable?) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            viewModel.newSearchFilter(s.toString(), binding.tagFilterField.text.toString())
        }
    }

    private val tagSearchWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun afterTextChanged(p0: Editable?) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            viewModel.newSearchFilter(binding.nameSearchField.text.toString(), s.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}