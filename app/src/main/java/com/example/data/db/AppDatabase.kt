package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.CustomFlashcardEntity
import com.example.data.model.KanjiEntity
import com.example.data.model.KotobaEntity
import com.example.data.model.QuizHistory
import com.example.data.model.UserProfile
import com.example.data.model.VocabCard
import com.example.data.model.SrsReviewLog

@Database(
    entities = [
        VocabCard::class,
        UserProfile::class,
        QuizHistory::class,
        KanjiEntity::class,
        KotobaEntity::class,
        CustomFlashcardEntity::class,
        SrsReviewLog::class
    ],
    version = 9,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun vocabDao(): VocabDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun quizDao(): QuizDao
    abstract fun kanjiDao(): KanjiDao
    abstract fun kotobaDao(): KotobaDao
    abstract fun customFlashcardDao(): CustomFlashcardDao
    abstract fun srsReviewDao(): SrsReviewDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kanjikotoba_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
