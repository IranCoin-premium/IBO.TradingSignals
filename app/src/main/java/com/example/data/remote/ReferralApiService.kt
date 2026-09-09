package com.example.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/*
 * PART 12 — Referral & Sales Partnership network layer (server-authoritative).
 *
 * RULES (enforced by scripts/verify-part12.sh and PART12_CORRECTION_DIRECTIVE.md):
 *  - NEVER generate referral codes, balances or commissions on the device.
 *  - ALL numbers rendered in UI come exclusively from these endpoints.
 *  - BASE_URL comes from BuildConfig.BACKEND_BASE_URL (injected by caller).
 *  - Backend error codes surface verbatim: SELF_REFERRAL_BLOCKED, BELOW_MIN_PAYOUT,
 *    INSUFFICIENT_BALANCE, INVALID_PAYOUT_TRANSITION.
 */

// ===== Shared envelope: { status: "success", data: ... } =====
private const val STATUS_SUCCESS = "success"

// ---- GET /api/v1/referral/my-code -> data: { code, created_at } ----
data class ReferralCodeDto(val code: String, val created_at: String? = null)

// ---- GET /api/v1/referral/dashboard -> data (referral.service.getDashboard) ----
data class PayoutRowDto(
    val id: String,
    val amount: String,
    val currency: String,
    val status: String,
    val method: String,
    val created_at: String? = null,
    val decided_at: String? = null,
    val note: String? = null
)

data class DashboardDto(
    val hasCode: Boolean,
    val code: String?,
    val clicks: Int,
    val conversions: Int,
    val balance: String,
    val currency: String,
    val payouts: List<PayoutRowDto>
)

// ---- GET /api/v1/referral/partnership -> landing data (referral.service.getLandingData) ----
data class CommissionTierDto(val tier: String, val percent: Double)

data class PartnershipLandingDto(
    val program: String,
    val risk_disclosure: String,
    val income_guarantees: String,
    val commission_tiers: List<CommissionTierDto>,
    val how_it_works: List<String>
)

// ---- GET /api/v1/tabs/manifest -> data.tabs[] (Tab Registry) ----
data class PlatformTabDto(
    val tab_key: String,
    val title_i18n_key: String,
    val route: String,
    val icon: String,
    val audience: String,
    val status: String,
    val feature_flags: List<String>
)

// ---- GET /api/v1/tabs/manifest -> data: { readiness, integration_target, tabs[] } ----
data class TabsManifestDto(
    val readiness: String,
    val integration_target: String? = null,
    val tabs: List<PlatformTabDto> = emptyList()
)

// ---- POST /api/v1/referral/payouts ----
data class PayoutRequestDto(val amount: Double, val method: String, val currency: String = "USD")
data class PayoutCreatedDto(val id: String, val status: String)

// ===== Unified backend envelope: { status: "success", data: ... } =====
data class ApiEnvelope<T>(val status: String, val data: T)

// ===== Retrofit service — single network source of truth for Part 12 client =====
interface ReferralApiService {
    @GET("api/v1/referral/partnership")
    suspend fun getPartnershipLanding(): ApiEnvelope<PartnershipLandingDto>

    @GET("api/v1/referral/my-code")
    suspend fun getMyCode(): ApiEnvelope<ReferralCodeDto>

    @GET("api/v1/referral/dashboard")
    suspend fun getDashboard(): ApiEnvelope<DashboardDto>

    @GET("api/v1/tabs/manifest")
    suspend fun getTabsManifest(): ApiEnvelope<TabsManifestDto>

    @POST("api/v1/referral/payouts")
    suspend fun requestPayout(@Body body: PayoutRequestDto): ApiEnvelope<PayoutCreatedDto>
}

// ===== Factory — caller injects BuildConfig.BASE_URL and the Bearer-token interceptor =====
object ReferralApiFactory {
    fun create(baseUrl: String, authInterceptor: okhttp3.Interceptor, debugLogging: Boolean = false): ReferralApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .apply {
                if (debugLogging) addInterceptor(
                    okhttp3.logging.HttpLoggingInterceptor().apply { level = okhttp3.logging.HttpLoggingInterceptor.Level.BASIC }
                )
            }
            .build()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().add(KotlinJsonAdapterFactory()).build()))
            .build()
            .create(ReferralApiService::class.java)
    }
}
