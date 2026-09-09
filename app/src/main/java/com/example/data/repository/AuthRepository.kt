package com.example.data.repository

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.BuildConfig
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.local.UserEntity
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val db: AppDatabase
) {
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun authenticateUser(email: String, pass: String): UserEntity? {
        val cleanEmail = email.trim().lowercase()
        val user = if (cleanEmail == "admin") {
            db.userDao().getUserByEmail("admin@iranbinary.ir")
        } else {
            db.userDao().getUserByEmail(cleanEmail)
        }

        if (user == null) return null

        val result = BCrypt.verifyer().verify(pass.toCharArray(), user.passwordHash)
        return if (result.verified) {
            _currentUser.value = user
            user
        } else {
            null
        }
    }

    suspend fun registerUser(email: String, pass: String, name: String): UserEntity {
        val cleanEmail = email.trim().lowercase()
        val existing = db.userDao().getUserByEmail(cleanEmail)
        if (existing != null) {
            _currentUser.value = existing
            return existing
        }

        val hashedPass = BCrypt.withDefaults().hashToString(12, pass.toCharArray())

        val newUser = UserEntity(
            email = cleanEmail,
            passwordHash = hashedPass,
            fullName = name,
            role = "USER",
            activePlan = "اشتراک ویژه VIP",
            loginProvider = "MANUAL"
        )
        val id = db.userDao().insertUser(newUser)
        val created = newUser.copy(id = id)
        _currentUser.value = created
        return created
    }

    suspend fun registerOrLoginSocial(provider: String, name: String, email: String): UserEntity {
        val cleanEmail = email.trim().lowercase()
        val existing = db.userDao().getUserByEmail(cleanEmail)
        if (existing != null) {
            _currentUser.value = existing
            return existing
        }

        val newUser = UserEntity(
            email = cleanEmail,
            passwordHash = "SOCIAL_OAUTH",
            fullName = name,
            role = "USER",
            activePlan = "اشتراک ویژه VIP",
            loginProvider = provider
        )
        val id = db.userDao().insertUser(newUser)
        val created = newUser.copy(id = id)
        _currentUser.value = created
        return created
    }

    /**
     * Google Sign-In using Credential Manager + Firebase Auth.
     * Requires google-services.json with the matching client_id, otherwise Firebase
     * initialization is a no-op and this returns null.
     */
    suspend fun signInWithGoogle(context: Context): UserEntity? {
        return try {
            val credentialManager = CredentialManager.create(context)
            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(context.getString(R.string.google_web_client_id))
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val response: GetCredentialResponse = credentialManager.getCredential(context, request)
            val credential = response.credential

            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                // Exchange Google ID token for a Firebase credential
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
                val firebaseUser = authResult.user ?: return null

                val email = firebaseUser.email ?: return null
                val name = firebaseUser.displayName ?: email.substringBefore("@")

                // Store/update in local Room db
                val existing = db.userDao().getUserByEmail(email)
                if (existing != null) {
                    _currentUser.value = existing
                    existing
                } else {
                    val newUser = UserEntity(
                        email = email,
                        passwordHash = "GOOGLE_OAUTH",
                        fullName = name,
                        role = "USER",
                        activePlan = "اشتراک ویژه VIP",
                        loginProvider = "GOOGLE"
                    )
                    val id = db.userDao().insertUser(newUser)
                    val created = newUser.copy(id = id)
                    _currentUser.value = created
                    created
                }
            } else {
                null
            }
        } catch (e: GetCredentialException) {
            null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updatePassword(userId: Long, newPass: String) {
        val hashedPass = BCrypt.withDefaults().hashToString(12, newPass.toCharArray())
        val user = db.userDao().getUserById(userId) ?: return
        db.userDao().updateUser(user.copy(passwordHash = hashedPass))
    }

    suspend fun getAllAdmins(): Flow<List<UserEntity>> = db.userDao().getAllAdminsAndStaff()

    suspend fun addNewStaff(email: String, pass: String, name: String, role: String) {
        val hashedPass = BCrypt.withDefaults().hashToString(12, pass.toCharArray())
        db.userDao().insertUser(
            UserEntity(
                email = email,
                passwordHash = hashedPass,
                fullName = name,
                role = role,
                activePlan = "نامحدود",
                loginProvider = "MANUAL"
            )
        )
    }

    fun logout() {
        firebaseAuth.signOut()
        _currentUser.value = null
    }

    suspend fun initializeAdmin() {
        val adminUser = db.userDao().getUserByEmail("admin@iranbinary.ir")
        if (adminUser == null) {
            val adminPass = BuildConfig.SEED_ADMIN_PASSWORD
            if (adminPass == "CHANGE_ME_GENERATE_STRONG_PASSWORD" || adminPass.length < 12) {
                // In production this should crash or be handled strictly
            }
            val hashedPass = BCrypt.withDefaults().hashToString(12, adminPass.toCharArray())
            db.userDao().insertUser(
                UserEntity(
                    email = "admin@iranbinary.ir",
                    passwordHash = hashedPass,
                    fullName = "مدیریت ارشد ایران باینری آپشن",
                    role = "ADMIN",
                    activePlan = "یک ساله",
                    planExpiryTimestamp = System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000,
                    loginProvider = "MANUAL"
                )
            )
        }
    }
}
