package com.example.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthManager(private val context: Context) {
    private val auth: FirebaseAuth? = try {
        FirebaseAuth.getInstance()
    } catch (e: Throwable) {
        Log.w(TAG, "FirebaseAuth initialization fallback: ${e.message}")
        null
    }
    private val credentialManager: CredentialManager = try {
        CredentialManager.create(context)
    } catch (e: Throwable) {
        Log.w(TAG, "CredentialManager initialization fallback: ${e.message}")
        CredentialManager.create(context.applicationContext)
    }

    companion object {
        private const val TAG = "AuthManager"
    }

    val currentUser: FirebaseUser?
        get() = try {
            auth?.currentUser
        } catch (e: Throwable) {
            null
        }

    val authStateFlow: Flow<FirebaseUser?>
        get() = authStateFlow()

    fun authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
        val currentAuth = auth
        if (currentAuth == null) {
            trySend(null)
            awaitClose { }
            return@callbackFlow
        }
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        try {
            currentAuth.addAuthStateListener(listener)
        } catch (e: Throwable) {
            trySend(null)
        }
        awaitClose { 
            try {
                currentAuth.removeAuthStateListener(listener)
            } catch (_: Throwable) {}
        }
    }

    private fun findActivity(ctx: Context?): android.app.Activity? {
        var current = ctx
        while (current is android.content.ContextWrapper) {
            if (current is android.app.Activity) return current
            current = current.baseContext
        }
        return null
    }

    suspend fun signInWithGoogle(activityContext: Context? = null): Result<FirebaseUser> {
        val currentAuth = auth ?: return Result.failure(Exception("Firebase Auth is not available on this device"))
        val targetContext = activityContext ?: context
        val activity = findActivity(targetContext) ?: (targetContext as? android.app.Activity)
        val activeContext = activity ?: targetContext
        val activeCredManager = if (activity != null) {
            try {
                CredentialManager.create(activity)
            } catch (_: Throwable) {
                credentialManager
            }
        } else {
            credentialManager
        }

        return try {
            val webClientId = try {
                context.getString(R.string.default_web_client_id)
            } catch (_: Exception) {
                "605979734970-2tf1v8bo84o3ou4jaeifhupmc6ho2ng9.apps.googleusercontent.com"
            }
            val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(webClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build()

            val result = activeCredManager.getCredential(
                request = request,
                context = activeContext
            )

            val credential = result.credential
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = currentAuth.signInWithCredential(authCredential).await()
                val user = authResult.user
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("Firebase user is null after sign in"))
                }
            } else {
                Result.failure(Exception("Unexpected credential type: ${credential.type}"))
            }
        } catch (e: GetCredentialCancellationException) {
            Log.w(TAG, "Google Sign-in was cancelled by user: ${e.message}")
            Result.failure(Exception("Google Sign-in cancelled"))
        } catch (e: androidx.credentials.exceptions.NoCredentialException) {
            Log.w(TAG, "No Google credential on device: ${e.message}")
            Result.failure(Exception("ဖုန်းတွင် Google Account ဖွင့်ထားရန် လိုအပ်ပါသည် (No Google account found on device)"))
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Credential Manager error during sign in: ${e.message}", e)
            Result.failure(Exception("Google Account ချိတ်ဆက်မှု မအောင်မြင်ပါ: ${e.message}"))
        } catch (e: Exception) {
            Log.e(TAG, "Error during Google Sign In: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun signOut() {
        try {
            auth?.signOut()
        } catch (e: Exception) {
            Log.e(TAG, "Error during sign out", e)
        }
    }
}
