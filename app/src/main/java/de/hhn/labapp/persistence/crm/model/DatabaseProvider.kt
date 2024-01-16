package de.hhn.labapp.persistence.crm.model

import android.content.Context
import androidx.room.Room
import de.hhn.labapp.persistence.crm.factories.CustomerFactory
import de.hhn.labapp.persistence.crm.factories.InvoicesFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object DatabaseProvider {
    private lateinit var database: AppDatabase
        private set

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun init(context: Context) {
        database = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "exercise2"
        ).build()

        withDatabase { populateDatabase() }
    }

    fun withDatabase(block: AppDatabase.() -> Unit): Job {
        return coroutineScope.launch { database.block() }
    }

    private fun populateDatabase() {
        if (database.customerDao().getAll().isEmpty()) {
            database.customerDao().insertAll(
                CustomerFactory.createRandomCustomers(3)
            )
        }

        // TODO: You can replace this to insert the data into the database instead of the
        //       in-memory list
        if (database.invoiceDao().getAll().isEmpty()) {
            val customers = database.customerDao().getAll()

//            Invoices.insertAll(
//                InvoicesFactory.createRandomInvoices(5, customers)
//            )
            database.invoiceDao().insertAll(
                InvoicesFactory.createRandomInvoices(5, customers)
            )
        }
    }
}
