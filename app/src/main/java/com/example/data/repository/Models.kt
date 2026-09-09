package com.example.data.repository

data class BrokerItem(
    val id: String,
    val name: String,
    val faName: String,
    val payoutRate: String,
    val otc247: Boolean,
    val executionSpeed: String,
    val minDeposit: String,
    val status: String,
    val badge: String,
    val description: String
)
