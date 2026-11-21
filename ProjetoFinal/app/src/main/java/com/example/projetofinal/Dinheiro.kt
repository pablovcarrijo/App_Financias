package com.example.projetofinal

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

// Aceita o padrão brasileiro: 3000, 3000,00 e 3.000,00.
// Pontos são separadores de milhares; a vírgula separa os centavos.
fun converterParaCentavos(texto: String): Long? {
    val valor = texto.trim()
    val formato = Regex("(?:[0-9]+|[0-9]{1,3}(?:\\.[0-9]{3})+)(?:,[0-9]{1,2})?")
    if (!formato.matches(valor)) return null
    return try {
        BigDecimal(
            valor
                .replace(".", "").replace(',', '.')
        ).movePointRight(2).longValueExact().takeIf { it > 0 }
    } catch (_: NumberFormatException) {
        null
    } catch (_: ArithmeticException) {
        null
    }
}

fun formatarReais(centavos: Long): String =
    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
        .format(BigDecimal.valueOf(centavos, 2))
