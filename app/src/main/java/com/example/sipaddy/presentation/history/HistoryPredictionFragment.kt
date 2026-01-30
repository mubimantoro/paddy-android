package com.example.sipaddy.presentation.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sipaddy.databinding.FragmentHistoryPredictionBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.example.sipaddy.utils.ResultState
import com.example.sipaddy.utils.showToast

class HistoryPredictionFragment : Fragment() {

    private var _binding: FragmentHistoryPredictionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryPredictionViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var adapter: HistoryPredictionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHistoryPredictionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObserver()
        setupListener()


        viewModel.loadPrediction()
    }

    private fun setupRecyclerView() {
        adapter = HistoryPredictionAdapter { item ->
            val action = HistoryPredictionFragmentDirections
                .actionNavigationHistoryPredictionDiseaseToNavigationResult(item.id)
            findNavController().navigate(action)
        }

        binding.predictionHistoryRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HistoryPredictionFragment.adapter
        }
    }

    private fun setupObserver() {
        viewModel.result.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }

                is ResultState.Error -> {
                    showLoading(false)
                    requireContext().showToast(result.message)
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
            viewModel.refreshHistory()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun showEmpty(isEmpty: Boolean) {
        binding.emptyLayout.isVisible = isEmpty
        binding.predictionHistoryRv.isVisible = !isEmpty
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
        binding.predictionHistoryRv.isVisible = !isLoading
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}