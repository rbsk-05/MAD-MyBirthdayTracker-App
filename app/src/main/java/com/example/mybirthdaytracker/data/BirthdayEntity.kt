package com.example.mybirthdaytracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "birthdays")
data class BirthdayEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val dob: String, // e.g., "May 18 2005"
    val tag: String,
    val image: String // Can be a local drawable name ("d4.jpg") or a content URI
)
