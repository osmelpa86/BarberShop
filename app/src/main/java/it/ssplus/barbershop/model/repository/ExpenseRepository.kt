package it.ssplus.barbershop.model.repository

import androidx.lifecycle.LiveData
import it.ssplus.barbershop.model.dao.ExpenseDao
import it.ssplus.barbershop.model.entity.Expense
import it.ssplus.barbershop.model.pojo.ExpensePojo

class ExpenseRepository(private val expenseDao: ExpenseDao) {
    val all: LiveData<List<ExpensePojo>> = expenseDao.getAll()


    fun getExpenseCategoryByExpense(id_expene_category: Long): LiveData<List<ExpensePojo>>{
       return expenseDao.getExpenseCategoryByExpense(id_expene_category)
    }

    fun get(id: Long): LiveData<ExpensePojo> {
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

    fun search(query: String): LiveData<List<ExpensePojo>> {
        return expenseDao.search("%$query%")
    }

    fun lastInserted(): LiveData<ExpensePojo> {
        return expenseDao.lastInserted()
    }
}