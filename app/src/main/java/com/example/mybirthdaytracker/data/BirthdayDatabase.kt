package com.example.mybirthdaytracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStreamReader

@Database(entities = [BirthdayEntity::class], version = 1, exportSchema = false)
abstract class BirthdayDatabase : RoomDatabase() {

    abstract fun birthdayDao(): BirthdayDao

    companion object {
        @Volatile
        private var INSTANCE: BirthdayDatabase? = null

        fun getDatabase(context: Context): BirthdayDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BirthdayDatabase::class.java,
                    "birthday_database"
                )
                .addCallback(BirthdayDatabaseCallback(context.applicationContext))
                .build()
                INSTANCE = instance
                instance
            }
        }

        // Moved callback to companion object so it can safely access INSTANCE
        private class BirthdayDatabaseCallback(
            private val context: Context
        ) : RoomDatabase.Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Use INSTANCE safely inside companion object scope
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.birthdayDao(), context)
                    }
                }
            }

            private suspend fun populateDatabase(birthdayDao: BirthdayDao, context: Context) {
                try {
                    val inputStream = context.assets.open("birthdays.json")
                    val reader = InputStreamReader(inputStream)
                    val type = object : TypeToken<List<BirthdayEntity>>() {}.type
                    val birthdays: List<BirthdayEntity> = Gson().fromJson(reader, type)
                    birthdayDao.insertAll(birthdays)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
