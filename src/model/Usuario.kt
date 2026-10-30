package model

// Clase base: "open" permite heredar (en Kotlin las clases son final por defecto)
open class Usuario(
    val nombre: String,
    val telefono: String,
    val correo: String
) {
    fun contacto(): String = "Tel: $telefono | Correo: $correo"

    // Polimorfismo: cada subclase redefine su descripción
    open fun describir(): String = "Usuario: $nombre"
}