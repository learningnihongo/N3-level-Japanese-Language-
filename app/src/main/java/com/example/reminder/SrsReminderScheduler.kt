package com.example.reminder

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.data.db.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

object SrsReminderScheduler {
    const val PERIODIC_SRS_WORK_NAME = "kanji_srs_periodic_check_work"
    const val DYNAMIC_SRS_WORK_NAME = "kanji_srs_dynamic_due_work"
    const val TEST_SRS_WORK_NAME = "kanji_srs_test_work"
    const val WORK_TAG = "kanji_srs_reminder_tag"

    /**
     * Initializes both periodic background checks (every 6 hours) and dynamic
     * review scheduling based on user preferences.
     */
    fun initScheduler(context: Context) {
        val settings = ReminderPreferences(context).settings
        if (settings.isEnabled && settings.isSrsReviewReminderEnabled) {
            schedulePeriodicCheck(context)
            scheduleNextDueCheck(context)
        } else {
            cancelSrsReminders(context)
        }
    }

    /**
     * Periodic fallback check running in the background every 6 hours.
     * Guarantees that even if exact alarms are deferred by Doze mode,
     * due cards are checked and notified.
     */
    fun schedulePeriodicCheck(context: Context) {
        val periodicRequest = PeriodicWorkRequest.Builder(
            SrsDueReminderWorker::class.java,
            6L,
            TimeUnit.HOURS
        )
            .addTag(WORK_TAG)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_SRS_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )
    }

    /**
     * Schedules a dynamic OneTimeWorkRequest targeted for the exact future timestamp
     * when the next batch of SRS Kanji cards become due.
     */
    fun scheduleNextDueCheck(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getInstance(context)
                scheduleNextDueCheckInternal(context, db)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun scheduleNextDueCheckInternal(context: Context, db: AppDatabase) {
        val settings = ReminderPreferences(context).settings
        if (!settings.isEnabled || !settings.isSrsReviewReminderEnabled) {
            return
        }

        val now = System.currentTimeMillis()
        val earliestFutureTimestamp = db.vocabDao().getEarliestFutureReviewTimestamp(now)

        if (earliestFutureTimestamp != null && earliestFutureTimestamp > now) {
            val delayMs = (earliestFutureTimestamp - now).coerceAtLeast(60_000L) // Minimum 1 minute
            
            // Only schedule if within next 14 days
            if (delayMs <= TimeUnit.DAYS.toMillis(14)) {
                val request = OneTimeWorkRequest.Builder(SrsDueReminderWorker::class.java)
                    .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                    .addTag(WORK_TAG)
                    .build()

                WorkManager.getInstance(context).enqueueUniqueWork(
                    DYNAMIC_SRS_WORK_NAME,
                    ExistingWorkPolicy.REPLACE,
                    request
                )
            }
        }
    }

    fun rescheduleFromPreferences(context: Context) {
        val settings = ReminderPreferences(context).settings
        if (settings.isEnabled && settings.isSrsReviewReminderEnabled) {
            schedulePeriodicCheck(context)
            scheduleNextDueCheck(context)
        } else {
            cancelSrsReminders(context)
        }
    }

    fun cancelSrsReminders(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(PERIODIC_SRS_WORK_NAME)
        workManager.cancelUniqueWork(DYNAMIC_SRS_WORK_NAME)
    }

    /**
     * Triggers an immediate one-time SRS alert so the user can test the feature
     * and verify notifications in the background or foreground.
     */
    fun sendImmediateTestSrsReminder(context: Context) {
        val request = OneTimeWorkRequest.Builder(SrsDueReminderWorker::class.java)
            .addTag(WORK_TAG)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            TEST_SRS_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}
