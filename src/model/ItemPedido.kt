package model

// Línea del pedido: un producto y la cantidad solicitada
data class ItemPedido(
    val producto: Producto,
    val cantidad: Int
) {
    init {
        require(cantidad in RANGO_CANTIDAD) {
            "La cantidad de ${producto.nombre} debe estar entre ${RANGO_CANTIDAD.first} y ${RANGO_CANTIDAD.last} (recibido: $cantidad)."
        }
    }

    fun subtotal(): Long = producto.precio * cantidad

    companion object {
        // Range de cantidades permitidas por producto
        val RANGO_CANTIDAD = 1..100
    }
}