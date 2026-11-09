package model

import utils.formatearPesos

// Clase base de todos los productos del menú
// precio: incluye IVA
// ingredientes: nombre del ingrediente -> cantidad necesaria por unidad (vacío si no usa inventario)
abstract class Producto(
    val nombre: String,
    val precio: Long,
    val ingredientes: Map<String, Int>
) {
    // Cada subclase define su categoría
    abstract val categoria: CategoriaProducto

    init {
        require(precio > 0) { "El precio de $nombre debe ser mayor a 0 (recibido: $precio)." }
    }

    // Cantidad de pizzas que aporta una unidad (para el descuento por cantidad)
    open fun pizzasIncluidas(): Int = 0

    open fun detallarProducto(): String {
        val detalle = if (ingredientes.isEmpty()) "Sin ingredientes de inventario"
        else "Ingredientes: " + ingredientes.entries.joinToString(", ") { "${it.key} x${it.value}" }
        return "$nombre [${categoria.descripcion}] - ${formatearPesos(precio)} (IVA incluido)\n  $detalle"
    }

    // Igualdad: mismo nombre (sin importar mayúsculas) y misma categoría
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Producto) return false
        return nombre.equals(other.nombre, ignoreCase = true) && categoria == other.categoria
    }

    // hashCode usa los mismos atributos que equals (necesario para Set y Map)
    override fun hashCode(): Int = 31 * nombre.lowercase().hashCode() + categoria.hashCode()

    override fun toString(): String = "$nombre (${categoria.descripcion})"
}