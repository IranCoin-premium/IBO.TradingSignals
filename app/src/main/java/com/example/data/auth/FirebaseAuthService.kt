package com.example.data.auth

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class FirebaseAuthService(private val context: Context) {
    private val auth: FirebaseAuth by lazy {
        com.example.fcm.FirebaseAppInitializer.ensureInitialized(context)
        FirebaseAuth.getInstance()
    }
    private val credentialManager: CredentialManager by lazy { CredentialManager.create(context) }

    val currentUser: FirebaseUser?
        get() = try {
            auth.currentUser
        } catch (e: Exception) {
            null
        }

    suspend fun signInWithEmail(email: String, pass: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, pass).await()
            val user = result.user
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("کاربر یافت نشد."))
            }
        } catch (e: Exception) {
            Log.e("FirebaseAuthService", "signInWithEmail error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(email: String, pass: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            val user = result.user
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("ثبت‌نام با خطا مواجه شد."))
            }
        } catch (e: Exception) {
            Log.e("FirebaseAuthService", "signUpWithEmail error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogleCredentialManager(serverClientId: String? = null): Result<FirebaseUser> {
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .setServerClientId(serverClientId ?: "126239385393-placeholder.apps.googleusercontent.com")
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val response = credentialManager.getCredential(context = context, request = request)
            val credential = response.credential

            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth.signInWithCredential(authCredential).await()
                val user = authResult.user
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("اعتبارسنجی کاربر گوگل با شکست مواجه شد."))
                }
            } else {
                Result.failure(Exception("نوع اطلاعات کاربری معتبر نبود."))
            }
        } catch (e: Exception) {
            Log.e("FirebaseAuthService", "Google Credential Manager Sign-In fallback: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun signOut() {
        try {
            auth.signOut()
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (e: Exception) {
            Log.e("FirebaseAuthService", "SignOut error: ${e.message}")
        }
    }
}
