package com.example.mybirthdaytracker.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mybirthdaytracker.data.BirthdayDatabase
import com.example.mybirthdaytracker.data.BirthdayEntity
import com.example.mybirthdaytracker.databinding.FragmentAddBinding
import com.example.mybirthdaytracker.repository.BirthdayRepository
import com.example.mybirthdaytracker.viewmodel.BirthdayViewModel
import com.example.mybirthdaytracker.viewmodel.BirthdayViewModelFactory

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: BirthdayViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = BirthdayDatabase.getDatabase(requireContext()).birthdayDao()
        val repository = BirthdayRepository(dao)
        val factory = BirthdayViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[BirthdayViewModel::class.java]

        binding.buttonSave.setOnClickListener {
            saveBirthday()
        }
    }

    private fun saveBirthday() {
        val name = binding.inputName.text.toString().trim()
        val dob = binding.inputDob.text.toString().trim()
        val tag = binding.inputTag.text.toString().trim()

        if (name.isEmpty() || dob.isEmpty() || tag.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val newBirthday = BirthdayEntity(
            name = name,
            dob = dob,
            tag = tag,
            image = "profile.jpg"
        )

        viewModel.insert(newBirthday)
        Toast.makeText(requireContext(), "Birthday saved! 🎉", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
