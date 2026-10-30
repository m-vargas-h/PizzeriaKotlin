package model

// Línea del pedido: un producto y la cantidad solicitada
data class ItemPedido(
    val producto: Producto,
    val cantidad: Int
) {
    fun subtotal(): Double = (producto.precio * cantidad).toDouble()
}