package com.example.recursivecontainermanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recursivecontainermanager.adapters.ItemListAdapter
import com.example.recursivecontainermanager.databinding.LocationChainFragmentBinding

class LocationChainFragment: Fragment() {
    private var _binding: LocationChainFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater:LayoutInflater, container:ViewGroup?, bundle:Bundle?): View {
        _binding = LocationChainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //viewModel.itemLocationList.observe(requireActivity()) {
        //    binding.itemLocationRecycler.adapter = ItemListAdapter(it, ::viewModel.changeCurrentItem, true)
        //}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}