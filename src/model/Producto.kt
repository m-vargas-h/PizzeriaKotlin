package model

import util.formatearPesos

// precio: incluye IVA
// ingredientes: nombre del ingrediente -> cantidad necesaria por unidad
class Producto(
    val nombre: String,
    val precio: Int,
    val ingredientes: Map<String, Int>
) {
    fun detallarProducto(): String {
        val listaIngredientes = ingredientes.entries.joinToString(", ") { "${it.key} x${it.value}" }
        return "$nombre - ${formatearPesos(precio)} (IVA incluido)\n  Ingredientes: $listaIngredientes"
    }
}