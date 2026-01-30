package com.example.sipaddy.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.sipaddy.R
import com.example.sipaddy.databinding.FragmentHomeBinding
import com.example.sipaddy.presentation.auth.AuthViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
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


        setupObserver()
        setupListener()
        observerLoginStatus()

    }

    private fun setupListener() {
        binding.predictDiseaseCard.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_predict_disease)

        }

        binding.pengaduanTanamanCard.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_pengaduanTanamanFragment)
        }

        binding.historyPredictDiseaseCard.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_history_prediction_disease)
        }

        binding.tvSeeAllRecent.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_historyFragment)
        }
    }

    private fun setupObservers() {
        TODO("Not yet implemented")
    }

    private fun observerLoginStatus() {
        viewModel.isLoggedIn.observe(viewLifecycleOwner) { isLoggedIn ->
            if (!isLoggedIn) {
                navigateToLogin()
            }
        }
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_navigation_home_to_navigation_login)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}