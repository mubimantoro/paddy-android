package com.example.sipaddy.presentation.pengaduantanaman.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sipaddy.utils.ResultState
import com.example.sipaddy.databinding.FragmentHistoryPengaduanTanamanBinding
import com.example.sipaddy.presentation.ViewModelFactory


class HistoryPengaduanTanamanFragment : Fragment() {

    private var _binding: FragmentHistoryPengaduanTanamanBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryPengaduanTanamanViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var historyPengaduanAdapter: HistoryPengaduanTanamanAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHistoryPengaduanTanamanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObserver()
    }

    private fun setupRecyclerView() {
        historyPengaduanAdapter = HistoryPengaduanTanamanAdapter { item ->
            val action =
                HistoryPengaduanTanamanFragmentDirections.actionHistoryPengaduanTanamanFragmentToDetailPengaduanTanamanFragment(
                    item.id
                )
            findNavController().navigate(action)
        }

        binding.historyPengaduanTanamanRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyPengaduanAdapter
            setHasFixedSize(true)
        }

        binding.swipeRefresh.setOnRefreshListener {
            loadHistory()
        }


    }

    private fun loadHistory() {
        viewModel.getPengaduanTanamanHistory()
    }

    private fun setupObserver() {

        viewModel.result.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }

                is ResultState.Error -> {
                    showLoading(false)
                    binding.swipeRefresh.isRefreshing = false
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()

                }

                is ResultState.Success -> {
                    showLoading(false)
                    binding.swipeRefresh.isRefreshing = false
                    if (result.data.isEmpty()) {
                        showEmptyState(true)
                    } else {
                        showEmptyState(false)
                        historyPengaduanAdapter.submitList(result.data)
                    }
                }
            }
        }
    }

    private fun showEmptyState(isEmpty: Boolean) {
        binding.apply {
            if (isEmpty) {
                emptyStateTv.visibility = View.VISIBLE
                historyPengaduanTanamanRv.visibility = View.GONE
            } else {
                emptyStateTv.visibility = View.GONE
                historyPengaduanTanamanRv.visibility = View.VISIBLE
            }
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}