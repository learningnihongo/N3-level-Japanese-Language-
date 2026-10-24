package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.SrsReviewLog
import kotlinx.coroutines.flow.Flow

@Dao
interface SrsReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: SrsReviewLog): Long

    @Query("SELECT * FROM srs_review_logs ORDER BY reviewTimestamp DESC LIMIT :limit")
    fun getRecentLogs(limit: Int): Flow<List<SrsReviewLog>>

    @Query("SELECT * FROM srs_review_logs WHERE cardId = :cardId ORDER BY reviewTimestamp DESC")
    fun getLogsForCard(cardId: Long): Flow<List<SrsReviewLog>>

    @Query("SELECT COUNT(*) FROM srs_review_logs")
    fun getTotalReviewCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM srs_review_logs WHERE isRetentionSuccess = 1")
    fun getSuccessfulReviewCount(): Flow<Int>

    @Query("SELECT (CAST(SUM(CASE WHEN isRetentionSuccess = 1 THEN 1 ELSE 0 END) AS REAL) * 100.0 / NULLIF(COUNT(*), 0)) FROM srs_review_logs")
    fun getRetentionRatePercent(): Flow<Float?>
}
