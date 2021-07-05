package it.ssplus.barbershop.model.repository

import androidx.lifecycle.LiveData
import it.ssplus.barbershop.model.dao.TypeServiceDao
import it.ssplus.barbershop.model.entity.TypeService

class TypeServiceRepository(private val typeServiceDao: TypeServiceDao) {
    val all: LiveData<List<TypeService>> = typeServiceDao.getAll()

    fun get(id: Long): LiveData<TypeService> {
        return typeServiceDao.get(id)
    }

    suspend fun insert(obj: TypeService) {
        typeServiceDao.insert(obj)
    }

    suspend fun update(obj: TypeService) {
        typeServiceDao.update(obj)
    }

    suspend fun delete(obj: TypeService) {
        typeServiceDao.delete(obj)
    }

    suspend fun delete(list: List<TypeService>) {
        typeServiceDao.delete(list)
    }

    fun search(query: String): LiveData<List<TypeService>> {
        return typeServiceDao.search("%$query%")
    }

    fun lastInserted(): LiveData<TypeService> {
        return typeServiceDao.lastInserted()
    }
}