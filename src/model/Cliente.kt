package model

import utils.CORREO_POR_DEFECTO

// Cliente hereda nombre y contacto de Usuario
class Cliente(
    nombre: String,
    telefono: String,
    correo: String,
    val direccion: String
) : Usuario(nombre, telefono, correo) {

    override fun describir(): String = "Cliente: $nombre - $direccion"

    // null si el correo no es válido; se usa con ?.let para notificar
    fun correoParaNotificar(): String? = correo.takeIf { it != CORREO_POR_DEFECTO }

    // Desestructuración: val (nombre, correo, telefono) = cliente
    // Se define manualmente porque Cliente hereda de Usuario y no puede ser data class
    operator fun component1(): String = nombre
    operator fun component2(): String = correo
    operator fun component3(): String = telefono

    // Igualdad: mismo nombre (sin importar mayúsculas) y mismo correo
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cliente) return false
        return nombre.equals(other.nombre, ignoreCase = true) && correo == other.correo
    }

    // hashCode usa los mismos atributos que equals
    override fun hashCode(): Int = 31 * nombre.lowercase().hashCode() + correo.hashCode()
}