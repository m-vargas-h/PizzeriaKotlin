import data.crearInventario
import data.crearMenu
import data.crearRepartidores
import model.Bebida
import model.Cliente
import model.Combo
import model.EstadoPedido
import model.Ingredientes
import model.IngredienteExtra
import model.ItemPedido
import model.Pedido
import model.PeriodoPromocion
import model.Pizza
import model.Producto
import model.Usuario
import service.*
import ui.ingresarPedido
import utils.esPromocionable
import utils.formatearFecha
import utils.inspeccionarClase
import utils.listarClasesPromocionables
import java.time.LocalDate

fun main() {
    println("============== SISTEMA DE GESTIÓN - PIZZERÍA ==============")

    val inventario = crearInventario()
    val menu = crearMenu()
    val repartidores = crearRepartidores()
    val (pepperoni, cuatroQuesos, vegetariana, bebida, extraQueso) = menu
    val comboPareja = menu[5]

    // Catálogo sin duplicados (Set + equals/hashCode de Producto)
    menu.forEach { agregarAlCatalogo(it) }

    println("\n--- Menú del día ---")
    obtenerCatalogo().forEach { println(it.detallarProducto()) }

    // Range de fechas: periodo en que los pedidos reciben descuento
    val porcentajePromocion = (PeriodoPromocion.TASA_DESCUENTO * 100).toInt()
    println("\n${PeriodoPromocion.NOMBRE}: $porcentajePromocion% de descuento en pedidos con productos promocionables del " +
            "${formatearFecha(PeriodoPromocion.INICIO)} al ${formatearFecha(PeriodoPromocion.FIN)}")

    // Anotaciones + reflection: qué clases de producto califican para promociones
    println("\n--- Promociones (@Promocionable) [Uso interno] ---")
    listarClasesPromocionables(listOf(Pizza::class, Bebida::class, IngredienteExtra::class, Combo::class))
    println("  Elegibles en el menú: " + obtenerCatalogo().filter { esPromocionable(it) }.joinToString(", ") { it.nombre })

    // Registro de productos nuevos: precio validado con try-catch y sin duplicados
    println("\n--- Registro de productos [Uso interno] ---")
    registrarProducto("Hawaiana", "abc", mapOf(Ingredientes.MASA to 1, Ingredientes.QUESO to 2))
    registrarProducto("Napolitana", "-500", mapOf(Ingredientes.MASA to 1, Ingredientes.QUESO to 2))
    registrarProducto("Margarita", "8490", mapOf(Ingredientes.MASA to 1, Ingredientes.SALSA_TOMATE to 1, Ingredientes.QUESO to 2))
    registrarProducto("PEPPERONI", "9500", mapOf(Ingredientes.MASA to 1, Ingredientes.QUESO to 2))   // mismo nombre y categoría

    // Clientes precargados (Regex: se validan y formatean teléfono y correo al registrarlos)
    println("\n--- Registro de clientes [Uso interno] ---")
    val juan = registrarCliente("Juan Pérez", "+56 9 8765 4321", "juan@mail.com", "Av. Central 123")
    val ana = registrarCliente("Ana Soto", "912345678", "ana.soto@mail.cl", "Los Aromos 456")
    val pedro = registrarCliente("Pedro Rojas", "87651234", "pedro@correo.cl", null)
    val sofia = registrarCliente("Sofía Díaz", "12345", "sofia@mail", "Pasaje 7 #89")
    // Duplicado: mismo nombre y correo escritos distinto -> equals los considera iguales
    val juanDuplicado = registrarCliente("juan pérez", "987654321", "JUAN@mail.com", "Otra dirección 1")
    println("  ¿El duplicado retorna el mismo registro de Juan? ${juanDuplicado === juan}")

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

    // Pedidos precargados:
    // #1002 fuera del periodo de promoción; #1003 sin productos promocionables (bebida + extra)
    val pedido1 = Pedido(1001L, juan,
        listOf(ItemPedido(pepperoni, 2), ItemPedido(vegetariana, 1), ItemPedido(comboPareja, 1)),
        fecha = LocalDate.of(2026, 11, 5))
    val pedido2 = Pedido(1002L, ana, listOf(ItemPedido(pepperoni, 4)), "Tocar el timbre dos veces",
        LocalDate.of(2026, 10, 28))
    val pedido3 = Pedido(1003L, pedro, listOf(ItemPedido(bebida, 2), ItemPedido(extraQueso, 1)), "",
        LocalDate.of(2026, 11, 12))
    val pedido4 = Pedido(1004L, sofia, listOf(ItemPedido(vegetariana, 3)),
        fecha = LocalDate.of(2026, 11, 5))
    // Pedido adicional de Juan: se combina con el #1001 usando el operador + (solo para el resumen final)
    val pedidoAdicional = Pedido(1006L, juan, listOf(ItemPedido(pepperoni, 1), ItemPedido(bebida, 2)),
        "Agregar servilletas", LocalDate.of(2026, 11, 5))

    println("\n--- Procesamiento de pedidos [Uso interno] ---")
    // procesarPedido retorna el estado final: el resultado se usa directamente
    val resultados = listOf(
        pedido1 to EstadoPedido.ENTREGADO,
        pedido2 to EstadoPedido.EN_CAMINO,
        pedido3 to EstadoPedido.LISTO,
        pedido4 to EstadoPedido.ENTREGADO        // sin champiñones suficientes
    ).map { (pedido, objetivo) -> pedido.idPedido to procesarPedido(pedido, inventario, repartidores, objetivo) }
    println("\n  Resultado: " + resultados.joinToString(" | ") { (id, estado) -> "#$id ${estado.descripcion}" })

    // Reabastecimiento tras el rechazo del #1004 (try-catch para cantidades inválidas)
    println("\n--- Reabastecimiento [Uso interno] ---")
    registrarReabastecimiento(inventario, Ingredientes.CHAMPINONES, "-5")
    registrarReabastecimiento(inventario, Ingredientes.CHAMPINONES, "diez")
    registrarReabastecimiento(inventario, Ingredientes.CHAMPINONES, "10")

    // Lote anticipado: calcula los ingredientes necesarios y actualiza el inventario
    println("\n--- Preparación de lote [Uso interno] ---")
    val lotePreparado = inventario.prepararLote(pepperoni.ingredientes, 2)
    println("  Lote de 2 ${pepperoni.nombre}: ${if (lotePreparado) "preparado" else "no se pudo preparar"}")

    // Pedido ingresado por consola (fecha = hoy), con el catálogo actualizado
    val pedidoConsola = ingresarPedido(1005L, obtenerCatalogo())
    pedidoConsola?.let {
        val estado = procesarPedido(it, inventario, repartidores, EstadoPedido.EN_PREPARACION)
        println("  Resultado: #${it.idPedido} ${estado.descripcion}")
    }

    // Arreglo con todos los pedidos del día
    val precargados = arrayOf(pedido1, pedido2, pedido3, pedido4)
    val pedidosDelDia: Array<Pedido> = pedidoConsola?.let { precargados + it } ?: precargados

    generarReporteDelDia(pedidosDelDia)
    for (pedido in pedidosDelDia) mostrarResumen(pedido)

    println()
    inventario.mostrarInventario()

    // Reflection: análisis dinámico de las entidades clave
    println("\n--- Análisis de clases con reflection [Uso interno] ---")
    listOf(Pedido::class, Producto::class, Cliente::class).forEach { inspeccionarClase(it) }

    // Resumen final: integra +, desestructuración, @Promocionable e igualdad de objetos
    mostrarResumenAvanzado(pedido1, pedidoAdicional, pedido2, obtenerCatalogo())
}