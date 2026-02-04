package com.example.sipaddy.presentation.popt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sipaddy.databinding.FragmentAssignedPengaduanTanamanBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.example.sipaddy.utils.ResultState


class AssignedPengaduanTanamanFragment : Fragment() {

    private var _binding: FragmentAssignedPengaduanTanamanBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AssignedPengaduanTanamanViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var adapter: AssignedPengaduanTanamanAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAssignedPengaduanTanamanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObserver()
        setupListener()
    }

    private fun setupRecyclerView() {
        adapter = AssignedPengaduanTanamanAdapter { item ->
            val action =
                AssignedPengaduanTanamanFragmentDirections.actionNavigationAssignedPengaduanTanamanToHandlePengaduanTanamanFragment(
                    item.id
                )
            findNavController().navigate(action)
        }

        binding.assignedRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AssignedPengaduanTanamanFragment.adapter
        }
    }

    private fun setupObserver() {
        viewModel.assignedResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }

                is ResultState.Error -> {
                    showLoading(false)
                }

                is ResultState.Success -> {
                    showLoading(false)
                    if (result.data.isEmpty()) {
                        showEmpty(true)
                    } else {
                        showEmpty(false)
                        adapter.submitList(result.data)
                    }
                }
            }
        }
    }

    private fun setupListener() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshPengaduanTanaman()
            binding.swipeRefresh.isRefreshing = false
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
        binding.assignedRv.isVisible = !isLoading
    }

    private fun showEmpty(isEmpty: Boolean) {
        binding.emptyLayout.isVisible = isEmpty
        binding.assignedRv.isVisible = !isEmpty
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}