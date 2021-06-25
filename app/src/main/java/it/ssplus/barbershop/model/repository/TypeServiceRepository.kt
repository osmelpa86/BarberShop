package it.ssplus.barbershop.model.repository

import androidx.lifecycle.LiveData
import it.ssplus.barbershop.model.dao.TypeServiceDao
import it.ssplus.barbershop.model.entity.TypeService

class TypeServiceRepository(private val rypeServiceDao: TypeServiceDao) {
    val all: LiveData<List<TypeService>> = rypeServiceDao.getAll()

    fun get(id: Long): LiveData<TypeService> {
        return rypeServiceDao.get(id)
    }

    suspend fun insert(obj: TypeService) {
        rypeServiceDao.insert(obj)
    }

    suspend fun update(obj: TypeService) {
        rypeServiceDao.update(obj)
    }

    suspend fun delete(obj: TypeService) {
        rypeServiceDao.delete(obj)
    }

    suspend fun delete(list: List<TypeService>) {
        rypeServiceDao.delete(list)
    }

    fun search(query: String): LiveData<List<TypeService>> {
        return rypeServiceDao.search("%$query%")
    }

    fun lastInserted(): LiveData<TypeService> {
        return rypeServiceDao.lastInserted()
    }
}