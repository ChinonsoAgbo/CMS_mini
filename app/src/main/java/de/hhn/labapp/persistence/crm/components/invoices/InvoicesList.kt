package de.hhn.labapp.persistence.crm.components.invoices

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.hhn.labapp.persistence.crm.components.shared.AppTitleBar
import de.hhn.labapp.persistence.crm.factories.CustomerFactory
import de.hhn.labapp.persistence.crm.factories.InvoicesFactory
import de.hhn.labapp.persistence.crm.model.entities.Invoice
import de.hhn.labapp.persistence.crm.viewmodel.CustomersOverviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesList(
    navDrawerState: DrawerState,
    invoices: Collection<Invoice>,
    onInvoiceClicked: (Invoice) -> Unit,
) {
    val viewModel = CustomersOverviewViewModel()
    LaunchedEffect(viewModel) {
        viewModel.init()
    }

    Column {
        AppTitleBar(
            title = "Invoices",
            navDrawerState = navDrawerState,
        )
        LazyColumn(
            modifier = Modifier.padding(horizontal = 4.dp),
            content = {
                val (paidInvoices, issuedInvoices) = invoices.partition { it.isPaid }

                invoicesSection(
                    title = "Issued",
                    emptyListMessage = "No issued invoices",
                    invoices = issuedInvoices,
                    onInvoiceClicked = onInvoiceClicked,
                )

                item { Spacer(modifier = Modifier.padding(8.dp)) }

                invoicesSection(
                    title = "Paid",
                    emptyListMessage = "No paid invoices",
                    invoices = paidInvoices,
                    onInvoiceClicked = onInvoiceClicked,
                )
            }
        )
    }
}

private fun LazyListScope.invoicesSection(
    title: String,
    emptyListMessage: String,
    invoices: List<Invoice>,
    onInvoiceClicked: (Invoice) -> Unit,
) {
    item {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Bold,
        )
    }

    if (invoices.isEmpty()) {
        item {
            Text(
                text = emptyListMessage,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            )
        }
    } else {
        items(invoices) { invoice ->
            InvoiceCard(
                invoice = invoice,
                onClick = onInvoiceClicked,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun InvoicesListPreview() {
    val customers = CustomerFactory.createRandomCustomers(3)

    InvoicesList(
        navDrawerState = rememberDrawerState(DrawerValue.Closed),
        invoices = InvoicesFactory.createRandomInvoices(10, customers),
        onInvoiceClicked = {},
    )
}
