package it.ssplus.barbershop.model.pojo

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import it.ssplus.barbershop.model.entity.Service
import it.ssplus.barbershop.model.entity.ServiceTypeService
import it.ssplus.barbershop.model.entity.TypeService

data class ServiceTypeServicePojo(
    @Embedded val service: Service,
    @Relation(
        parentColumn = "id_service",
        entity = TypeService::class,
        entityColumn = "id_type_service",
        associateBy = Junction(
            value = ServiceTypeService::class,
            parentColumn = "id_service",
            entityColumn = "id_type_service"
        )
    )
    val typeServiceList: List<TypeService>
)