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
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject

class FirestoreSyncRepository(
    private val context: Context,
    private val db: FirebaseFirestore?
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("kanji_kotoba_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val TAG = "FirestoreSyncRepo"
        private const val KEY_LOCAL_CARDS = "local_custom_cards"
        private const val KEY_LOCAL_XP = "local_total_xp"
        private const val KEY_LOCAL_STREAK = "local_current_streak"
        private const val KEY_LOCAL_FRONT_LANG = "local_front_lang"
        private const val KEY_LOCAL_TTS = "local_auto_play_tts"
        private const val KEY_LOCAL_GESTURE = "local_gesture_style"
        private const val KEY_LOCAL_SHUFFLE = "local_shuffle_mode"
        private const val KEY_LOCAL_STUDIED_COUNT = "local_studied_count"

        fun create(context: Context): FirestoreSyncRepository {
            val dbInstance = try {
                val databaseId = try {
                    val resId = context.resources.getIdentifier("firestore_database_id", "string", context.packageName)
                    if (resId != 0) context.getString(resId) else null
                } catch (e: Exception) {
                    null
                }
                if (!databaseId.isNullOrBlank()) {
                    FirebaseFirestore.getInstance(databaseId)
                } else {
                    FirebaseFirestore.getInstance()
                }
            } catch (e: Throwable) {
                Log.w(TAG, "FirebaseFirestore not available, running in local offline mode: ${e.message}")
                null
            }
            return FirestoreSyncRepository(context.applicationContext, dbInstance)
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
                        deckName = obj.optString("deckName", "General"),
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
        saveAllLocalCards(current)
    }

    fun saveAllLocalCards(cards: List<CustomFlashcard>) {
        val jsonArray = JSONArray()
        for (item in cards) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("userId", item.userId)
            obj.put("front", item.front)
            obj.put("reading", item.reading)
            obj.put("back", item.back)
            obj.put("cardType", item.cardType)
            obj.put("category", item.category)
            obj.put("deckName", item.deckName)
            obj.put("exampleSentence", item.exampleSentence)
            obj.put("exampleMeaning", item.exampleMeaning)
            obj.put("masteryLevel", item.masteryLevel)
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_LOCAL_CARDS, jsonArray.toString()).apply()
    }

    fun deleteLocalCustomCard(cardId: String) {
        val current = getLocalCustomCards().filter { it.id != cardId }
        saveAllLocalCards(current)
    }

    fun getLocalUserProfile(): FirestoreUserProfile {
        val xp = prefs.getLong(KEY_LOCAL_XP, 120L)
        val streak = prefs.getLong(KEY_LOCAL_STREAK, 1L)
        val frontLang = prefs.getString(KEY_LOCAL_FRONT_LANG, "Japanese") ?: "Japanese"
        val tts = prefs.getBoolean(KEY_LOCAL_TTS, false)
        val gesture = prefs.getString(KEY_LOCAL_GESTURE, "SWIPE_REVIEW") ?: "SWIPE_REVIEW"
        val shuffle = prefs.getString(KEY_LOCAL_SHUFFLE, "DEFAULT") ?: "DEFAULT"
        val studiedCount = prefs.getLong(KEY_LOCAL_STUDIED_COUNT, 0L)

        return FirestoreUserProfile(
            userId = "local_user",
            displayName = "Learner",
            totalXp = xp,
            currentStreak = streak,
            flashcardFrontLang = frontLang,
            autoPlayTts = tts,
            gestureStyle = gesture,
            shuffleMode = shuffle,
            cardsStudiedCount = studiedCount
        )
    }

    fun saveLocalUserProfile(profile: FirestoreUserProfile) {
        prefs.edit()
            .putLong(KEY_LOCAL_XP, profile.totalXp)
            .putLong(KEY_LOCAL_STREAK, profile.currentStreak)
            .putString(KEY_LOCAL_FRONT_LANG, profile.flashcardFrontLang)
            .putBoolean(KEY_LOCAL_TTS, profile.autoPlayTts)
            .putString(KEY_LOCAL_GESTURE, profile.gestureStyle)
            .putString(KEY_LOCAL_SHUFFLE, profile.shuffleMode)
            .putLong(KEY_LOCAL_STUDIED_COUNT, profile.cardsStudiedCount)
            .apply()
    }

    // Combined Cloud + Local Observation for Custom Flashcards
    fun observeCustomFlashcards(userId: String?): Flow<List<CustomFlashcard>> = callbackFlow {
        // Emit local cards immediately so screen never appears empty
        val localList = getLocalCustomCards()
        trySend(localList)

        val currentDb = db
        if (userId.isNullOrEmpty() || currentDb == null) {
            awaitClose { }
            return@callbackFlow
        }

        // Authenticated: Listen to Firestore and merge
        val colRef = currentDb.collection("users").document(userId).collection("custom_flashcards")
        val registration = colRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Error observing custom flashcards from Firestore: ${error.message}")
                trySend(getLocalCustomCards())
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val cloudCards = snapshot.toObjects(CustomFlashcard::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                val currentLocal = getLocalCustomCards()
                val combinedMap = mutableMapOf<String, CustomFlashcard>()
                currentLocal.forEach { combinedMap[it.id] = it }
                cloudCards.forEach { combinedMap[it.id] = it }
                val merged = combinedMap.values.toList()
                saveAllLocalCards(merged)
                trySend(merged)
            } else {
                trySend(getLocalCustomCards())
            }
        }
        awaitClose { registration.remove() }
    }

    fun getCustomCardsFlow(userId: String?): Flow<List<CustomFlashcard>> = observeCustomFlashcards(userId)
    fun getUserProfileFlow(userId: String?): Flow<FirestoreUserProfile?> = observeUserProfile(userId)
    fun getQuizHistoriesFlow(userId: String?): Flow<List<FirestoreQuizHistory>> = observeQuizHistories(userId)

    suspend fun saveCustomFlashcard(card: CustomFlashcard): Result<Unit> {
        // 1. Save to local storage first (instant durability)
        saveLocalCustomCard(card)

        // 2. If signed in and Firestore available, sync to Firestore
        val userId = card.userId
        val currentDb = db
        if (currentDb != null && userId.isNotEmpty() && userId != "local" && userId != "local_user") {
            try {
                val cardId = if (card.id.isNotEmpty()) card.id else "card_${System.currentTimeMillis()}"
                val docRef = currentDb.collection("users").document(userId).collection("custom_flashcards").document(cardId)
                val data = mutableMapOf<String, Any>(
                    "id" to cardId,
                    "userId" to userId,
                    "front" to card.front,
                    "reading" to card.reading,
                    "back" to card.back,
                    "cardType" to card.cardType,
                    "category" to card.category,
                    "deckName" to card.deckName,
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

    suspend fun saveCustomCard(userId: String, card: CustomFlashcard): Result<Unit> {
        return saveCustomFlashcard(card.copy(userId = userId))
    }

    suspend fun saveBulkCustomCards(userId: String, cards: List<CustomFlashcard>): Result<Unit> {
        for (card in cards) {
            saveCustomFlashcard(card.copy(userId = userId))
        }
        return Result.success(Unit)
    }

    suspend fun addCustomFlashcard(
        userId: String?,
        front: String,
        reading: String,
        back: String,
        cardType: String,
        category: String,
        deckName: String,
        exampleSentence: String = "",
        exampleMeaning: String = ""
    ): Result<Unit> {
        val effectiveUid = userId ?: "local"
        val card = CustomFlashcard(
            id = "custom_${System.currentTimeMillis()}_${(100..999).random()}",
            userId = effectiveUid,
            front = front,
            reading = reading,
            back = back,
            cardType = cardType,
            category = category,
            deckName = deckName,
            exampleSentence = exampleSentence,
            exampleMeaning = exampleMeaning,
            createdAt = com.google.firebase.Timestamp.now()
        )
        return saveCustomFlashcard(card)
    }

    suspend fun deleteCustomFlashcard(userId: String?, cardId: String): Result<Unit> {
        deleteLocalCustomCard(cardId)
        val currentDb = db
        val actualUid = userId ?: ""
        if (currentDb != null && actualUid.isNotEmpty() && actualUid != "local" && actualUid != "local_user") {
            try {
                currentDb.collection("users").document(actualUid).collection("custom_flashcards").document(cardId).delete().await()
            } catch (e: Exception) {
                Log.w(TAG, "Cloud delete failed: ${e.message}")
            }
        }
        return Result.success(Unit)
    }

    suspend fun deleteCustomCard(userId: String?, cardId: String): Result<Unit> {
        return deleteCustomFlashcard(userId, cardId)
    }

    fun observeUserProfile(userId: String?): Flow<FirestoreUserProfile?> = callbackFlow {
        val local = getLocalUserProfile()
        trySend(local)

        val currentDb = db
        if (userId.isNullOrEmpty() || currentDb == null) {
            awaitClose { }
            return@callbackFlow
        }

        val docRef = currentDb.collection("users").document(userId)
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
        val currentDb = db
        if (currentDb != null && userId.isNotEmpty() && userId != "local_user") {
            try {
                val docRef = currentDb.collection("users").document(userId)
                val data = mutableMapOf<String, Any>(
                    "userId" to userId,
                    "displayName" to profile.displayName,
                    "targetJlptLevel" to profile.targetJlptLevel,
                    "dailyGoal" to profile.dailyGoal,
                    "totalXp" to profile.totalXp,
                    "currentStreak" to profile.currentStreak,
                    "bestStreak" to profile.bestStreak,
                    "flashcardFrontLang" to profile.flashcardFrontLang,
                    "autoPlayTts" to profile.autoPlayTts,
                    "gestureStyle" to profile.gestureStyle,
                    "shuffleMode" to profile.shuffleMode,
                    "cardsStudiedCount" to profile.cardsStudiedCount
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

    suspend fun updateProfileStats(userId: String, xp: Long, streak: Long): Result<Unit> {
        val current = getLocalUserProfile()
        val updated = current.copy(
            userId = userId,
            totalXp = xp,
            currentStreak = streak,
            bestStreak = maxOf(current.bestStreak, streak)
        )
        return saveOrUpdateProfile(updated)
    }

    suspend fun updateFlashcardPreferences(
        userId: String,
        frontLang: String,
        autoPlayTts: Boolean,
        gestureStyle: String,
        shuffleMode: String
    ): Result<Unit> {
        val current = getLocalUserProfile()
        val updated = current.copy(
            userId = if (userId.isNotEmpty()) userId else current.userId,
            flashcardFrontLang = frontLang,
            autoPlayTts = autoPlayTts,
            gestureStyle = gestureStyle,
            shuffleMode = shuffleMode
        )
        return saveOrUpdateProfile(updated)
    }

    suspend fun updateProfileSettings(
        userId: String,
        displayName: String,
        targetJlptLevel: String,
        dailyGoal: Long,
        frontLang: String,
        autoPlayTts: Boolean,
        gestureStyle: String,
        shuffleMode: String
    ): Result<Unit> {
        val current = getLocalUserProfile()
        val updated = current.copy(
            userId = if (userId.isNotEmpty()) userId else current.userId,
            displayName = displayName,
            targetJlptLevel = targetJlptLevel,
            dailyGoal = dailyGoal,
            flashcardFrontLang = frontLang,
            autoPlayTts = autoPlayTts,
            gestureStyle = gestureStyle,
            shuffleMode = shuffleMode
        )
        return saveOrUpdateProfile(updated)
    }

    suspend fun incrementCardsStudied(userId: String, count: Long = 1L): Result<Unit> {
        val current = getLocalUserProfile()
        val updated = current.copy(
            userId = if (userId.isNotEmpty()) userId else current.userId,
            cardsStudiedCount = current.cardsStudiedCount + count
        )
        return saveOrUpdateProfile(updated)
    }

    fun observeQuizHistories(userId: String?): Flow<List<FirestoreQuizHistory>> = callbackFlow {
        val currentDb = db
        if (userId.isNullOrEmpty() || currentDb == null) {
            trySend(emptyList())
            awaitClose { }
            return@callbackFlow
        }

        val colRef = currentDb.collection("users").document(userId).collection("quiz_histories")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(30)

        val registration = colRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Error observing quiz histories: ${error.message}")
                trySend(emptyList())
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.toObjects(FirestoreQuizHistory::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                trySend(list)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { registration.remove() }
    }

    suspend fun recordQuizHistory(history: FirestoreQuizHistory): Result<Unit> {
        val userId = history.userId
        val currentDb = db
        if (currentDb != null && userId.isNotEmpty() && userId != "local_user") {
            try {
                val quizId = if (history.id.isNotEmpty()) history.id else "quiz_${System.currentTimeMillis()}"
                val docRef = currentDb.collection("users").document(userId).collection("quiz_histories").document(quizId)
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
