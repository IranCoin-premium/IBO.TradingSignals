package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.NewsEntity
import com.example.data.local.PlanEntity
import com.example.data.local.SignalEntity
import com.example.data.local.UserEntity
import com.example.data.repository.BrokerItem
import com.example.data.repository.TradingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TradingViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TradingRepository.getInstance(application)

    val signals: StateFlow<List<SignalEntity>> = repository.allSignals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val plans: StateFlow<List<PlanEntity>> = repository.allPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val newsList: StateFlow<List<NewsEntity>> = repository.allNews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val staffList: StateFlow<List<UserEntity>> = repository.allAdmins
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
            val user = repository.registerOrLoginSocial("GOOGLE", "کاربر گرامی گوگل", "user.google@gmail.com")
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
            val user = repository.registerUser(email, pass, name)
            _currentUser.value = user
            _userPlan.value = user.activePlan
            onResult(true, "حساب کاربری با موفقیت ایجاد شد.")
        }
    }

    fun logout() {
        repository.logout()
        _currentUser.value = null
        _userPlan.value = "رایگان (Demo)"
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
                val updated = user.copy(activePlan = "${plan.title} (${plan.durationText})")
                repository.updateUser(updated)
                _currentUser.value = updated
            }
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
