package com.example.sipaddy.presentation.popt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sipaddy.utils.ResultState
import com.example.sipaddy.databinding.FragmentPoptPengaduanTanamanBinding
import com.example.sipaddy.presentation.ViewModelFactory


class PoptPengaduanTanamanFragment : Fragment() {

    private var _binding: FragmentPoptPengaduanTanamanBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PoptPengaduanTanamanViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    private lateinit var adapter: PoptPengaduanTanamanAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPoptPengaduanTanamanBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            backBtn.setOnClickListener {
                view.findNavController().popBackStack()
            }

            swipeRefresh.setOnRefreshListener {
                loadData()
            }
        }


        setupRecyclerView()
        setupObserver()
        loadData()
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
                }

                is ResultState.Success -> {
                    showLoading(false)
                    binding.swipeRefresh.isRefreshing = false

                    val data = result.data.data?.pengaduanTanaman

                    adapter.submitList(data)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.pengaduanTanamanRv.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun loadData() {
        viewModel.getPengaduanTanaman()
    }

    private fun setupRecyclerView() {
        adapter = PoptPengaduanTanamanAdapter { item ->
            val action =
                PoptPengaduanTanamanFragmentDirections.actionPoptPengaduanTanamanFragmentToPoptDetailPengaduanTanamanFragment(
                    item.id
                )
            findNavController().navigate(action)
        }

        binding.pengaduanTanamanRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@PoptPengaduanTanamanFragment.adapter
            setHasFixedSize(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}