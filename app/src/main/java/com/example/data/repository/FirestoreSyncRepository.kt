package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.R
import com.example.data.model.CustomFlashcard
import com.example.data.model.FirestoreQuizHistory
import com.example.data.model.FirestoreUserProfile
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject

class FirestoreSyncRepository(
    private val context: Context,
    private val db: FirebaseFirestore
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("kanji_kotoba_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val TAG = "FirestoreSyncRepo"
        private const val KEY_LOCAL_CARDS = "local_custom_cards"
        private const val KEY_LOCAL_XP = "local_total_xp"
        private const val KEY_LOCAL_STREAK = "local_current_streak"

        fun create(context: Context): FirestoreSyncRepository {
            val databaseId = context.getString(R.string.firestore_database_id)
            val instance = FirebaseFirestore.getInstance(databaseId)
            return FirestoreSyncRepository(context.applicationContext, instance)
        }
    }

    // Local Storage Helpers
    fun getLocalCustomCards(): List<CustomFlashcard> {
        val jsonStr = prefs.getString(KEY_LOCAL_CARDS, null) ?: return emptyList()
        val list = mutableListOf<CustomFlashcard>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    CustomFlashcard(
                        id = obj.optString("id", "card_$i"),
                        userId = obj.optString("userId", "local"),
                        front = obj.optString("front", ""),
                        reading = obj.optString("reading", ""),
                        back = obj.optString("back", ""),
                        cardType = obj.optString("cardType", "Kotoba"),
                        category = obj.optString("category", "Custom"),
                        exampleSentence = obj.optString("exampleSentence", ""),
                        exampleMeaning = obj.optString("exampleMeaning", ""),
                        masteryLevel = obj.optLong("masteryLevel", 0L)
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing local cards: ${e.message}", e)
        }
        return list
    }

    fun saveLocalCustomCard(card: CustomFlashcard) {
        val current = getLocalCustomCards().toMutableList()
        val index = current.indexOfFirst { it.id == card.id }
        if (index != -1) {
            current[index] = card
        } else {
            current.add(0, card)
        }
        val jsonArray = JSONArray()
        for (item in current) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("userId", item.userId)
            obj.put("front", item.front)
            obj.put("reading", item.reading)
            obj.put("back", item.back)
            obj.put("cardType", item.cardType)
            obj.put("category", item.category)
            obj.put("exampleSentence", item.exampleSentence)
            obj.put("exampleMeaning", item.exampleMeaning)
            obj.put("masteryLevel", item.masteryLevel)
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_LOCAL_CARDS, jsonArray.toString()).apply()
    }

    fun deleteLocalCustomCard(cardId: String) {
        val current = getLocalCustomCards().filter { it.id != cardId }
        val jsonArray = JSONArray()
        for (item in current) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("userId", item.userId)
            obj.put("front", item.front)
            obj.put("reading", item.reading)
            obj.put("back", item.back)
            obj.put("cardType", item.cardType)
            obj.put("category", item.category)
            obj.put("exampleSentence", item.exampleSentence)
            obj.put("exampleMeaning", item.exampleMeaning)
            obj.put("masteryLevel", item.masteryLevel)
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_LOCAL_CARDS, jsonArray.toString()).apply()
    }

    fun getLocalUserProfile(): FirestoreUserProfile {
        val xp = prefs.getLong(KEY_LOCAL_XP, 120L)
        val streak = prefs.getLong(KEY_LOCAL_STREAK, 1L)
        return FirestoreUserProfile(
            userId = "local_user",
            displayName = "Learner",
            totalXp = xp,
            currentStreak = streak
        )
    }

    fun saveLocalUserProfile(profile: FirestoreUserProfile) {
        prefs.edit()
            .putLong(KEY_LOCAL_XP, profile.totalXp)
            .putLong(KEY_LOCAL_STREAK, profile.currentStreak)
            .apply()
    }

    // Combined Cloud + Local Observation for Custom Flashcards
    fun observeCustomFlashcards(userId: String?): Flow<List<CustomFlashcard>> = callbackFlow {
        // First emit local cards immediately so user NEVER sees empty screen
        val localList = getLocalCustomCards()
        trySend(localList)

        if (userId.isNullOrEmpty()) {
            // Unauthenticated: Keep emitting local storage
            awaitClose { }
            return@callbackFlow
        }

        // Authenticated: Listen to Firestore and merge
        val colRef = db.collection("users").document(userId).collection("custom_flashcards")
        val registration = colRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Error observing custom flashcards from Firestore: ${error.message}")
                trySend(getLocalCustomCards())
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val cloudCards = snapshot.toObjects(CustomFlashcard::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                val currentLocal = getLocalCustomCards()
                // Merge cloud and local cards by id
                val combinedMap = mutableMapOf<String, CustomFlashcard>()
                currentLocal.forEach { combinedMap[it.id] = it }
                cloudCards.forEach { combinedMap[it.id] = it }
                trySend(combinedMap.values.toList())
            } else {
                trySend(getLocalCustomCards())
            }
        }
        awaitClose { registration.remove() }
    }

    suspend fun saveCustomFlashcard(card: CustomFlashcard): Result<Unit> {
        // 1. Save to local storage first (instant durability)
        saveLocalCustomCard(card)

        // 2. If signed in, sync to Firestore
        val userId = card.userId
        if (userId.isNotEmpty() && userId != "local" && userId != "local_user") {
            try {
                val cardId = if (card.id.isNotEmpty()) card.id else "card_${System.currentTimeMillis()}"
                val docRef = db.collection("users").document(userId).collection("custom_flashcards").document(cardId)
                val data = mutableMapOf<String, Any>(
                    "id" to cardId,
                    "userId" to userId,
                    "front" to card.front,
                    "reading" to card.reading,
                    "back" to card.back,
                    "cardType" to card.cardType,
                    "category" to card.category,
                    "exampleSentence" to card.exampleSentence,
                    "exampleMeaning" to card.exampleMeaning,
                    "masteryLevel" to card.masteryLevel
                )
                val snapshot = docRef.get().await()
                if (!snapshot.exists()) {
                    data["createdAt"] = FieldValue.serverTimestamp()
                } else {
                    data["updatedAt"] = FieldValue.serverTimestamp()
                }
                docRef.set(data).await()
            } catch (e: Exception) {
                Log.w(TAG, "Cloud sync pending: ${e.message}")
            }
        }
        return Result.success(Unit)
    }

    suspend fun deleteCustomFlashcard(userId: String, cardId: String): Result<Unit> {
        deleteLocalCustomCard(cardId)
        if (userId.isNotEmpty() && userId != "local" && userId != "local_user") {
            try {
                db.collection("users").document(userId).collection("custom_flashcards").document(cardId).delete().await()
            } catch (e: Exception) {
                Log.w(TAG, "Cloud delete failed: ${e.message}")
            }
        }
        return Result.success(Unit)
    }

    fun observeUserProfile(userId: String?): Flow<FirestoreUserProfile?> = callbackFlow {
        val local = getLocalUserProfile()
        trySend(local)

        if (userId.isNullOrEmpty()) {
            awaitClose { }
            return@callbackFlow
        }

        val docRef = db.collection("users").document(userId)
        val registration = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(getLocalUserProfile())
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val profile = snapshot.toObject(FirestoreUserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                if (profile != null) {
                    saveLocalUserProfile(profile)
                    trySend(profile)
                }
            } else {
                trySend(getLocalUserProfile())
            }
        }
        awaitClose { registration.remove() }
    }

    suspend fun saveOrUpdateProfile(profile: FirestoreUserProfile): Result<Unit> {
        saveLocalUserProfile(profile)
        val userId = profile.userId
        if (userId.isNotEmpty() && userId != "local_user") {
            try {
                val docRef = db.collection("users").document(userId)
                val data = mutableMapOf<String, Any>(
                    "userId" to userId,
                    "displayName" to profile.displayName,
                    "targetJlptLevel" to profile.targetJlptLevel,
                    "dailyGoal" to profile.dailyGoal,
                    "totalXp" to profile.totalXp,
                    "currentStreak" to profile.currentStreak,
                    "bestStreak" to profile.bestStreak
                )
                profile.email?.let { data["email"] = it }
                profile.avatarUrl?.let { data["avatarUrl"] = it }

                val snapshot = docRef.get().await()
                if (!snapshot.exists()) {
                    data["createdAt"] = FieldValue.serverTimestamp()
                } else {
                    data["updatedAt"] = FieldValue.serverTimestamp()
                }
                docRef.set(data, com.google.firebase.firestore.SetOptions.merge()).await()
            } catch (e: Exception) {
                Log.w(TAG, "Cloud profile update failed: ${e.message}")
            }
        }
        return Result.success(Unit)
    }

    suspend fun recordQuizHistory(history: FirestoreQuizHistory): Result<Unit> {
        val userId = history.userId
        if (userId.isNotEmpty() && userId != "local_user") {
            try {
                val quizId = if (history.id.isNotEmpty()) history.id else "quiz_${System.currentTimeMillis()}"
                val docRef = db.collection("users").document(userId).collection("quiz_histories").document(quizId)
                val data = mapOf(
                    "id" to quizId,
                    "userId" to userId,
                    "quizType" to history.quizType,
                    "score" to history.score,
                    "totalQuestions" to history.totalQuestions,
                    "accuracyPercentage" to history.accuracyPercentage,
                    "xpEarned" to history.xpEarned,
                    "createdAt" to FieldValue.serverTimestamp()
                )
                docRef.set(data).await()
            } catch (e: Exception) {
                Log.w(TAG, "Quiz history cloud record failed: ${e.message}")
            }
        }
        return Result.success(Unit)
    }
}
