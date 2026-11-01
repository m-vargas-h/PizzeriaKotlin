package model

import util.CORREO_POR_DEFECTO

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
}