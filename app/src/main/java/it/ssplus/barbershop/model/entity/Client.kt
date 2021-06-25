package it.ssplus.barbershop.model.entity

import androidx.room.*
import java.io.Serializable

@Entity(
    tableName = "client",
    indices = [Index(value = ["id_client"], unique = true)]
)
data class Client(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_client")
    val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "phone_number")
    val phoneNumber: String? = null,
    @ColumnInfo(name = "cell_phone")
    val cellPhone: String? = null,
    val picture: ByteArray? = null,
    val observation: String? = null,
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Client

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Cliente(id=$id, nombre='$name', télefono=$phoneNumber, celular=$cellPhone, foto=$picture, observación=$observation )"
    }
}
