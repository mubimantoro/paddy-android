package com.example.sipaddy.presentation.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sipaddy.utils.ResultState
import com.example.sipaddy.data.model.response.PredictResponse
import com.example.sipaddy.databinding.FragmentHistoryPredictionBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.example.sipaddy.utils.gone
import com.example.sipaddy.utils.showToast
import com.example.sipaddy.utils.visible

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

    private fun setupListener() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadPrediction()
        }
    }

    private fun setupObserver() {
        viewModel.result.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(false)
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

    private fun showEmpty(isEmpty: Boolean) {
        if (isEmpty) {
            binding.layoutEmpty.visible()
            binding.historyPredictionRv.gone()
        } else {
            binding.layoutEmpty.gone()
            binding.historyPredictionRv.visible()
        }
    }

    private fun setupRecyclerView() {
        adapter = HistoryPredictionAdapter { item ->
            navigateToDetail(item)
        }

        binding.historyPredictionRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HistoryPredictionFragment.adapter
            setHasFixedSize(true)
        }
    }

    private fun navigateToDetail(item: PredictResponse) {}

    private fun showLoading(isLoading: Boolean) {
        binding.swipeRefresh.isRefreshing = isLoading
        if (isLoading) {
            binding.progressBar.visible()
        } else {
            binding.progressBar.gone()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}