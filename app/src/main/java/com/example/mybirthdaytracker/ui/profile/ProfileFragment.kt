package com.example.mybirthdaytracker.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.mybirthdaytracker.databinding.FragmentProfileBinding
import com.example.mybirthdaytracker.workers.BirthdayReminderWorker

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonTestNotification.setOnClickListener {
            Toast.makeText(requireContext(), "Triggering reminder check...", Toast.LENGTH_SHORT).show()
            val request = OneTimeWorkRequestBuilder<BirthdayReminderWorker>().build()
            WorkManager.getInstance(requireContext()).enqueue(request)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
