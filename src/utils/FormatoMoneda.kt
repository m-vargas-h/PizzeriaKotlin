package utils

import java.util.Locale

// Montos en pesos chilenos enteros (Long)

// Formato de moneda chilena: $8.990
fun formatearPesos(monto: Long): String =
    "$" + String.format(Locale.forLanguageTag("es-CL"), "%,d", monto)

// División entera redondeada al peso más cercano
fun dividirRedondeado(dividendo: Long, divisor: Long): Long = (dividendo + divisor / 2) / divisor

// Porcentaje de un monto, redondeado a pesos enteros
fun aplicarPorcentaje(monto: Long, porcentaje: Int): Long = dividirRedondeado(monto * porcentaje, 100)