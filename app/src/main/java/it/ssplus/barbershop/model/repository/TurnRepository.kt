package it.ssplus.barbershop.model.repository

import androidx.lifecycle.LiveData
import it.ssplus.barbershop.model.dao.TurnDao
import it.ssplus.barbershop.model.entity.Turn

class TurnRepository(private val turnDao: TurnDao) {
    val all: LiveData<List<Turn>> = turnDao.getAll()

    fun get(id: Long): LiveData<Turn> {
        return turnDao.get(id)
    }

    suspend fun insert(obj: Turn) {
        turnDao.insert(obj)
    }

    suspend fun update(obj: Turn) {
        turnDao.update(obj)
    }

    suspend fun delete(obj: Turn) {
        turnDao.delete(obj)
    }

    suspend fun delete(list: List<Turn>) {
        turnDao.delete(list)
    }

    fun search(query: String): LiveData<List<Turn>> {
        return turnDao.search("%$query%")
    }

    fun lastInserted(): LiveData<Turn> {
        return turnDao.lastInserted()
    }
}