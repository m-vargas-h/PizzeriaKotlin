package model

// datos del pedido
class Pedido(
    val idPedido: Long,
    val cliente: Cliente,
    val pizza: Pizza,
    val cantidadPizzas: Int
) {
    var totalPedido: Double = 0.0
    var estadoPedido: String = "Pendiente"
    var repartidor: Repartidor? = null      // null hasta que se asigne

    // subtotal = precio unitario * cantidad
    fun calcularSubtotal(): Double = (pizza.precioUnitario * cantidadPizzas).toDouble()

    // 10% de descuento si el pedido supera 3 pizzas
    fun calcularDescuento(): Double =
        if (cantidadPizzas > 3) calcularSubtotal() * 0.10 else 0.0

    fun calcularTotal() {
        totalPedido = calcularSubtotal() - calcularDescuento()
    }
}