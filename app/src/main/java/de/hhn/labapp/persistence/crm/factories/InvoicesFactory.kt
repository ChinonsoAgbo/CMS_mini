package de.hhn.labapp.persistence.crm.factories

import de.hhn.labapp.persistence.crm.model.entities.Customer
import de.hhn.labapp.persistence.crm.model.entities.Invoice
import de.hhn.labapp.persistence.crm.util.toLocalDateString
import de.hhn.labapp.persistence.crm.util.utcDateTime
import de.hhn.labapp.persistence.crm.util.utcNow

object InvoicesFactory {
    fun createInvoice(customer: Customer? = null): Invoice {
        if (customer != null && customer.id == null) {
            throw IllegalArgumentException("Customer must be persisted and have an id")
        }

        return Invoice(
            customerId = customer?.id ?: 0,
            amount = 0,
            isPaid = false,
            date = ""
        )
    }

    fun createRandomInvoice(customer: Customer?): Invoice {
        return Invoice(
            customerId = customer?.id ?: 0,
            amount = randomAmount(),
            isPaid = randomIsPaid(),
            date = randomDate(),
        )
    }

    fun createRandomInvoices(count: Int, customers: List<Customer>): List<Invoice> {
        return (1..count).map { createRandomInvoice(customers.random()) }
    }

    fun randomAmount(): Int {
        return (1..1000_00).random()
    }

    fun randomIsPaid(): Boolean {
        return booleanArrayOf(true, false).random()
    }

    fun randomDate(): String {
        val earliestDate = utcDateTime(year = 2020, month = 1, day = 1).toEpochSecond()
        val latestDate = utcNow().toEpochSecond()
        val randomDate = (earliestDate..latestDate).random()

        return toLocalDateString(randomDate)
    }
}