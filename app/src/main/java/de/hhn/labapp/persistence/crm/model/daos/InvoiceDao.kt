package de.hhn.labapp.persistence.crm.model.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import de.hhn.labapp.persistence.crm.model.entities.Invoice

@Dao
interface InvoiceDao {

    @Query("SELECT * FROM Invoice WHERE id = :id")
    fun getInvoiceById(id: Int): Invoice?

    @Query("SELECT * FROM Invoice")
     fun getAll(): List<Invoice>

    @Query("DELETE FROM Invoice WHERE id = :id")
     fun deleteInvoiceById(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insert(invoice: Invoice)

    @Update
   fun update(invoice: Invoice)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(invoices: List<Invoice>)

}
