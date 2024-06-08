package model

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID

@Serializable
data class Product(val id: String, val name: String, val category: Category, val price: Double)