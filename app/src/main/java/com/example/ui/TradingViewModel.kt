package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.FirebaseAuthService
import com.example.data.local.NewsEntity
import com.example.data.local.PlanEntity
import com.example.data.local.SignalEntity
import com.example.data.local.UserEntity
import com.example.data.local.UserSubscriptionEntity
import com.example.data.repository.BrokerItem
import com.example.data.repository.OfflineCacheSyncStatus
import com.example.data.repository.TradingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TradingViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TradingRepository.getInstance(application)
    private val firebaseAuthService = FirebaseAuthService(application)

    val signals: StateFlow<List<SignalEntity>> = repository.allSignals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val historicalSignals: StateFlow<List<SignalEntity>> = repository.historicalSignals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeSignals: StateFlow<List<SignalEntity>> = repository.activeSignals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val plans: StateFlow<List<PlanEntity>> = repository.allPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subscriptions: StateFlow<List<UserSubscriptionEntity>> = repository.allSubscriptions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val newsList: StateFlow<List<NewsEntity>> = repository.allNews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val staffList: StateFlow<List<UserEntity>> = repository.allAdmins
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val offlineCacheStatus: StateFlow<OfflineCacheSyncStatus> = repository.offlineCacheStatus

    val brokers: List<BrokerItem> = repository.binaryBrokers

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _userPlan = MutableStateFlow("رایگان (Demo)")
    val userPlan: StateFlow<String> = _userPlan.asStateFlow()

    init {
        repository.initializeSeedData(viewModelScope)
    }

    fun login(email: String, pass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            // Attempt Firebase Auth sign-in
            firebaseAuthService.signInWithEmail(email, pass)

            // Authenticate with local/admin user repository
            val user = repository.authenticateUser(email, pass)
            if (user != null) {
                _currentUser.value = user
                _userPlan.value = user.activePlan
                onResult(true, "ورود با موفقیت انجام شد.")
            } else {
                onResult(false, "اطلاعات ورود نادرست است.")
            }
        }
    }

    fun loginWithGoogle() {
        viewModelScope.launch {
            val googleResult = firebaseAuthService.signInWithGoogleCredentialManager()
            val email = googleResult.getOrNull()?.email ?: "user.google@gmail.com"
            val name = googleResult.getOrNull()?.displayName ?: "کاربر گرامی گوگل"
            val user = repository.registerOrLoginSocial("GOOGLE", name, email)
            _currentUser.value = user
            _userPlan.value = user.activePlan
        }
    }

    fun loginWithGitHub() {
        viewModelScope.launch {
            val user = repository.registerOrLoginSocial("GITHUB", "کاربر توسعه‌دهنده گیت‌هاب", "developer@github.com")
            _currentUser.value = user
            _userPlan.value = user.activePlan
        }
    }

    fun registerManual(email: String, pass: String, name: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            // Attempt Firebase Auth sign-up
            firebaseAuthService.signUpWithEmail(email, pass)

            val user = repository.registerUser(email, pass, name)
            _currentUser.value = user
            _userPlan.value = user.activePlan
            onResult(true, "حساب کاربری با موفقیت ایجاد شد.")
        }
    }

    fun logout() {
        viewModelScope.launch {
            firebaseAuthService.signOut()
            repository.logout()
            _currentUser.value = null
            _userPlan.value = "رایگان (Demo)"
        }
    }

    fun updateAdminPassword(newPass: String) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            repository.updatePassword(user.id, newPass)
            _currentUser.value = user.copy(passwordHash = newPass)
        }
    }

    fun addNewStaff(email: String, pass: String, name: String, role: String) {
        viewModelScope.launch {
            repository.addNewStaff(email, pass, name, role)
        }
    }

    fun addSignal(signal: SignalEntity) {
        viewModelScope.launch {
            repository.addSignal(signal)
        }
    }

    fun deleteSignal(id: Long) {
        viewModelScope.launch {
            repository.deleteSignal(id)
        }
    }

    fun updateSignalStatus(signal: SignalEntity, status: String) {
        viewModelScope.launch {
            repository.updateSignal(signal.copy(status = status))
        }
    }

    fun updatePlan(plan: PlanEntity) {
        viewModelScope.launch {
            repository.updatePlan(plan)
        }
    }

    fun buyPlan(plan: PlanEntity) {
        _userPlan.value = "${plan.title} (${plan.durationText})"
        val user = _currentUser.value
        if (user != null) {
            viewModelScope.launch {
                val days = when {
                    "یک هفته" in plan.durationText -> 7
                    "یک ماه" in plan.durationText -> 30
                    "سه ماه" in plan.durationText -> 90
                    "شش ماه" in plan.durationText -> 180
                    "یک سال" in plan.durationText -> 365
                    else -> 30
                }
                repository.recordUserSubscription(
                    planTitle = "${plan.title} (${plan.durationText})",
                    durationDays = days,
                    priceToman = plan.priceToman,
                    priceUsdt = plan.priceUsdt,
                    paymentMethod = "CRYPTO_USDT",
                    transactionRef = "TX-IB-${System.currentTimeMillis() % 1000000}"
                )
            }
        }
    }

    fun syncOfflineCache() {
        viewModelScope.launch {
            repository.syncOfflineCacheWithCloud()
        }
    }

    fun addNews(news: NewsEntity) {
        viewModelScope.launch {
            repository.addNews(news)
        }
    }

    fun deleteNews(id: Long) {
        viewModelScope.launch {
            repository.deleteNews(id)
        }
    }

    fun runAiAgent(prompt: String): String {
        return repository.runAiEditorAgent(prompt)
    }
}
