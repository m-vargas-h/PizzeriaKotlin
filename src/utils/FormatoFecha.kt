package utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val FORMATO_FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy")

fun formatearFecha(fecha: LocalDate): String = fecha.format(FORMATO_FECHA)