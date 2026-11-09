package model

import java.time.LocalDate

// Los pedidos con fecha dentro del rango reciben descuento sobre su total
object PeriodoPromocion {
    const val NOMBRE = "Promo Noviembre"
    const val PORCENTAJE_DESCUENTO = 15
    val INICIO: LocalDate = LocalDate.of(2026, 11, 1)
    val FIN: LocalDate = LocalDate.of(2026, 11, 30)

    // Range de fechas creado con el operador ..
    val RANGO = INICIO..FIN

    fun estaVigente(fecha: LocalDate): Boolean = fecha in RANGO
}