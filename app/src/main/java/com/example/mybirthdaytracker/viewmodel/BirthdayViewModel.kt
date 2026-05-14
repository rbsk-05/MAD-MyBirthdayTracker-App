package com.example.mybirthdaytracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mybirthdaytracker.data.BirthdayEntity
import com.example.mybirthdaytracker.repository.BirthdayRepository
import com.example.mybirthdaytracker.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class BirthdayViewModel(private val repository: BirthdayRepository) : ViewModel() {

    // Get all birthdays and sort them by days left
    val upcomingBirthdays: Flow<List<BirthdayEntity>> = repository.allBirthdays.map { list ->
        list.sortedBy { DateUtils.getDaysLeft(it.dob) }
    }

    // Filter birthdays that happened recently (days ago > 0) and take top 3
    val recentBirthdays: Flow<List<BirthdayEntity>> = repository.allBirthdays.map { list ->
        list.filter { DateUtils.getDaysAgo(it.dob) > 0 }
            .sortedBy { DateUtils.getDaysAgo(it.dob) }
            .take(3)
    }

    fun insert(birthday: BirthdayEntity) = viewModelScope.launch {
        repository.insert(birthday)
    }

    fun update(birthday: BirthdayEntity) = viewModelScope.launch {
        repository.update(birthday)
    }

    fun delete(birthday: BirthdayEntity) = viewModelScope.launch {
        repository.delete(birthday)
    }
}

class BirthdayViewModelFactory(private val repository: BirthdayRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BirthdayViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BirthdayViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
