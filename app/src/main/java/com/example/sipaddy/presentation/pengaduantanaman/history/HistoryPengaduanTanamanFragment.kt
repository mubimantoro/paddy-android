package com.example.sipaddy.presentation.pengaduantanaman.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sipaddy.data.ResultState
import com.example.sipaddy.data.network.response.PengaduanTanamanData
import com.example.sipaddy.databinding.FragmentHistoryPengaduanTanamanBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.example.sipaddy.utils.showErrorDialog


class HistoryPengaduanTanamanFragment : Fragment() {

    private var _binding: FragmentHistoryPengaduanTanamanBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryPengaduanTanamanViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    private val pengaduanTanamanAdapter by lazy {
        HistoryPengaduanTanamanAdapter()
    }

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

        with(binding) {

            backBtn.setOnClickListener {
                view.findNavController().popBackStack()
            }

            historyPengaduanTanamanRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = pengaduanTanamanAdapter
            }
        }

        setupObserver()
    }

    private fun setupObserver() {
        viewModel.getPengaduanTanamanHistory()

        viewModel.result.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }

                is ResultState.Error -> {
                    showLoading(false)
                    showErrorDialog(
                        requireContext(),
                        message = result.error,
                        onClick = {},
                    )
                }

                is ResultState.Success -> {
                    showLoading(false)
                    setHistoryPengaduanTanaman(result.data.data)
                }
            }
        }
    }

    private fun setHistoryPengaduanTanaman(item: PengaduanTanamanData?) {
        val pengaduanTanaman = item?.pengaduanTanaman.orEmpty()
        if (pengaduanTanaman.isEmpty()) {
            showEmptyState()
        } else {
            showContent()
            pengaduanTanamanAdapter.submitList(pengaduanTanaman)
        }

    }

    private fun showEmptyState() {
        binding.apply {
            historyPengaduanTanamanRv.visibility = View.GONE
            emptyStateContainer.visibility = View.VISIBLE
        }
    }

    private fun showContent() {
        binding.apply {
            historyPengaduanTanamanRv.visibility = View.VISIBLE
            emptyStateContainer.visibility = View.GONE
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            historyPengaduanTanamanRv.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}