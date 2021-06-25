package it.ssplus.barbershop.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import it.ssplus.barbershop.model.entity.Client
import it.ssplus.barbershop.model.entity.Service
import it.ssplus.barbershop.model.entity.ServiceTypeService
import it.ssplus.barbershop.model.entity.TypeService

@Dao
interface ServiceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(obj: Service): Long

    @Transaction
    suspend fun insert(
        obj: Service,
        listTypesService: MutableList<TypeService>,
        serviceTypeServiceDao: ServiceTypeServiceDao
    ) {
        val id = insert(obj)
        for (typeService in listTypesService) {
            var typeServiceType = ServiceTypeService(id, typeService.id)
            serviceTypeServiceDao.insert(typeServiceType)
        }
    }

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(obj: Service)

    @Transaction
    suspend fun update(
        obj: Service,
        listTypesService: MutableList<TypeService>,
        listTypesServiceDelete: MutableList<TypeService>,
        serviceTypeServiceDao: ServiceTypeServiceDao
    ) {

        for (typeService in listTypesService) {
            if (typeService.id == null) {
                var typeServiceType = ServiceTypeService(obj.id, typeService.id)
                serviceTypeServiceDao.insert(typeServiceType)
            }
        }

        if (!listTypesServiceDelete.isEmpty()) {
            for (item in listTypesServiceDelete) {
                serviceTypeServiceDao.deleteByTypeService(item.id)
            }
        }

        update(obj)
    }

    @Delete
    suspend fun delete(obj: Service)

    @Delete
    suspend fun delete(list: List<Service>)

    @Query("SELECT * FROM service ORDER BY id_service ASC")
    fun getAll(): LiveData<List<Service>>

    @Query(
        """ SELECT * FROM service WHERE 
        id_service LIKE :query 
        OR date LIKE :query
        ORDER BY id_service ASC """
    )
    fun search(query: String): LiveData<List<Service>>

    @Query("SELECT * FROM service WHERE id_service = :id_service LIMIT 1")
    fun get(id_service: Long): LiveData<Service>

    @Query("SELECT * FROM service WHERE id_service = (SELECT MAX(id_service) FROM service)")
    fun lastInserted(): LiveData<Service>
}