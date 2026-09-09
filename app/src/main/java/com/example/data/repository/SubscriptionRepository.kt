package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.PlanEntity
import com.example.data.local.UserSubscriptionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

class SubscriptionRepository(
    private val db: AppDatabase
) {
    fun getPlans(): Flow<List<PlanEntity>> = db.planDao().getAllPlans()
    
    fun getSubscriptions(): Flow<List<UserSubscriptionEntity>> = db.userSubscriptionDao().getAllSubscriptions()

    suspend fun addSubscription(sub: UserSubscriptionEntity) = db.userSubscriptionDao().insertSubscription(sub)

    suspend fun recordUserSubscription(
        userId: String,
        userEmail: String,
        planTitle: String,
        durationDays: Int,
        priceToman: String,
        priceUsdt: String,
        paymentMethod: String,
        transactionRef: String
    ): UserSubscriptionEntity {
        val expiry = System.currentTimeMillis() + (durationDays.toLong() * 24 * 60 * 60 * 1000)
        val sub = UserSubscriptionEntity(
            id = java.util.UUID.randomUUID().toString(),
            userId = userId,
            userEmail = userEmail,
            planTitle = planTitle,
            durationDays = durationDays,
            priceToman = priceToman,
            priceUsdt = priceUsdt,
            paymentMethod = paymentMethod,
            transactionRef = transactionRef,
            status = "ACTIVE",
            expiryDate = expiry
        )
        db.userSubscriptionDao().insertSubscription(sub)
        return sub
    }

    suspend fun updatePlan(plan: PlanEntity) = db.planDao().updatePlan(plan)

    suspend fun seedPlans(plans: List<PlanEntity>) {
        if (db.planDao().getCount() == 0) {
            db.planDao().insertAll(plans)
        }
    }

    suspend fun seedSubscriptions(subs: List<UserSubscriptionEntity>) {
        if (db.userSubscriptionDao().getCount() == 0) {
            db.userSubscriptionDao().insertAll(subs)
        }
    }
}
