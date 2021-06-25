package it.ssplus.barbershop.ui.expense

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ExpenseViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is expense Fragment"
    }
    val text: LiveData<String> = _text
}