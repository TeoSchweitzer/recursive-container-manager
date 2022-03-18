package com.example.recursivecontainermanager.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.recursivecontainermanager.R
import com.example.recursivecontainermanager.databinding.MainFragmentBinding
import com.example.recursivecontainermanager.viewmodel.MainViewModel


class MainFragment: Fragment() {
    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

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
        binding.enterTokenButton.setOnClickListener { enterToken() }
        viewModel.loadingStatus.observe(viewLifecycleOwner) {
            Toast.makeText(context, getString(it), Toast.LENGTH_SHORT).show()
            if (it == R.string.authenticate_user_not_found || it == R.string.create_account_done) {
                val frag = MainMenuFragment()
                frag.show(parentFragmentManager, null)
                frag.chooseTab(1)
            } else if (it == R.string.create_account_name_taken) {
                val frag = MainMenuFragment()
                frag.show(parentFragmentManager, null)
                frag.chooseTab(2)
            }
        }
    }

    private fun onRefresh(){
        viewModel.fetchItems()
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

    private fun enterToken() {
        EnterTokenFragment().show(parentFragmentManager, null)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.layout_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.show_menu_button -> {
                MainMenuFragment().show(parentFragmentManager, null)
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