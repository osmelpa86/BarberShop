package it.ssplus.barbershop.model.repository

import androidx.lifecycle.LiveData
import it.ssplus.barbershop.model.dao.ExpenseDao
import it.ssplus.barbershop.model.entity.Expense

class ExpenseRepository(private val expenseDao: ExpenseDao) {
    val all: LiveData<List<Expense>> = expenseDao.getAll()

    fun get(id: Long): LiveData<Expense> {
        return expenseDao.get(id)
    }

    suspend fun insert(obj: Expense) {
        expenseDao.insert(obj)
    }

    suspend fun update(obj: Expense) {
        expenseDao.update(obj)
    }

    suspend fun delete(obj: Expense) {
        expenseDao.delete(obj)
    }

    suspend fun delete(list: List<Expense>) {
        expenseDao.delete(list)
    }

    fun search(query: String): LiveData<List<Expense>> {
        return expenseDao.search("%$query%")
    }

    fun lastInserted(): LiveData<Expense> {
        return expenseDao.lastInserted()
    }
}