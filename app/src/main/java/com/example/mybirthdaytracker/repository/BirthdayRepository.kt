package com.example.mybirthdaytracker.repository

import com.example.mybirthdaytracker.data.BirthdayDao
import com.example.mybirthdaytracker.data.BirthdayEntity
import kotlinx.coroutines.flow.Flow

class BirthdayRepository(private val birthdayDao: BirthdayDao) {

    val allBirthdays: Flow<List<BirthdayEntity>> = birthdayDao.getAllBirthdays()

    suspend fun insert(birthday: BirthdayEntity) {
        birthdayDao.insertBirthday(birthday)
    }

    suspend fun update(birthday: BirthdayEntity) {
        birthdayDao.updateBirthday(birthday)
    }

    suspend fun delete(birthday: BirthdayEntity) {
        birthdayDao.deleteBirthday(birthday)
    }
}
