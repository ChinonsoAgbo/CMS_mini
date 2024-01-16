package de.hhn.labapp.persistence.crm.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.hhn.labapp.persistence.crm.model.DatabaseProvider
import de.hhn.labapp.persistence.crm.model.DatabaseProvider.withDatabase
import de.hhn.labapp.persistence.crm.model.entities.Invoice
import kotlinx.coroutines.launch

class InvoicesOverviewViewModel : ViewModel() {
    val invoices = mutableStateOf(emptyList<Invoice>())
        @Synchronized get

    fun init() {
        viewModelScope.launch {
            loadInvoices()
        }
    }

    private fun loadInvoices() {
      //  invoices.value = Invoice.getAll()
        withDatabase {


            val temp = invoiceDao().getAll()

            // prevent concurrent modification, but do not lock the variable during
            // the database operation

            synchronized(invoices){
                invoices.value = temp
            }
        }
    }
}