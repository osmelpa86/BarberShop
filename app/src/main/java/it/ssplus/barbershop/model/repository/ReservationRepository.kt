package it.ssplus.barbershop.model.repository

import androidx.lifecycle.LiveData
import it.ssplus.barbershop.model.dao.ReservationDao
import it.ssplus.barbershop.model.entity.Reservation

class ReservationRepository(private val reservationDao: ReservationDao) {
    val all: LiveData<List<Reservation>> = reservationDao.getAll()

    fun get(id: Long): LiveData<Reservation> {
        return reservationDao.get(id)
    }

    suspend fun insert(obj: Reservation) {
        reservationDao.insert(obj)
    }

    suspend fun update(obj: Reservation) {
        reservationDao.update(obj)
    }

    suspend fun delete(obj: Reservation) {
        reservationDao.delete(obj)
    }

    suspend fun delete(list: List<Reservation>) {
        reservationDao.delete(list)
    }

    fun search(query: String): LiveData<List<Reservation>> {
        return reservationDao.search("%$query%")
    }

    fun lastInserted(): LiveData<Reservation> {
        return reservationDao.lastInserted()
    }
}