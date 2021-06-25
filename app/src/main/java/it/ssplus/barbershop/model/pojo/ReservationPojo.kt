package it.ssplus.barbershop.model.pojo

import androidx.room.Embedded
import androidx.room.Relation
import it.ssplus.barbershop.model.entity.*
import java.io.Serializable

data class ReservationPojo(
    @Embedded val reservation: Reservation,
    @Relation(parentColumn = "id_turn", entityColumn = "id_turn")
    val turn: Turn,
    @Relation(parentColumn = "id_service", entityColumn = "id_service")
    val service: Service

) : Serializable