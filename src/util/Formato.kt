package util

import java.util.Locale

// Formato de moneda chilena: $8.990
fun formatearPesos(monto: Double): String =
    "$" + String.format(Locale.forLanguageTag("es-CL"), "%,.0f", monto)

fun formatearPesos(monto: Int): String = formatearPesos(monto.toDouble())