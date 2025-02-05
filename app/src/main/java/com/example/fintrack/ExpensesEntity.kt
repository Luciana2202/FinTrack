package com.example.fintrack

import androidx.annotation.DrawableRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["key"],
            childColumns = ["category"]
        )
    ]
)
data class ExpensesEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("key")
    val id: Long = 0,
    val category: String,
    val nameSpent: String,
    val spent: String,
    @DrawableRes val icon: Int
)