package it.ssplus.barbershop.model.repository

import androidx.lifecycle.LiveData
import it.ssplus.barbershop.model.dao.ReservationDao
import it.ssplus.barbershop.model.dao.ReservationServiceDao
import it.ssplus.barbershop.model.entity.Reservation
import it.ssplus.barbershop.model.entity.Service
import it.ssplus.barbershop.model.pojo.ReservationPojo

class ReservationRepository(
    private val reservationDao: ReservationDao,
    private val reservationServiceDao: ReservationServiceDao
) {
    val all: LiveData<List<ReservationPojo>> = reservationDao.getAll()

    fun get(id: Long): LiveData<ReservationPojo> {
        return reservationDao.get(id)
    }

    suspend fun insert(obj: Reservation) {
        reservationDao.insert(obj)
    }

    suspend fun insert(obj: Reservation, services: MutableList<Service>) {
        reservationDao.insert(obj, services, reservationServiceDao)
    }

    suspend fun update(obj: Reservation) {
        reservationDao.update(obj)
    }

    suspend fun update(
        obj: Reservation, services: MutableList<Service>, servicesDelete: MutableList<Service>
    ) {
        reservationDao.update(obj, services, servicesDelete, reservationServiceDao)
    }

    suspend fun delete(obj: Reservation) {
        reservationDao.delete(obj)
    }

    suspend fun delete(list: List<Reservation>) {
        reservationDao.delete(list)
    }

    fun search(query: String): LiveData<List<ReservationPojo>> {
        return reservationDao.search("%$query%")
    }

    fun lastInserted(): LiveData<Reservation> {
        return reservationDao.lastInserted()
    }
}