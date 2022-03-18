package com.example.recursivecontainermanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recursivecontainermanager.adapters.ItemListAdapter
import com.example.recursivecontainermanager.databinding.LocationChainFragmentBinding
import com.example.recursivecontainermanager.viewmodel.MainViewModel

class LocationChainFragment: Fragment() {
    private var _binding: LocationChainFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater:LayoutInflater, container:ViewGroup?, bundle:Bundle?): View {
        _binding = LocationChainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.itemLocation.observe(requireActivity()) { list ->
            binding.itemLocationRecycler.adapter = ItemListAdapter(list, {viewModel.changeCurrentItem(it)}, true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}