package model

// Cliente hereda nombre y contacto de Usuario
class Cliente(
    nombre: String,
    telefono: String,
    correo: String,
    val direccion: String
) : Usuario(nombre, telefono, correo) {

    override fun describir(): String = "Cliente: $nombre - $direccion"
}