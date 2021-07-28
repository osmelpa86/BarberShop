package it.ssplus.barbershop.model.pojo

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import it.ssplus.barbershop.model.entity.*
import java.io.Serializable

data class ReservationPojo(
    @Embedded val reservation: Reservation,
    @Relation(parentColumn = "id_turn", entityColumn = "id_turn")
    val turn: Turn,
    @Relation(parentColumn = "id_client", entityColumn = "id_client")
    val client: Client,
    @Relation(
        parentColumn = "id_reservation",
        entity = Service::class,
        entityColumn = "id_service",
        associateBy = Junction(
            value = ReservationService::class,
            parentColumn = "id_reservation",
            entityColumn = "id_service",
        )
    )
    val services: List<Service>
): Serializable