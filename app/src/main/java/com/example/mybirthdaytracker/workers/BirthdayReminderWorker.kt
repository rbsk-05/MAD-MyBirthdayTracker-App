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
        val tomorrowBirthdays = allBirthdays.filter { DateUtils.getDaysLeft(it.dob) == 1 }

        if (todayBirthdays.isEmpty() && tomorrowBirthdays.isEmpty()) {
            Log.d("BirthdayReminderWorker", "No birthdays today or tomorrow.")
            if (isManualRun()) {
                NotificationHelper.showNotification(
                    context,
                    "System Check",
                    "Reminder system active. Sending test SMS & Call...",
                    999
                )
                
                val twilioApi = TwilioClient.create(
                    BuildConfig.TWILIO_ACCOUNT_SID,
                    BuildConfig.TWILIO_AUTH_TOKEN
                )
                
                // Test SMS
                sendSms(twilioApi, "Darshan", "Today is Darshan's birthday! 🎉 Don't forget to wish them! Happy Birthday Darshan! - From BirthdayTracker")
                
                // Test Call
                makeCall(twilioApi, "Darshan", "Hello! This is a test from Birthday Tracker. Today is Darshan's birthday! Don't forget to wish them. Happy Birthday Darshan!")
            }
            return Result.success()
        }

        val twilioApi = TwilioClient.create(
            BuildConfig.TWILIO_ACCOUNT_SID,
            BuildConfig.TWILIO_AUTH_TOKEN
        )

        // Today's Birthdays
        for (birthday in todayBirthdays) {
            val msg = "Today is ${birthday.name}'s birthday! 🎉"
            NotificationHelper.showNotification(context, "Birthday Today! \uD83C\uDF82", msg, birthday.id)

            sendSms(twilioApi, birthday.name, "Happy Birthday, ${birthday.name}! \uD83C\uDF82 - From BirthdayTracker")
            makeCall(twilioApi, birthday.name, "Hello! This is a reminder that today is ${birthday.name}'s birthday. Don't forget to wish them!")
        }

        // Tomorrow's Birthdays
        for (birthday in tomorrowBirthdays) {
            val msg = "Intimation: ${birthday.name}'s birthday is tomorrow! \uD83D\uDCC5"
            NotificationHelper.showNotification(context, "Birthday Tomorrow! \uD83D\uDCC5", msg, birthday.id + 1000)
        }

        return Result.success()
    }

    private fun isManualRun(): Boolean = true

    private suspend fun sendSms(twilioApi: com.example.mybirthdaytracker.network.TwilioApi, name: String, message: String) {
        try {
            if (BuildConfig.TWILIO_ACCOUNT_SID.isNotEmpty() && BuildConfig.TWILIO_TO_PHONE.isNotEmpty()) {
                twilioApi.sendMessage(
                    accountSid = BuildConfig.TWILIO_ACCOUNT_SID,
                    to = BuildConfig.TWILIO_TO_PHONE,
                    from = BuildConfig.TWILIO_FROM_PHONE,
                    body = message
                )
            }
        } catch (e: Exception) {
            Log.e("BirthdayReminderWorker", "SMS Error", e)
        }
    }

    private suspend fun makeCall(twilioApi: com.example.mybirthdaytracker.network.TwilioApi, name: String, voiceMessage: String) {
        try {
            if (BuildConfig.TWILIO_ACCOUNT_SID.isNotEmpty() && BuildConfig.TWILIO_TO_PHONE.isNotEmpty()) {
                val twiml = "<Response><Say>$voiceMessage</Say></Response>"
                twilioApi.makeCall(
                    accountSid = BuildConfig.TWILIO_ACCOUNT_SID,
                    to = BuildConfig.TWILIO_TO_PHONE,
                    from = BuildConfig.TWILIO_FROM_PHONE,
                    twiml = twiml
                )
                Log.d("BirthdayReminderWorker", "Call initiated for $name")
            }
        } catch (e: Exception) {
            Log.e("BirthdayReminderWorker", "Call Error", e)
        }
    }
}
