package it.ssplus.barbershop.ui.reservation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import it.ssplus.barbershop.model.DatabaseConfig
import it.ssplus.barbershop.model.entity.Reservation
import it.ssplus.barbershop.model.entity.Service
import it.ssplus.barbershop.model.pojo.ReservationPojo
import it.ssplus.barbershop.model.repository.ReservationRepository
import kotlinx.coroutines.launch

class ReservationViewModel(application: Application) : AndroidViewModel(application) {
    private val reservationRepository: ReservationRepository

    val all: LiveData<List<ReservationPojo>>

    init {
        val dao = DatabaseConfig.getDatabase(application).reservationDao()
        val daoReservationService = DatabaseConfig.getDatabase(application).reservationServiceDao()
        reservationRepository = ReservationRepository(dao, daoReservationService)
        all = reservationRepository.all
    }

    fun insert(obj: Reservation, serviceList: MutableList<Service>) = viewModelScope.launch {
        reservationRepository.insert(obj, serviceList)
    }

    fun update(
        obj: Reservation,
        servies: MutableList<Service>,
        serviesDelete: MutableList<Service>
    ) = viewModelScope.launch {
        reservationRepository.update(obj, servies, serviesDelete)
    }

    fun delete(obj: Reservation) = viewModelScope.launch {
        reservationRepository.delete(obj)
    }

    fun delete(list: List<Reservation>) = viewModelScope.launch {
        reservationRepository.delete(list)
    }

    fun search(query: String): LiveData<List<ReservationPojo>> =
        reservationRepository.search(query)

    fun getItem(position: Int): ReservationPojo? {
        return all.value?.get(position)
    }

    fun lastInserted(): LiveData<Reservation> {
        return reservationRepository.lastInserted()
    }
}