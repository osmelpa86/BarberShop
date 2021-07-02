package it.ssplus.barbershop.ui.turn

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import it.ssplus.barbershop.model.DatabaseConfig
import it.ssplus.barbershop.model.entity.Turn
import it.ssplus.barbershop.model.repository.TurnRepository
import kotlinx.coroutines.launch

class TurnViewModel(application: Application) : AndroidViewModel(application) {
    private val turnRepository: TurnRepository
    val all: LiveData<List<Turn>>

    init {
        val dao = DatabaseConfig.getDatabase(application).turnDao()
        turnRepository = TurnRepository(dao)
        all = turnRepository.all
    }

    fun insert(obj: Turn) = viewModelScope.launch {
        turnRepository.insert(obj)
    }

    fun update(obj: Turn) = viewModelScope.launch {
        turnRepository.update(obj)
    }

    fun delete(obj: Turn) = viewModelScope.launch {
        turnRepository.delete(obj)
    }

    fun delete(list: List<Turn>) = viewModelScope.launch {
        turnRepository.delete(list)
    }

    fun search(query: String): LiveData<List<Turn>> =
        turnRepository.search(query)

    fun getItem(position: Int): Turn? {
        return all.value?.get(position)
    }

    fun lastInserted(): LiveData<Turn> {
        return turnRepository.lastInserted()
    }
}