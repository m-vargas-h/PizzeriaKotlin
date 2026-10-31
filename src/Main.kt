import model.*
import service.Inventario
import service.asignarRepartidor

fun main() {
    val inventario = Inventario(
        mapOf("Masa" to 10, "Salsa de tomate" to 10, "Queso" to 15, "Pepperoni" to 12, "Champiñones" to 2)
    )

    val pepperoni = Producto("Pepperoni", 8990, mapOf("Masa" to 1, "Salsa de tomate" to 1, "Queso" to 2, "Pepperoni" to 3))
    val vegetariana = Producto("Vegetariana", 9490, mapOf("Masa" to 1, "Salsa de tomate" to 1, "Queso" to 2, "Champiñones" to 2))

    val repartidores = listOf(
        Repartidor("Carlos", "+56 9 1111 2222", "carlos@pizzeria.cl", 4.0),
        Repartidor("María", "+56 9 3333 4444", "maria@pizzeria.cl", 1.5),
        Repartidor("Luis", "+56 9 5555 6666", "luis@pizzeria.cl", 0.8, disponible = false)
    )

    val cliente = Cliente("Juan Pérez", "+56 9 8765 4321", "juan@mail.com", "Av. Central 123")

    // Pedido aceptado y despachado
    val pedido1 = Pedido(1001L, cliente, listOf(ItemPedido(pepperoni, 2), ItemPedido(vegetariana, 1)))
    inventario.aceptarPedido(pedido1)
    pedido1.cambiarEstado(EstadoPedido.LISTO)
    asignarRepartidor(pedido1, repartidores)

    // Pedido rechazado: ya no quedan champiñones
    println()
    val pedido2 = Pedido(1002L, cliente, listOf(ItemPedido(vegetariana, 2)))
    inventario.aceptarPedido(pedido2)

    // Reabastecimiento y lote
    println()
    inventario.reabastecer("Champiñones", 10)
    println("Lote de 2 Pepperoni preparado: ${inventario.prepararLote(pepperoni.ingredientes, 2)}")
    inventario.mostrarInventario()
}