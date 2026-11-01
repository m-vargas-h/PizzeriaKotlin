package model

// Línea del pedido: un producto y la cantidad solicitada
data class ItemPedido(
    val producto: Producto,
    val cantidad: Int
) {
    init {
        require(cantidad > 0) { "La cantidad de ${producto.nombre} debe ser mayor a 0 (recibido: $cantidad)." }
    }

    fun subtotal(): Double = (producto.precio * cantidad).toDouble()
}