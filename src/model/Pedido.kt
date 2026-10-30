package model

// Pedido con varios productos; los precios ya incluyen IVA
class Pedido(
    val idPedido: Long,
    val cliente: Cliente,
    val items: List<ItemPedido>,
    val notas: String? = null          // opcional
) {
    // private set: el estado solo cambia mediante cambiarEstado()
    var estado: EstadoPedido = EstadoPedido.PENDIENTE
        private set

    var repartidor: Repartidor? = null  // null hasta que se asigne

    fun cantidadPizzas(): Int = items.sumOf { it.cantidad }

    fun calcularSubtotal(): Double = items.sumOf { it.subtotal() }

    // 10% de descuento si el pedido supera 3 pizzas
    fun calcularDescuento(): Double =
        if (cantidadPizzas() > MINIMO_PIZZAS_DESCUENTO) calcularSubtotal() * TASA_DESCUENTO else 0.0

    // Total final (IVA incluido)
    fun calcularTotal(): Double = calcularSubtotal() - calcularDescuento()

    // Desglose del IVA contenido en el total
    fun calcularNeto(): Double = calcularTotal() / (1 + TASA_IVA)
    fun calcularIva(): Double = calcularTotal() - calcularNeto()

    fun cambiarEstado(nuevoEstado: EstadoPedido): Boolean {
        if (estado.esFinal) {
            println("El pedido #$idPedido ya está ${estado.descripcion} y no puede cambiar de estado.")
            return false
        }
        estado = nuevoEstado
        return true
    }

    companion object {
        const val TASA_IVA = 0.19
        const val TASA_DESCUENTO = 0.10
        const val MINIMO_PIZZAS_DESCUENTO = 3
    }
}