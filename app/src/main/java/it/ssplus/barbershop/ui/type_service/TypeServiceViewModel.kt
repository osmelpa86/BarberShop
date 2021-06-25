package it.ssplus.barbershop.ui.type_service

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import it.ssplus.barbershop.model.DatabaseConfig
import it.ssplus.barbershop.model.entity.TypeService
import it.ssplus.barbershop.model.repository.TypeServiceRepository
import kotlinx.coroutines.launch

class TypeServiceViewModel(application: Application) : AndroidViewModel(application) {
    val typeServiceRepository: TypeServiceRepository = TODO()
    val all: LiveData<List<TypeService>>

    init {
        val dao = DatabaseConfig.getDatabase(application).typeServiceDao()
        typeServiceRepository = TypeServiceRepository(dao);
        all = typeServiceRepository.all
    }

    fun insert(obj: TypeService) = viewModelScope.launch {
        typeServiceRepository.insert(obj);
    }

    fun update(obj: TypeService) = viewModelScope.launch {
        typeServiceRepository.update(obj)
    }

    fun delete(obj: TypeService) = viewModelScope.launch {
        typeServiceRepository.delete(obj)
    }

    fun delete(list: List<TypeService>) = viewModelScope.launch {
        typeServiceRepository.delete(list)
    }

    fun search(query: String): LiveData<List<TypeService>> = typeServiceRepository.search(query)

    fun getItem(position: Int): TypeService? {
        return all.value?.get(position)
    }

    fun lastInserted(): LiveData<TypeService> {
        return typeServiceRepository.lastInserted()
    }
}