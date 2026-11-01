import data.crearInventario
import data.crearMenu
import data.crearRepartidores
import model.EstadoPedido
import model.ItemPedido
import model.Pedido
import service.*
import ui.ingresarPedido

fun main() {
    println("============== SISTEMA DE GESTIÓN - PIZZERÍA ==============")

    val inventario = crearInventario()
    val menu = crearMenu()
    val repartidores = crearRepartidores()
    val (pepperoni, cuatroQuesos, vegetariana) = menu

    println("\n--- Menú del día ---")
    menu.forEach { println(it.detallarProducto()) }

    // Clientes precargados (se validan teléfono y correo al registrarlos)
    println("\n--- Registro de clientes ---")
    val juan = registrarCliente("Juan Pérez", "+56 9 8765 4321", "juan@mail.com", "Av. Central 123")
    val ana = registrarCliente("Ana Soto", "912345678", "ana.soto@mail.cl", "Los Aromos 456")
    val pedro = registrarCliente("Pedro Rojas", "87651234", "pedro@correo.cl", null)
    val sofia = registrarCliente("Sofía Díaz", "12345", "sofia@mail", "Pasaje 7 #89")

    // Pedidos precargados
    val pedido1 = Pedido(1001L, juan, listOf(ItemPedido(pepperoni, 2), ItemPedido(vegetariana, 1)))
    val pedido2 = Pedido(1002L, ana, listOf(ItemPedido(pepperoni, 4)), "Tocar el timbre dos veces")
    val pedido3 = Pedido(1003L, pedro, listOf(ItemPedido(cuatroQuesos, 1)), "")
    val pedido4 = Pedido(1004L, sofia, listOf(ItemPedido(vegetariana, 3)))

    println("\n--- Procesamiento de pedidos precargados ---")
    procesarPedido(pedido1, inventario, repartidores, EstadoPedido.ENTREGADO)
    procesarPedido(pedido2, inventario, repartidores, EstadoPedido.EN_CAMINO)
    procesarPedido(pedido3, inventario, repartidores, EstadoPedido.LISTO)
    procesarPedido(pedido4, inventario, repartidores, EstadoPedido.ENTREGADO) // sin champiñones suficientes

    // Pedido ingresado por consola
    val pedidoConsola = ingresarPedido(1005L, menu)
    pedidoConsola?.let { procesarPedido(it, inventario, repartidores, EstadoPedido.EN_PREPARACION) }

    // Arreglo con todos los pedidos del día
    val precargados = arrayOf(pedido1, pedido2, pedido3, pedido4)
    val pedidosDelDia: Array<Pedido> = pedidoConsola?.let { precargados + it } ?: precargados

    generarReporteDelDia(pedidosDelDia)
    for (pedido in pedidosDelDia) mostrarResumen(pedido)

    println()
    inventario.mostrarInventario()
}