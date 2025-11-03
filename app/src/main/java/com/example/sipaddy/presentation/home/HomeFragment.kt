package com.example.sipaddy.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.sipaddy.R
import com.example.sipaddy.databinding.FragmentHomeBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.example.sipaddy.presentation.base.BaseFragment

class HomeFragment : BaseFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getUsername().observe(viewLifecycleOwner) { username ->
            binding.homeHeadingTv.text = "Selamat Datang, \n$username"
        }

        viewModel.getRole().observe(viewLifecycleOwner) { role ->
            when (role) {
                "user" -> {
                    setupUserView()
                }

                "popt" -> {
                    setupPoptView()
                }
            }
        }

        setupObserver()

    }

    private fun setupUserView() {
        with(binding) {

            headingPengaduanTanamanTv.text = "Pengaduan\nTanaman"
            pengaduanTanamanBtn.text = "Lapor"

            headingHistoryTv.text = "Riwayat\nPengaduan"
            historyBtn.text = "Lihat"


            diagnoseBtn.setOnClickListener {
                view?.findNavController()?.navigate(R.id.action_navigation_home_to_diagnoseFragment)
            }

            pengaduanTanamanBtn.setOnClickListener {
                view?.findNavController()
                    ?.navigate(R.id.action_navigation_home_to_pengaduanTanamanFragment)
            }

            historyBtn.setOnClickListener {
                view?.findNavController()
                    ?.navigate(R.id.action_navigation_home_to_historyPengaduanTanamanFragment)
            }


        }
    }

    private fun setupPoptView() {
        with(binding) {
            headingPengaduanTanamanTv.text = "Kelola\nPengaduan"
            pengaduanTanamanBtn.text = "Lihat Semua"

            headingHistoryTv.text = "Pengaduan\nPetani"
            historyBtn.text = "Lihat"

            diagnoseBtn.setOnClickListener {
                view?.findNavController()?.navigate(
                    R.id.action_navigation_home_to_diagnoseFragment
                )
            }

            historyBtn.setOnClickListener {
                view?.findNavController()?.navigate(
                    R.id.action_navigation_home_to_poptPengaduanTanamanFragment
                )
            }
        }
    }

    private fun setupObserver() {
        viewModel.getSession().observe(viewLifecycleOwner) { session ->
            if (session.isEmpty()) {
                navigateToLogin()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}