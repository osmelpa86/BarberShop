package it.ssplus.barbershop.ui.expense_category

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import it.ssplus.barbershop.model.DatabaseConfig
import it.ssplus.barbershop.model.entity.ExpenseCategory
import it.ssplus.barbershop.model.repository.ExpenseCategoryRepository
import kotlinx.coroutines.launch

class ExpenseCategoryViewModel(application: Application) : AndroidViewModel(application) {
    val expenseCategoryRepository: ExpenseCategoryRepository
    val all: LiveData<List<ExpenseCategory>>
    val allExpenseCategoryNames: LiveData<List<String>>

    init {
        val dao = DatabaseConfig.getDatabase(application).expenseCategoryDao()
        expenseCategoryRepository = ExpenseCategoryRepository(dao)
        all = expenseCategoryRepository.all
        allExpenseCategoryNames = expenseCategoryRepository.allExpenseCategoryNames
    }

    fun insert(obj: ExpenseCategory) = viewModelScope.launch {
        expenseCategoryRepository.insert(obj);
    }

    fun update(obj: ExpenseCategory) = viewModelScope.launch {
        expenseCategoryRepository.update(obj)
    }

    fun delete(obj: ExpenseCategory) = viewModelScope.launch {
        expenseCategoryRepository.delete(obj)
    }

    fun delete(list: List<ExpenseCategory>) = viewModelScope.launch {
        expenseCategoryRepository.delete(list)
    }

    fun search(query: String): LiveData<List<ExpenseCategory>> =
        expenseCategoryRepository.search(query)

    fun getItem(position: Int): ExpenseCategory? {
        return all.value?.get(position)
    }

    fun lastInserted(): LiveData<ExpenseCategory> {
        return expenseCategoryRepository.lastInserted()
    }
}