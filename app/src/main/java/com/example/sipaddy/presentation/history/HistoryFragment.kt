package com.example.sipaddy.presentation.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sipaddy.R
import com.example.sipaddy.adapter.HistoryAdapter
import com.example.sipaddy.data.ResultState
import com.example.sipaddy.data.network.response.HistoryItem
import com.example.sipaddy.databinding.FragmentHistoryBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.example.sipaddy.utils.bottomSheetDialog


class HistoryFragment : Fragment() {

    private val binding: FragmentHistoryBinding by lazy {
        FragmentHistoryBinding.inflate(layoutInflater)
    }

    private val viewModel: HistoryViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getHistory()
        getHistory()
    }

    private fun getHistory() {
        viewModel.resultHistory.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> {
                        showLoading(true)
                    }

                    is ResultState.Error -> {
                        showLoading(false)
                        bottomSheetDialog(
                            requireContext(),
                            getString(R.string.failed_to_get_history),
                            R.drawable.error_image,
                            buttonColorResId = R.color.red,
                            onClick = {}

                        )
                    }

                    is ResultState.Success -> {
                        showLoading(false)
                        setHistoryData(result.data.data)
                    }
                }
            }
        }
    }

    private fun setHistoryData(item: List<HistoryItem>?) {
        val adapter = HistoryAdapter()
        adapter.submitList(item)

        with(binding) {
            historyRv.layoutManager = LinearLayoutManager(requireContext())
            historyRv.adapter = adapter
        }
    }

    private fun showLoading(isLoading: Boolean) {
        with(binding) {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            historyRv.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
    }


}