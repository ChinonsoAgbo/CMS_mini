package de.hhn.labapp.persistence.crm.model

import androidx.room.Database
import androidx.room.RoomDatabase
import de.hhn.labapp.persistence.crm.model.daos.CustomerDao
import de.hhn.labapp.persistence.crm.model.daos.InvoiceDao
import de.hhn.labapp.persistence.crm.model.entities.Customer
import de.hhn.labapp.persistence.crm.model.entities.Invoice

@Database(
    entities = [
        Customer::class,
        Invoice::class
    ], version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun invoiceDao(): InvoiceDao

}
