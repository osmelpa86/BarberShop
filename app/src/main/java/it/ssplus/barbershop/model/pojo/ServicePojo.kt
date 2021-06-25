package it.ssplus.barbershop.model.pojo

import androidx.room.Embedded
import androidx.room.Relation
import it.ssplus.barbershop.model.entity.*
import java.io.Serializable

data class ServicePojo(
    @Embedded val service: Service,
    @Relation(parentColumn = "id_client", entityColumn = "id_client")
    val client: Client
) : Serializable