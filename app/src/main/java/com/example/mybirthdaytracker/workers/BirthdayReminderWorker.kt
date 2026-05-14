package com.example.mybirthdaytracker.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mybirthdaytracker.BuildConfig
import com.example.mybirthdaytracker.data.BirthdayDatabase
import com.example.mybirthdaytracker.network.TwilioClient
import com.example.mybirthdaytracker.utils.DateUtils
import com.example.mybirthdaytracker.utils.NotificationHelper
import kotlinx.coroutines.flow.firstOrNull

class BirthdayReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("BirthdayReminderWorker", "Running birthday check...")
        
        NotificationHelper.createNotificationChannel(context)

        val database = BirthdayDatabase.getDatabase(context)
        val birthdaysFlow = database.birthdayDao().getAllBirthdays()
        
        val allBirthdays = birthdaysFlow.firstOrNull() ?: return Result.success()

        val todayBirthdays = allBirthdays.filter { DateUtils.getDaysLeft(it.dob) == 0 }

        if (todayBirthdays.isEmpty()) {
            Log.d("BirthdayReminderWorker", "No birthdays today.")
            return Result.success()
        }

        val twilioApi = TwilioClient.create(
            BuildConfig.TWILIO_ACCOUNT_SID,
            BuildConfig.TWILIO_AUTH_TOKEN
        )

        for (birthday in todayBirthdays) {
            val messageBody = "Reminder: Today is ${birthday.name}'s birthday! 🎉"
            
            // Show Local Notification
            NotificationHelper.showNotification(
                context,
                "Birthday Reminder \uD83C\uDF82",
                messageBody,
                birthday.id
            )

            // Send Twilio SMS
            try {
                if (BuildConfig.TWILIO_ACCOUNT_SID.isNotEmpty() && BuildConfig.TWILIO_TO_PHONE.isNotEmpty()) {
                    val response = twilioApi.sendMessage(
                        accountSid = BuildConfig.TWILIO_ACCOUNT_SID,
                        to = BuildConfig.TWILIO_TO_PHONE,
                        from = BuildConfig.TWILIO_FROM_PHONE,
                        body = messageBody
                    )
                    
                    if (response.isSuccessful) {
                        Log.d("BirthdayReminderWorker", "SMS sent successfully for ${birthday.name}")
                    } else {
                        Log.e("BirthdayReminderWorker", "Failed to send SMS: ${response.errorBody()?.string()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("BirthdayReminderWorker", "Exception sending SMS", e)
                // Returning failure could trigger retry depending on WorkManager config,
                // but since it's a daily notification, we might just log and continue
                // so we don't spam them if the API is down for hours.
            }
        }

        return Result.success()
    }
}
