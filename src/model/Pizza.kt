package model

import annotations.Promocionable

@Promocionable("Pizzas del menú participan en las promociones")
class Pizza(
    nombre: String,
    precio: Long,
    ingredientes: Map<String, Int>
) : Producto(nombre, precio, ingredientes) {

    // get(): se calcula al consultarla, sin depender del orden de inicialización
    override val categoria: CategoriaProducto
        get() = CategoriaProducto.PIZZA

    init {
        require(ingredientes.isNotEmpty()) { "La pizza $nombre debe tener al menos un ingrediente." }
    }

    override fun pizzasIncluidas(): Int = 1
}