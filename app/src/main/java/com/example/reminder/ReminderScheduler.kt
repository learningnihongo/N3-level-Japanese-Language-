package com.example.reminder

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object ReminderScheduler {
    const val UNIQUE_WORK_NAME = "kanji_daily_study_reminder_work"
    const val WORK_TAG = "kanji_study_reminder_tag"
    const val TEST_WORK_NAME = "kanji_test_reminder_work"

    fun calculateInitialDelayMs(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (target.timeInMillis <= now.timeInMillis) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }
        return target.timeInMillis - now.timeInMillis
    }

    fun getNextScheduledDescription(hour: Int, minute: Int): String {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val isTomorrow = target.timeInMillis <= now.timeInMillis
        val period = if (hour >= 12) "PM" else "AM"
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        val formattedTime = String.format("%d:%02d %s", displayHour, minute, period)
        return if (isTomorrow) "Tomorrow at $formattedTime" else "Today at $formattedTime"
    }

    fun scheduleDailyReminder(context: Context, hour: Int, minute: Int) {
        val delay = calculateInitialDelayMs(hour, minute)
        val request = PeriodicWorkRequest.Builder(
            DailyStudyReminderWorker::class.java,
            24L,
            TimeUnit.HOURS
        )
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag(WORK_TAG)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun rescheduleFromPreferences(context: Context) {
        val settings = ReminderPreferences(context).settings
        if (settings.isEnabled) {
            scheduleDailyReminder(context, settings.reminderHour, settings.reminderMinute)
        } else {
            cancelDailyReminder(context)
        }
    }

    fun cancelDailyReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_WORK_NAME)
    }

    fun sendImmediateTestReminder(context: Context) {
        val request = OneTimeWorkRequest.Builder(DailyStudyReminderWorker::class.java)
            .addTag(WORK_TAG)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            TEST_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}
