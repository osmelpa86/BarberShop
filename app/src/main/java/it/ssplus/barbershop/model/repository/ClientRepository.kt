package it.ssplus.barbershop.model.repository

import androidx.lifecycle.LiveData
import it.ssplus.barbershop.model.dao.ClientDao
import it.ssplus.barbershop.model.entity.Client

class ClientRepository(private val clientDao: ClientDao) {
    val all: LiveData<List<Client>> = clientDao.getAll()

    fun get(id: Long): LiveData<Client> {
        return clientDao.get(id)
    }

    suspend fun insert(obj: Client) {
        clientDao.insert(obj)
    }

    suspend fun update(obj: Client) {
        clientDao.update(obj)
    }

    suspend fun delete(obj: Client) {
        clientDao.delete(obj)
    }

    suspend fun delete(list: List<Client>) {
        clientDao.delete(list)
    }

    fun search(query: String): LiveData<List<Client>> {
        return clientDao.search("%$query%")
    }

    fun lastInserted(): LiveData<Client> {
        return clientDao.lastInserted()
    }
}