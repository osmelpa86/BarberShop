package it.ssplus.barbershop.ui.expense

import android.app.Application
import androidx.lifecycle.*
import it.ssplus.barbershop.model.DatabaseConfig
import it.ssplus.barbershop.model.entity.Expense
import it.ssplus.barbershop.model.pojo.ExpensePojo
import it.ssplus.barbershop.model.repository.ExpenseRepository
import kotlinx.coroutines.launch

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    private val expenseRepository: ExpenseRepository
    val all: LiveData<List<ExpensePojo>>

    init {
        val dao = DatabaseConfig.getDatabase(application).expenseDao()
        expenseRepository = ExpenseRepository(dao)
        all = expenseRepository.all
    }

    fun insert(obj: Expense) = viewModelScope.launch {
        expenseRepository.insert(obj)
    }

    fun update(obj: Expense) = viewModelScope.launch {
        expenseRepository.update(obj)
    }

    fun delete(obj: Expense) = viewModelScope.launch {
        expenseRepository.delete(obj)
    }

    fun delete(list: List<Expense>) = viewModelScope.launch {
        expenseRepository.delete(list)
    }

    fun search(query: String): LiveData<List<ExpensePojo>> =
        expenseRepository.search(query)

    fun getExpenseCategoryByExpense(id_expene_category: Long): LiveData<List<ExpensePojo>> =
        expenseRepository.getExpenseCategoryByExpense(id_expene_category)

    fun getItem(position: Int): ExpensePojo? {
        return all.value?.get(position)
    }

    fun lastInserted(): LiveData<ExpensePojo> {
        return expenseRepository.lastInserted()
    }
}