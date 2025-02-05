package com.example.fintrack

import androidx.annotation.DrawableRes

data class ExpensesUiData(
    val id: Long,
    val nameSpent: String,
    val spent: String,
    val category: String,
    @DrawableRes val icon: Int
)