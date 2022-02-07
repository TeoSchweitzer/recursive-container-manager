package com.example.recursivecontainermanager.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.layout_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.show_menu_button -> {
                val action = MainFragmentDirections.actionMainFragmentToMainMenuFragment()
                binding.root.findNavController().navigate(action)
                return true
            }
            R.id.refresh_button -> {
                onRefresh()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onRefresh(){
        // use refresh() from viewmodel
        Toast.makeText(context,"Refreshing done", Toast.LENGTH_SHORT).show()
        binding.mainRefresh.isRefreshing = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}