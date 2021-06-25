package it.ssplus.barbershop.model.repository

import androidx.lifecycle.LiveData
import androidx.room.Query
import it.ssplus.barbershop.model.dao.ExpenseCategoryDao
import it.ssplus.barbershop.model.entity.ExpenseCategory

class ExpenseCategoryRepository(private val expenseCategoryDao: ExpenseCategoryDao) {
    val all: LiveData<List<ExpenseCategory>> = expenseCategoryDao.getAll()
    val allExpenseCategoryNames: LiveData<List<String>> =
        expenseCategoryDao.getAllExpenseCategoryNames()

    fun get(id: Long): LiveData<ExpenseCategory> {
        return expenseCategoryDao.get(id)
    }

    suspend fun insert(obj: ExpenseCategory) {
        expenseCategoryDao.insert(obj)
    }

    suspend fun update(obj: ExpenseCategory) {
        expenseCategoryDao.update(obj)
    }

    suspend fun delete(obj: ExpenseCategory) {
        expenseCategoryDao.delete(obj)
    }

    suspend fun delete(list: List<ExpenseCategory>) {
        expenseCategoryDao.delete(list)
    }

    fun search(query: String): LiveData<List<ExpenseCategory>> {
        return expenseCategoryDao.search("%$query%")
    }

    fun lastInserted(): LiveData<ExpenseCategory> {
        return expenseCategoryDao.lastInserted()
    }
}