package com.example.fintrack

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ExpensesDao {

    @Query("Select * From expensesentity")
    fun getAllExpenses(): List<ExpensesEntity>

    @Query("SELECT COALESCE(SUM(spent), 0) FROM ExpensesEntity")
    suspend fun getTotalValue(): Double

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllExpenses(expensesEntity: List<ExpensesEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExpenses(expensesEntity: ExpensesEntity)

    @Update
    fun update(expensesEntity: ExpensesEntity)

    @Delete
    fun delete(expensesEntity: ExpensesEntity)

    @Query("Select * From expensesentity where category is :categoryName")
    fun getAllByCategoryName(categoryName: String): List<ExpensesEntity>

    @Delete
    fun deleteAll(expensesEntity: List<ExpensesEntity>)

}