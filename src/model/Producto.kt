package model

import util.formatearPesos

// precio: incluye IVA
// ingredientes: nombre del ingrediente -> cantidad necesaria por unidad
class Producto(
    val nombre: String,
    val precio: Int,
    val ingredientes: Map<String, Int>
) {
    // require lanza IllegalArgumentException si el dato no es válido
    init {
        require(precio > 0) { "El precio de $nombre debe ser mayor a 0 (recibido: $precio)." }
        require(ingredientes.isNotEmpty()) { "El producto $nombre debe tener al menos un ingrediente." }
    }

    fun detallarProducto(): String {
        val listaIngredientes = ingredientes.entries.joinToString(", ") { "${it.key} x${it.value}" }
        return "$nombre - ${formatearPesos(precio)} (IVA incluido)\n  Ingredientes: $listaIngredientes"
    }
}