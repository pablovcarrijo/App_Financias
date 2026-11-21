package com.example.projetofinal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

const val MAXIMO_CARTOES = 5

@Entity(
    tableName = "cartoes",
    indices = [Index(value = ["indiceCor"], unique = true)]
)
data class Cartao(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val banco: String,
    val limiteCentavos: Long,
    // Independente do limite, para permitir descontar gastos futuramente.
    @ColumnInfo(defaultValue = "0") val saldoDisponivelCentavos: Long,
    val indiceCor: Int
)
