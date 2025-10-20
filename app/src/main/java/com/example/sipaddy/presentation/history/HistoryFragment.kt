package com.example.sipaddy.presentation.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sipaddy.data.ResultState
import com.example.sipaddy.data.network.response.DiseaseData
import com.example.sipaddy.databinding.FragmentHistoryBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.example.sipaddy.presentation.base.BaseFragment
import kotlinx.coroutines.launch


class HistoryFragment : BaseFragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    private val historyAdapter by lazy { HistoryAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupObserver()
    }

    private fun setupView() {
        binding.historyRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }

    private fun setupObserver() {
        viewModel.getHistory()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.resultHistory.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is ResultState.Loading -> {
                        showLoading(true)
                    }

                    is ResultState.Error -> {
                        showLoading(false)

                        if (result.isTokenExpired) {
                            viewLifecycleOwner.lifecycleScope.launch {
                                viewModel.logout()
                            }
                            handleTokenExpired()

                        } else {
                            showError(
                                message = result.error,
                                onRetry = {
                                    viewModel.getHistory()
                                }
                            )
                        }
                    }

                    is ResultState.Success -> {
                        showLoading(false)
                        setHistoryData(result.data.data)
                    }
                }

            }
        }
    }

    private fun setHistoryData(item: DiseaseData?) {
        val diseases = item?.diseases.orEmpty()

        if (diseases.isEmpty()) {
            showEmptyState()
        } else {
            showContent()
            historyAdapter.submitList(diseases)
        }
    }

    private fun showEmptyState() {
        binding.historyRv.visibility = View.GONE
    }

    private fun showContent() {
        binding.historyRv.visibility = View.VISIBLE
    }


    private fun showLoading(isLoading: Boolean) {
        with(binding) {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            historyRv.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}