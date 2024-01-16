package de.hhn.labapp.persistence.crm.components.invoices

import android.database.sqlite.SQLiteConstraintException
import android.os.Looper
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.HandlerCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import de.hhn.labapp.persistence.crm.components.shared.AutoCompleteTextField
import de.hhn.labapp.persistence.crm.components.shared.ControlButtons
import de.hhn.labapp.persistence.crm.components.shared.PropertyTextField
import de.hhn.labapp.persistence.crm.factories.InvoicesFactory
import de.hhn.labapp.persistence.crm.model.entities.Invoice
import de.hhn.labapp.persistence.crm.util.formatAmount
import de.hhn.labapp.persistence.crm.util.parseAmount
import de.hhn.labapp.persistence.crm.viewmodel.InvoiceDetailsViewModel
import kotlinx.coroutines.launch
import java.lang.RuntimeException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceDetails(
    invoice: Invoice,
    navController: NavController,
    navDrawerState: DrawerState,
) {
    val viewModel = remember {
        InvoiceDetailsViewModel(
            invoice = invoice,
            navController = navController,
            mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper())
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = viewModel.snackbarHostState) },
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background,
        ) {
            if (viewModel.showDeleteDialog) {
                DeleteDialog(
                    onConfirm = { viewModel.onDelete() },
                    onDismiss = { viewModel.showDeleteDialog = false }
                )
            }

            InvoiceDetailsContent(invoice, viewModel)
        }
    }
}

@Composable
fun InvoiceDetailsContent(
    invoice: Invoice,
    viewModel: InvoiceDetailsViewModel,
) {
    val scope = rememberCoroutineScope()

    fun handleException(e: Exception) {
        scope.launch {
            val reason = e.message ?: "Unknown error"
            viewModel.snackbarHostState.showSnackbar("Failed to save: $reason")
        }
    }

    fun onSave() {
        try {
            viewModel.onSave()
        } catch (e: SQLiteConstraintException) {
            handleException(e)
        } catch (e: RuntimeException) {
            handleException(e)
        }
    }

    Column(
        modifier = Modifier.padding(16.dp),
    ) {
        InvoicePropertyFields(viewModel)

        Spacer(modifier = Modifier.height(16.dp))

        ControlButtons(
            onCancel = viewModel::onCancel,
            onSave = ::onSave,
            onDelete = viewModel::onDelete,
            onGenerateRandom = viewModel::onGenerateRandom,

            isSaveEnabled = viewModel.isSaveEnabled,
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "ID: ${invoice.id}",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
fun InvoicePropertyFields(viewModel: InvoiceDetailsViewModel) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CustomerPropertyField(viewModel)
        AmountPropertyField(viewModel)
        IsPaidPropertyField(viewModel)
        DatePropertyField(viewModel)
    }
}

@Composable
private fun CustomerPropertyField(viewModel: InvoiceDetailsViewModel) {
    AutoCompleteTextField(
        label = "Customer",
        items = viewModel.customers,
        selectedItem = viewModel.customers.find { it.id == viewModel.customerId },
        toString = { it.name },
        onItemSelected = { it.id?.let { id -> viewModel.customerId = id } },
    )
}

@Composable
private fun AmountPropertyField(viewModel: InvoiceDetailsViewModel) {
    val scope = rememberCoroutineScope()

    PropertyTextField(
        property = "Amount",
        value = formatAmount(viewModel.amount),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    ) {
        fun handleException(e: Exception) {
            scope.launch {
                viewModel.snackbarHostState.showSnackbar("Invalid input: ${e.message}")
            }
        }

        try {
            viewModel.amount = parseAmount(it)
        } catch (e: NumberFormatException) {
            handleException(e)
        } catch (e: IllegalArgumentException) {
            handleException(e)
        }
    }
}

@Composable
private fun IsPaidPropertyField(viewModel: InvoiceDetailsViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Is Paid",
            modifier = Modifier.weight(1f),
        )

        Switch(
            checked = viewModel.isPaid,
            onCheckedChange = { viewModel.isPaid = it },
        )
    }
}

@Composable
private fun DatePropertyField(viewModel: InvoiceDetailsViewModel) {
    PropertyTextField(
        property = "Date",
        value = viewModel.date,
    ) { viewModel.date = it }
}

@Composable
private fun DeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Invoice") },
        text = { Text("Are you sure you want to delete this invoice?") },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Delete") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

//: Preview

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun InvoiceDetailsPreview() {
    InvoiceDetails(
        invoice = InvoicesFactory.createRandomInvoice(null),
        navController = rememberNavController(),
        navDrawerState = rememberDrawerState(initialValue = DrawerValue.Open),
    )
}
