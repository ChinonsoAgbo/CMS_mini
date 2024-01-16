package de.hhn.labapp.persistence.crm.model.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["id"],
            childColumns =  ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Invoice(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var customerId: Int,
    var amount: Int,
    var isPaid: Boolean,
    var date: String,
)
