package model

import annotations.Promocionable

@Promocionable("Combos con precio especial participan en las promociones")
class Combo(
    nombre: String,
    precio: Int,
    val productos: List<Producto>
) : Producto(nombre, precio, sumarIngredientes(productos)) {

    override val categoria: CategoriaProducto
        get() = CategoriaProducto.COMBO

    override fun pizzasIncluidas(): Int = productos.sumOf { it.pizzasIncluidas() }

    override fun detallarProducto(): String =
        super.detallarProducto() + "\n  Incluye: " + productos.joinToString(", ") { it.nombre }
}

// Suma los ingredientes de todos los productos del combo
private fun sumarIngredientes(productos: List<Producto>): Map<String, Int> =
    productos.flatMap { it.ingredientes.entries }
        .groupBy({ it.key }, { it.value })
        .mapValues { (_, cantidades) -> cantidades.sum() }