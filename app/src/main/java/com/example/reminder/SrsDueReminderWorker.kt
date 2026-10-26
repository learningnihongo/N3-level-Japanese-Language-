package com.example.reminder

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SrsDueReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val reminderPrefs = ReminderPreferences(applicationContext)
        val settings = reminderPrefs.settings

        // If overall reminders or SRS review alerts are disabled, exit cleanly
        if (!settings.isEnabled || !settings.isSrsReviewReminderEnabled) {
            return@withContext Result.success()
        }

        try {
            val db = AppDatabase.getInstance(applicationContext)
            val now = System.currentTimeMillis()

            // 1. Fetch cards that have reached their SRS due review timestamp
            val srsDueCards = db.vocabDao().getSrsCardsDueDirect(now)
            val dueCards = if (srsDueCards.isNotEmpty()) srsDueCards else db.vocabDao().getDueCardsDirect(now)

            if (dueCards.isNotEmpty()) {
                NotificationHelper.showSrsDueNotification(
                    context = applicationContext,
                    dueCards = dueCards,
                    totalDueCount = dueCards.size
                )
            }

            // 2. Schedule the next check dynamically for the earliest future due card
            SrsReminderScheduler.scheduleNextDueCheckInternal(applicationContext, db)

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
