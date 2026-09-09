package com.example.di

import com.example.data.auth.FirebaseAuthService
import com.example.data.local.AppDatabase
import com.example.data.local.NotificationPreferencesRepository
import com.example.data.repository.*
import com.example.ui.TradingViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Database
    single { AppDatabase.getInstance(androidContext()) }
    
    // Repositories & Services
    single { AuthRepository(get()) }
    single { SignalRepository(get()) }
    single { NewsRepository(get()) }
    single { SubscriptionRepository(get()) }
    single { TradeLogRepository(get()) }
    single { FeedbackRepository(get()) }
    single { FirestoreOfflineCacheManager(get(), androidContext()) }
    single { FirebaseAuthService(androidContext()) }
    single { NotificationPreferencesRepository.getInstance(androidContext()) }

    // ViewModel
    viewModel {
        TradingViewModel(
            application = androidApplication(),
            authRepository = get(),
            signalRepository = get(),
            newsRepository = get(),
            subscriptionRepository = get(),
            tradeLogRepository = get(),
            feedbackRepository = get(),
            offlineCacheManager = get(),
            firebaseAuthService = get(),
            notificationPreferencesRepository = get()
        )
    }
}
