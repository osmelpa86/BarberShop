package it.ssplus.barbershop.ui.client

import android.app.Application
import androidx.lifecycle.*
import it.ssplus.barbershop.model.DatabaseConfig
import it.ssplus.barbershop.model.entity.Client
import it.ssplus.barbershop.model.repository.ClientRepository
import kotlinx.coroutines.launch

class ClientViewModel(application: Application) : AndroidViewModel(application) {
    private val clientRepository: ClientRepository
    val all: LiveData<List<Client>>

    init {
        val dao = DatabaseConfig.getDatabase(application).clientDao()
        clientRepository = ClientRepository(dao)
        all = clientRepository.all
    }

    fun insert(obj: Client) = viewModelScope.launch {
        clientRepository.insert(obj)
    }

    fun update(obj: Client) = viewModelScope.launch {
        clientRepository.update(obj)
    }

    fun delete(obj: Client) = viewModelScope.launch {
        clientRepository.delete(obj)
    }

    fun delete(list: List<Client>) = viewModelScope.launch {
        clientRepository.delete(list)
    }

    fun search(query: String): LiveData<List<Client>> =
        clientRepository.search(query)

    fun getItem(position: Int): Client? {
        return all.value?.get(position)
    }

    fun lastInserted(): LiveData<Client> {
        return clientRepository.lastInserted()
    }
}