package de.hhn.labapp.persistence.crm.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Customer(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    var name: String,
    var address: String,
    var city: String,
    var postalCode: String,
    var country: String
)
