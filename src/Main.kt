import data.crearInventario
import data.crearMenu
import data.crearRepartidores
import model.EstadoPedido
import model.ItemPedido
import model.Pedido
import model.PeriodoPromocion
import model.Usuario
import service.*
import ui.ingresarPedido
import utils.formatearFecha
import java.time.LocalDate

fun main() {
    println("============== SISTEMA DE GESTIÓN - PIZZERÍA ==============")

    val inventario = crearInventario()
    val menu = crearMenu()
    val repartidores = crearRepartidores()
    val (pepperoni, cuatroQuesos, vegetariana) = menu

    println("\n--- Menú del día ---")
    menu.forEach { println(it.detallarProducto()) }

    // Range de fechas: periodo en que los pedidos reciben descuento
    val porcentajePromocion = (PeriodoPromocion.TASA_DESCUENTO * 100).toInt()
    println("\n${PeriodoPromocion.NOMBRE}: $porcentajePromocion% de descuento en pedidos del " +
            "${formatearFecha(PeriodoPromocion.INICIO)} al ${formatearFecha(PeriodoPromocion.FIN)}")

    // Registro de productos nuevos: el precio se valida con try-catch
    println("\n--- Registro de productos [Uso interno] ---")
    registrarProducto("Hawaiana", "abc", mapOf("Masa" to 1, "Queso" to 2))
    registrarProducto("Napolitana", "-500", mapOf("Masa" to 1, "Queso" to 2))

    // Clientes precargados (Regex: se validan y formatean teléfono y correo al registrarlos)
    println("\n--- Registro de clientes [Uso interno] ---")
    val juan = registrarCliente("Juan Pérez", "+56 9 8765 4321", "juan@mail.com", "Av. Central 123")
    val ana = registrarCliente("Ana Soto", "912345678", "ana.soto@mail.cl", "Los Aromos 456")
    val pedro = registrarCliente("Pedro Rojas", "87651234", "pedro@correo.cl", null)
    val sofia = registrarCliente("Sofía Díaz", "12345", "sofia@mail", "Pasaje 7 #89")

    // Polimorfismo: clientes y repartidores tratados como Usuario
    println("\n--- Usuarios registrados [Uso interno] ---")
    val usuarios: List<Usuario> = listOf<Usuario>(juan, ana, pedro, sofia) + repartidores
    usuarios.forEach { println("  ${it.describir()} | ${it.contacto()}") }

    // Range de cantidades: solo se aceptan entre 1 y 100 unidades por producto
    println("\n--- Validación de cantidades (rango ${ItemPedido.RANGO_CANTIDAD}) [Uso interno] ---")
    for (cantidad in listOf(0, 150, 2)) {
        try {
            ItemPedido(cuatroQuesos, cantidad)
            println("  Cantidad $cantidad: válida")
        } catch (e: IllegalArgumentException) {
            println("  Error: ${e.message}")
        }
    }

    // Pedidos precargados: #1002 tiene fecha fuera del periodo de promoción
    val pedido1 = Pedido(1001L, juan, listOf(ItemPedido(pepperoni, 2), ItemPedido(vegetariana, 1)),
        fecha = LocalDate.of(2026, 11, 5))
    val pedido2 = Pedido(1002L, ana, listOf(ItemPedido(pepperoni, 4)), "Tocar el timbre dos veces",
        LocalDate.of(2026, 10, 28))
    val pedido3 = Pedido(1003L, pedro, listOf(ItemPedido(cuatroQuesos, 1)), "",
        LocalDate.of(2026, 11, 12))
    val pedido4 = Pedido(1004L, sofia, listOf(ItemPedido(vegetariana, 3)),
        fecha = LocalDate.of(2026, 11, 5))

    println("\n--- Procesamiento de pedidos [Uso interno] ---")
    procesarPedido(pedido1, inventario, repartidores, EstadoPedido.ENTREGADO)
    procesarPedido(pedido2, inventario, repartidores, EstadoPedido.EN_CAMINO)
    procesarPedido(pedido3, inventario, repartidores, EstadoPedido.LISTO)
    procesarPedido(pedido4, inventario, repartidores, EstadoPedido.ENTREGADO) // sin champiñones suficientes

    // Reabastecimiento tras el rechazo del #1004 (try-catch para cantidades inválidas)
    println("\n--- Reabastecimiento [Uso interno] ---")
    registrarReabastecimiento(inventario, "Champiñones", "-5")
    registrarReabastecimiento(inventario, "Champiñones", "diez")
    registrarReabastecimiento(inventario, "Champiñones", "10")

    // Lote anticipado: calcula los ingredientes necesarios y actualiza el inventario
    println("\n--- Preparación de lote [Uso interno] ---")
    val lotePreparado = inventario.prepararLote(pepperoni.ingredientes, 2)
    println("  Lote de 2 ${pepperoni.nombre}: ${if (lotePreparado) "preparado" else "no se pudo preparar"}")

    // Pedido ingresado por consola (fecha = hoy)
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