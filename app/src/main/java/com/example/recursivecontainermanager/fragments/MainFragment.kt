package com.example.recursivecontainermanager.fragments

import android.app.Dialog
import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.core.view.isVisible
import androidx.fragment.app.*
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.recursivecontainermanager.R
import com.example.recursivecontainermanager.databinding.MainFragmentBinding


class MainFragment: Fragment() {
    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.mainRefresh.setOnRefreshListener { onRefresh() }
        binding.itemSearchField.setOnClickListener { searchItem() }
        binding.shade.setOnClickListener { cancelSearchFocus() }
    }

    private fun onRefresh(){
        Toast.makeText(context,"Refreshing done", Toast.LENGTH_SHORT).show()
        binding.mainRefresh.isRefreshing = false
    }

    private fun searchItem(){
        binding.searchFragmentView.visibility=View.VISIBLE
        binding.searchFragmentView.getFragment<SearchFragment>().focusSearchBar()
        binding.shade.minimumHeight = binding.mainLayout.height
    }

    private fun cancelSearchFocus() {
        binding.searchFragmentView.visibility=View.INVISIBLE
        binding.currentItemFragmentView.requestFocus()
        binding.shade.minimumHeight = 0
        (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.layout_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.show_menu_button -> {
                MainMenuFragment().show(parentFragmentManager, tag)
                return true
            }
            R.id.refresh_button -> {
                onRefresh()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}