import data.crearInventario
import data.crearMenu
import data.crearPedidoAdicional
import data.crearPedidosIniciales
import data.crearRepartidores
import data.registrarClientesIniciales
import demo.*
import model.EstadoPedido
import model.Pedido
import service.*
import ui.ingresarPedido

fun main() {
    println("============== SISTEMA DE GESTIÓN - PIZZERÍA ==============")

    // Datos base del día: inventario, menú (catálogo sin duplicados) y repartidores
    val inventario = crearInventario()
    val menu = crearMenu()
    val repartidores = crearRepartidores()
    menu.forEach { agregarAlCatalogo(it) }
    mostrarMenuDelDia(obtenerCatalogo())

    // Demostraciones: anotaciones y registro de productos (incluye entradas inválidas intencionales)
    demostrarPromocionables(obtenerCatalogo())
    demostrarRegistroProductos()

    // Clientes del día
    println("\n--- Registro de clientes [Uso interno] ---")
    val clientes = registrarClientesIniciales()
    val juan = clientes.first()
    demostrarClienteDuplicado(juan)
    mostrarUsuarios(clientes + repartidores)
    demostrarRangoCantidades(menu[1])

    // Pedidos precargados: cada uno se lleva hasta un estado objetivo y se usa el estado retornado
    val (pedido1, pedido2, pedido3, pedido4) = crearPedidosIniciales(clientes, menu)
    println("\n--- Procesamiento de pedidos [Uso interno] ---")
    val resultados = listOf(
        pedido1 to EstadoPedido.ENTREGADO,
        pedido2 to EstadoPedido.EN_CAMINO,
        pedido3 to EstadoPedido.LISTO,
        pedido4 to EstadoPedido.ENTREGADO        // sin champiñones suficientes
    ).map { (pedido, objetivo) -> pedido.idPedido to procesarPedido(pedido, inventario, repartidores, objetivo) }
    println("\n  Resultado: " + resultados.joinToString(" | ") { (id, estado) -> "#$id ${estado.descripcion}" })

    // Reabastecimiento tras el rechazo del #1004
    demostrarReabastecimiento(inventario)

    // Lote anticipado: calcula los ingredientes necesarios y actualiza el inventario
    println("\n--- Preparación de lote [Uso interno] ---")
    val pepperoni = menu.first()
    val lotePreparado = inventario.prepararLote(pepperoni.ingredientes, 2)
    println("  Lote de 2 ${pepperoni.nombre}: ${if (lotePreparado) "preparado" else "no se pudo preparar"}")

    // Pedido ingresado por consola (fecha = hoy), con el catálogo actualizado
    val pedidoConsola = ingresarPedido(1005L, obtenerCatalogo())
    pedidoConsola?.let {
        val estado = procesarPedido(it, inventario, repartidores, EstadoPedido.EN_PREPARACION)
        println("  Resultado: #${it.idPedido} ${estado.descripcion}")
    }

    // Reporte del día y resumen de cada pedido (arreglo con todos los pedidos)
    val precargados = arrayOf(pedido1, pedido2, pedido3, pedido4)
    val pedidosDelDia: Array<Pedido> = pedidoConsola?.let { precargados + it } ?: precargados
    generarReporteDelDia(pedidosDelDia)
    for (pedido in pedidosDelDia) mostrarResumen(pedido)

    println()
    inventario.mostrarInventario()

    // Reflection y resumen final de conceptos avanzados
    demostrarReflection()
    mostrarResumenAvanzado(pedido1, crearPedidoAdicional(juan, menu), pedido2, obtenerCatalogo())
}