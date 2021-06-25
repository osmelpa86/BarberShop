package it.ssplus.barbershop.model.entity

import androidx.room.*
import java.io.Serializable

@Entity(
    tableName = "service_type_service",
    indices = [
        Index(value = ["id_service_type_service"], unique = true),
        Index(value = ["id_service"]),
        Index(value = ["id_type_service"])],
    foreignKeys = [
        ForeignKey(
            entity = Service::class,
            parentColumns = ["id_service"],
            childColumns = ["id_service"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TypeService::class,
            parentColumns = ["id_type_service"],
            childColumns = ["id_type_service"],
            onDelete = ForeignKey.RESTRICT //can't delete a product if has asks
        )
    ]
)

data class ServiceTypeService(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_service_type_service") var idServiceTypeService: Long? = 0,
    @ColumnInfo(name = "id_service") var idService: Long,
    @ColumnInfo(name = "id_type_service") var idTypeService: Long
) : Serializable {
    constructor(idService: Long, idTypeService: Long) : this(null, idService, idTypeService)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ServiceTypeService

        if (idServiceTypeService != other.idServiceTypeService) return false

        return true
    }

    override fun hashCode(): Int {
        return idServiceTypeService.hashCode()
    }

    override fun toString(): String {
        return "ServicioTipoServicio(id=$idServiceTypeService, idService=$idService, idTypeService=$idTypeService)"
    }
}
