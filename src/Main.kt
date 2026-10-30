import model.*
import util.formatearPesos

fun main() {
    val pepperoni = Producto("Pepperoni", 8990, mapOf("Masa" to 1, "Salsa de tomate" to 1, "Queso" to 2, "Pepperoni" to 3))
    val cliente = Cliente("Juan Pérez", "+56 9 8765 4321", "juan@mail.com", "Av. Central 123")
    val repartidor = Repartidor("Carlos", "+56 9 1111 2222", "carlos@pizzeria.cl", 2.5)

    val pedido = Pedido(1001L, cliente, listOf(ItemPedido(pepperoni, 4)))

    println(pepperoni.detallarProducto())
    println(cliente.describir() + " | " + cliente.contacto())
    println(repartidor.describir())
    println("Subtotal: ${formatearPesos(pedido.calcularSubtotal())}")
    println("Descuento: ${formatearPesos(pedido.calcularDescuento())}")
    println("Total: ${formatearPesos(pedido.calcularTotal())} (IVA: ${formatearPesos(pedido.calcularIva())})")

    pedido.cambiarEstado(EstadoPedido.EN_CAMINO)
    repartidor.entregarPedido(pedido)
    pedido.cambiarEstado(EstadoPedido.LISTO)   // debe rechazar: ya está entregado
}