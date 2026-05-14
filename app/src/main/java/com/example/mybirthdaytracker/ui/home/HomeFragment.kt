package com.example.mybirthdaytracker.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mybirthdaytracker.adapters.BirthdayAdapter
import com.example.mybirthdaytracker.adapters.RecentAdapter
import com.example.mybirthdaytracker.data.BirthdayDatabase
import com.example.mybirthdaytracker.databinding.FragmentHomeBinding
import com.example.mybirthdaytracker.repository.BirthdayRepository
import com.example.mybirthdaytracker.viewmodel.BirthdayViewModel
import com.example.mybirthdaytracker.viewmodel.BirthdayViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: BirthdayViewModel
    private lateinit var upcomingAdapter: BirthdayAdapter
    private lateinit var recentAdapter: RecentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    // IMPORTANT: All view/lifecycle work happens in onViewCreated, NOT onCreateView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up ViewModel
        val dao = BirthdayDatabase.getDatabase(requireContext()).birthdayDao()
        val repository = BirthdayRepository(dao)
        val factory = BirthdayViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[BirthdayViewModel::class.java]

        setupRecyclerViews()
        observeData()
    }

    private fun setupRecyclerViews() {
        upcomingAdapter = BirthdayAdapter(requireContext())
        binding.recyclerViewUpcoming.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = upcomingAdapter
            setHasFixedSize(false)
        }

        recentAdapter = RecentAdapter(requireContext())
        binding.recyclerViewMissed.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = recentAdapter
            setHasFixedSize(false)
        }
    }

    // Observe flows AFTER view is ready — using viewLifecycleOwner safely from onViewCreated
    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.upcomingBirthdays.collectLatest { list ->
                upcomingAdapter.submitList(list)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.recentBirthdays.collectLatest { list ->
                recentAdapter.submitList(list)
                binding.recyclerViewMissed.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
