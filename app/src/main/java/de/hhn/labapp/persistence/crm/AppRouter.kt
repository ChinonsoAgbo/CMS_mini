package de.hhn.labapp.persistence.crm

import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import de.hhn.labapp.persistence.crm.components.customers.CustomersDetails
import de.hhn.labapp.persistence.crm.components.customers.CustomersOverview
import de.hhn.labapp.persistence.crm.components.invoices.InvoiceDetails
import de.hhn.labapp.persistence.crm.components.invoices.InvoicesOverview
import de.hhn.labapp.persistence.crm.components.shared.LoadingScreen
import de.hhn.labapp.persistence.crm.factories.CustomerFactory
import de.hhn.labapp.persistence.crm.factories.InvoicesFactory
import de.hhn.labapp.persistence.crm.model.DatabaseProvider.withDatabase
import de.hhn.labapp.persistence.crm.model.entities.Customer
import de.hhn.labapp.persistence.crm.model.entities.Invoice
import de.hhn.labapp.persistence.crm.viewmodel.Screen

class AppRouter @OptIn(ExperimentalMaterial3Api::class) constructor(
    private val navController: NavController,
    private val navDrawerState: DrawerState,
) {
    fun build(navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable(Screen.CustomersOverview.name) { listCustomers() }
        navGraphBuilder.composable(Screen.CustomerDetails.name) { createCustomer() }
        navGraphBuilder.composable(
            "${Screen.CustomerDetails.name}/{id}"
        ) { entry -> editCustomer(entry) }

        navGraphBuilder.composable(Screen.InvoicesOverview.name) { listInvoices() }
        navGraphBuilder.composable(Screen.InvoiceDetails.name) { createInvoice() }
        navGraphBuilder.composable(
            "${Screen.InvoiceDetails.name}/{id}"
        ) { entry -> editInvoice(entry) }
    }

    //: Customers

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun listCustomers() {
        CustomersOverview(navController, navDrawerState)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun createCustomer() {
        val customer = CustomerFactory.createCustomer()
        CustomersDetails(customer, navController, navDrawerState)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun editCustomer(entry: NavBackStackEntry) {
        fun cancel() {
            navController.popBackStack()
        }

        suspend fun prepareNavigate(): Customer? {
            val id = entry.arguments?.getString("id")?.toIntOrNull()
            if (id == null) {
                cancel()
                return null
            }

            var customer: Customer? = null
            withDatabase { customer = customerDao().get(id) }.join()
            return customer
        }

        waitOn(::prepareNavigate) { customer ->
            if (customer == null) {
                cancel()
                return@waitOn
            }

            CustomersDetails(customer, navController, navDrawerState)
        }
    }

    //: Invoices

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun listInvoices() {
        InvoicesOverview(navController, navDrawerState)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun createInvoice() {
        val invoice = InvoicesFactory.createInvoice(null)
        InvoiceDetails(invoice, navController, navDrawerState)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun editInvoice(entry: NavBackStackEntry) {
        fun cancel() {
            navController.popBackStack()
        }

        suspend fun prepareNavigate(): Invoice? {
            val id = entry.arguments?.getString("id")?.toIntOrNull()
            if (id == null) {
                cancel()
                return null
            }

            var invoice: Invoice? = null
            withDatabase {
                invoice = invoiceDao().getInvoiceById(id)
            }.join()

            return invoice
        }

        waitOn(::prepareNavigate) { invoice ->
            if (invoice == null) {
                cancel()
                return@waitOn
            }

            InvoiceDetails(invoice, navController, navDrawerState)
        }
    }

    //: Helpers

    @Composable
    private fun <T> waitOn(
        prepare: suspend () -> T,
        content: @Composable (T) -> Unit
    ) {
        suspend fun invokePrepare(onCompleted: (T) -> Unit) {
            onCompleted(prepare())
        }

        var t: T? by remember { mutableStateOf(null) }

        LaunchedEffect(Unit) {
            invokePrepare { t = it }
        }

        if (t == null) {
            LoadingScreen()
        } else {
            content(t!!)
        }
    }
}
