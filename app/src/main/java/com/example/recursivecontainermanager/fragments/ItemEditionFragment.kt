package com.example.recursivecontainermanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.recursivecontainermanager.databinding.ItemEditionFragmentBinding

class ItemEditionFragment: Fragment() {
    private var _binding: ItemEditionFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ItemEditionFragmentBinding.inflate(inflater, container, false)

        binding.enterItemButton.setOnClickListener {
            val action = ItemEditionFragmentDirections.actionItemEditionFragmentToMainFragment()
            binding.root.findNavController().navigate(action)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}