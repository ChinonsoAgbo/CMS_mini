package de.hhn.labapp.persistence.crm.components.invoices

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.hhn.labapp.persistence.crm.factories.InvoicesFactory
import de.hhn.labapp.persistence.crm.model.entities.Invoice
import de.hhn.labapp.persistence.crm.util.formatAmount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceCard(
    invoice: Invoice,
    onClick: (Invoice) -> Unit,
) {
    Card(
        // Fill the available width
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        onClick = { onClick(invoice) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)
            ) {
                Text(
                    text = formatAmount(invoice.amount),
                    fontSize = 16.sp
                )
                Text(
                    text = invoice.date,
                    fontSize = 12.sp
                )
            }

            Text(
                text = if (invoice.isPaid) "\uD83D\uDCB5" else "⌛",
                fontSize = 24.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

    }
}

@Preview
@Composable
fun PreviewInvoiceCard() {
    InvoiceCard(
        invoice = InvoicesFactory.createInvoice(),
        onClick = {}
    )
}
