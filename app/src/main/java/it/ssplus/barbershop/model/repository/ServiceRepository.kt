package it.ssplus.barbershop.model.repository

import androidx.lifecycle.LiveData
import it.ssplus.barbershop.model.dao.ServiceDao
import it.ssplus.barbershop.model.entity.Service

class ServiceRepository(
    private val serviceDao: ServiceDao
) {
    val all: LiveData<List<Service>> = serviceDao.getAll()

    fun get(id: Long): LiveData<Service> {
        return serviceDao.get(id)
    }

    suspend fun insert(obj: Service) {
        serviceDao.insert(obj)
    }

    suspend fun update(obj: Service) {
        serviceDao.update(obj)
    }

    suspend fun delete(obj: Service) {
        serviceDao.delete(obj)
    }

    suspend fun delete(list: List<Service>) {
        serviceDao.delete(list)
    }

    fun search(query: String): LiveData<List<Service>> {
        return serviceDao.search("%$query%")
    }

    fun lastInserted(): LiveData<Service> {
        return serviceDao.lastInserted()
    }
}