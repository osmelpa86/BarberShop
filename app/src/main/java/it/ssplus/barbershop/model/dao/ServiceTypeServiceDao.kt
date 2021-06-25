package it.ssplus.barbershop.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import it.ssplus.barbershop.model.entity.ServiceTypeService
import it.ssplus.barbershop.model.pojo.ServiceTypeServicePojo

@Dao
interface ServiceTypeServiceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(obj: ServiceTypeService): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(obj: ServiceTypeService)

    @Delete
    suspend fun delete(obj: ServiceTypeService)

    @Delete
    suspend fun delete(list: List<ServiceTypeService>)

    @Query("DELETE FROM service_type_service WHERE id_type_service = :id_type_service ")
    suspend fun deleteByTypeService(id_type_service: Long)

    @Query("SELECT * FROM service_type_service ORDER BY id_service_type_service ASC")
    fun getAll(): LiveData<List<ServiceTypeService>>

    @Query("SELECT * FROM service_type_service WHERE id_service_type_service = :idServiceTypeService LIMIT 1")
    fun get(idServiceTypeService: Long): LiveData<ServiceTypeService>

    @Query("DELETE FROM service_type_service")
    suspend fun deleteAll()

    @Query("SELECT * FROM service_type_service WHERE id_service_type_service = (SELECT MAX(id_service_type_service) FROM service_type_service)")
    fun lastInserted(): LiveData<ServiceTypeService>

    @Query("SELECT * FROM service WHERE id_service = :id_service")
    fun getGenresWithSerial(id_service: Long): LiveData<ServiceTypeServicePojo>
}