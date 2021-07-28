package it.ssplus.barbershop.ui.service

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import it.ssplus.barbershop.model.DatabaseConfig
import it.ssplus.barbershop.model.entity.Service
import it.ssplus.barbershop.model.repository.ServiceRepository
import kotlinx.coroutines.launch

class ServiceViewModel(application: Application) : AndroidViewModel(application) {
    private val serviceRepository: ServiceRepository
    val all: LiveData<List<Service>>

    init {
        val dao = DatabaseConfig.getDatabase(application).serviceDao()
        serviceRepository = ServiceRepository(dao)
        all = serviceRepository.all
    }

    fun insert(obj: Service) = viewModelScope.launch {
        serviceRepository.insert(obj)
    }

    fun update(obj: Service) = viewModelScope.launch {
        serviceRepository.update(obj)
    }

    fun delete(obj: Service) = viewModelScope.launch {
        serviceRepository.delete(obj)
    }

    fun delete(list: List<Service>) = viewModelScope.launch {
        serviceRepository.delete(list)
    }

    fun search(query: String): LiveData<List<Service>> =
        serviceRepository.search(query)

    fun getItem(position: Int): Service? {
        return all.value?.get(position)
    }

    fun lastInserted(): LiveData<Service> {
        return serviceRepository.lastInserted()
    }
}