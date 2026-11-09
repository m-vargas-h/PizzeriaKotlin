package demo

import app.Jornada
import data.crearPedidoAdicional
import data.crearPedidosIniciales
import data.registrarClientesIniciales
import model.Bebida
import model.Cliente
import model.Combo
import model.EstadoPedido
import model.IngredienteExtra
import model.Ingredientes
import model.ItemPedido
import model.Pedido
import model.PeriodoPromocion
import model.Pizza
import model.Producto
import service.generarReporteDelDia
import service.mostrarMenuDelDia
import service.mostrarResumen
import service.mostrarResumenAvanzado
import service.mostrarUsuarios
import service.procesarPedido
import service.registrarReabastecimiento
import utils.esPromocionable
import utils.formatearFecha
import utils.inspeccionarClase
import utils.listarClasesPromocionables
import java.time.LocalDate

// Demostraciones para validar el funcionamiento del sistema
// Incluyen entradas inválidas intencionales; cada una trabaja sobre su propia Jornada

// ======================= Demostraciones del menú =======================

fun demostrarRegexYRanges(jornada: Jornada) {
    titulo("REGEX Y RANGES")
    registrarClientes(jornada)
    println("  (El reingreso de teléfono y correo inválidos se prueba en Gestión de pedidos > Ingresar nuevo pedido)")
    validarRangoCantidades(jornada.menu[1])
    validarPeriodoPromocion()
}

fun demostrarAnotacionesYReflection(jornada: Jornada) {
    titulo("ANOTACIONES Y REFLECTION")
    listarPromocionables(jornada)
    analizarClases()
}

fun demostrarIgualdad(jornada: Jornada) {
    titulo("IGUALDAD Y REGISTRO SIN DUPLICADOS")
    registrarProductos(jornada)
    val clientes = registrarClientes(jornada)
    registrarClienteDuplicado(jornada, clientes.first())
}

fun demostrarFlujoPedidos(jornada: Jornada) {
    titulo("FLUJO DE PEDIDOS PRECARGADOS")
    val clientes = registrarClientes(jornada)
    procesarPedidosIniciales(jornada, clientes)
    reabastecerChampinones(jornada)
    prepararLoteAnticipado(jornada)
    mostrarCierreDelDia(jornada)
}

fun demostrarResumenFinal(jornada: Jornada) {
    titulo("RESUMEN FINAL DE CONCEPTOS AVANZADOS")
    val clientes = registrarClientes(jornada)
    val (pedido1, pedido2) = crearPedidosIniciales(clientes, jornada.menu)
    mostrarResumenAvanzado(pedido1, crearPedidoAdicional(clientes.first(), jornada.menu), pedido2,
        jornada.registro.obtenerCatalogo())
}

// Todas las secciones en orden sobre una misma jornada: muestra cómo las funcionalidades trabajan en conjunto
fun ejecutarDemostracionCompleta() {
    val jornada = Jornada()
    titulo("DEMOSTRACIÓN COMPLETA")
    mostrarMenuDelDia(jornada.registro.obtenerCatalogo())
    listarPromocionables(jornada)
    registrarProductos(jornada)
    val clientes = registrarClientes(jornada)
    registrarClienteDuplicado(jornada, clientes.first())
    validarRangoCantidades(jornada.menu[1])
    validarPeriodoPromocion()
    val (pedido1, pedido2) = procesarPedidosIniciales(jornada, clientes)
    reabastecerChampinones(jornada)
    prepararLoteAnticipado(jornada)
    mostrarCierreDelDia(jornada)
    analizarClases()
    mostrarResumenAvanzado(pedido1, crearPedidoAdicional(clientes.first(), jornada.menu), pedido2,
        jornada.registro.obtenerCatalogo())
}

// ======================= Secciones reutilizables =======================

private fun titulo(nombre: String) {
    println("\n=============== DEMOSTRACIÓN: $nombre ===============")
}

// Regex: teléfono y correo se validan y formatean al registrar; Sofía tiene datos inválidos a propósito
private fun registrarClientes(jornada: Jornada): List<Cliente> {
    println("\n--- Registro de clientes (Regex) [Uso interno] ---")
    val clientes = registrarClientesIniciales(jornada.registro)
    mostrarUsuarios(clientes + jornada.repartidores)
    return clientes
}

// Cliente duplicado: mismo nombre y correo escritos distinto -> se retorna el registro existente
private fun registrarClienteDuplicado(jornada: Jornada, original: Cliente) {
    println("\n--- Cliente duplicado (equals/hashCode) [Uso interno] ---")
    val duplicado = jornada.registro.registrarCliente(original.nombre.lowercase(), original.telefono,
        original.correo.uppercase(), "Otra dirección 1")
    println("  ¿El duplicado retorna el mismo registro de ${original.nombre}? ${duplicado === original}")
}

// Registro de productos: precios inválidos (try-catch), un producto válido y un duplicado (equals/hashCode)
private fun registrarProductos(jornada: Jornada) {
    println("\n--- Registro de productos [Uso interno] ---")
    val registro = jornada.registro
    registro.registrarProducto("Hawaiana", "abc", mapOf(Ingredientes.MASA to 1, Ingredientes.QUESO to 2))
    registro.registrarProducto("Napolitana", "-500", mapOf(Ingredientes.MASA to 1, Ingredientes.QUESO to 2))
    registro.registrarProducto("Margarita", "8490",
        mapOf(Ingredientes.MASA to 1, Ingredientes.SALSA_TOMATE to 1, Ingredientes.QUESO to 2))
    registro.registrarProducto("PEPPERONI", "9500", mapOf(Ingredientes.MASA to 1, Ingredientes.QUESO to 2))   // mismo nombre y categoría
}

// Anotaciones + reflection: qué clases de producto califican para promociones
private fun listarPromocionables(jornada: Jornada) {
    println("\n--- Promociones (@Promocionable) [Uso interno] ---")
    listarClasesPromocionables(listOf(Pizza::class, Bebida::class, IngredienteExtra::class, Combo::class))
    println("  Elegibles en el menú: " +
            jornada.registro.obtenerCatalogo().filter { esPromocionable(it) }.joinToString(", ") { it.nombre })
}

// Range de cantidades: solo se aceptan entre 1 y 100 unidades por producto
private fun validarRangoCantidades(producto: Producto) {
    println("\n--- Validación de cantidades (rango ${ItemPedido.RANGO_CANTIDAD}) [Uso interno] ---")
    for (cantidad in listOf(0, 150, 2)) {
        try {
            ItemPedido(producto, cantidad)
            println("  Cantidad $cantidad: válida")
        } catch (e: IllegalArgumentException) {
            println("  Error: ${e.message}")
        }
    }
}

// Range de fechas: verifica si una fecha está dentro del periodo de promoción
private fun validarPeriodoPromocion() {
    println("\n--- Periodo de promoción (rango de fechas) [Uso interno] ---")
    println("  ${PeriodoPromocion.NOMBRE}: ${formatearFecha(PeriodoPromocion.INICIO)} al ${formatearFecha(PeriodoPromocion.FIN)}")
    for (fecha in listOf(LocalDate.of(2026, 10, 28), LocalDate.of(2026, 11, 15), LocalDate.of(2026, 12, 1))) {
        val resultado = if (PeriodoPromocion.estaVigente(fecha)) "dentro del periodo (aplica descuento)" else "fuera del periodo"
        println("  ${formatearFecha(fecha)}: $resultado")
    }
}

// Procesa los pedidos precargados hasta su estado objetivo y usa el estado retornado
private fun procesarPedidosIniciales(jornada: Jornada, clientes: List<Cliente>): List<Pedido> {
    val pedidos = crearPedidosIniciales(clientes, jornada.menu)
    val (pedido1, pedido2, pedido3, pedido4) = pedidos
    println("\n--- Procesamiento de pedidos [Uso interno] ---")
    val resultados = listOf(
        pedido1 to EstadoPedido.ENTREGADO,
        pedido2 to EstadoPedido.EN_CAMINO,
        pedido3 to EstadoPedido.LISTO,
        pedido4 to EstadoPedido.ENTREGADO        // sin champiñones suficientes
    ).map { (pedido, objetivo) ->
        jornada.agregarPedido(pedido)
        pedido.idPedido to procesarPedido(pedido, jornada.inventario, jornada.repartidores, objetivo)
    }
    println("\n  Resultado: " + resultados.joinToString(" | ") { (id, estado) -> "#$id ${estado.descripcion}" })
    return pedidos
}

// Reabastecimiento con cantidades inválidas (try-catch-finally) y luego una válida
private fun reabastecerChampinones(jornada: Jornada) {
    println("\n--- Reabastecimiento [Uso interno] ---")
    registrarReabastecimiento(jornada.inventario, Ingredientes.CHAMPINONES, "-5")
    registrarReabastecimiento(jornada.inventario, Ingredientes.CHAMPINONES, "diez")
    registrarReabastecimiento(jornada.inventario, Ingredientes.CHAMPINONES, "10")
}

// Lote anticipado: calcula los ingredientes necesarios y actualiza el inventario
private fun prepararLoteAnticipado(jornada: Jornada) {
    println("\n--- Preparación de lote [Uso interno] ---")
    val pepperoni = jornada.menu.first()
    val preparado = jornada.inventario.prepararLote(pepperoni.ingredientes, 2)
    println("  Lote de 2 ${pepperoni.nombre}: ${if (preparado) "preparado" else "no se pudo preparar"}")
}

// Reporte del día, resumen de cada pedido e inventario final
private fun mostrarCierreDelDia(jornada: Jornada) {
    val pedidos = jornada.pedidosDelDia()
    generarReporteDelDia(pedidos)
    for (pedido in pedidos) mostrarResumen(pedido)
    println()
    jornada.inventario.mostrarInventario()
}

// Reflection: análisis dinámico de las entidades clave
private fun analizarClases() {
    println("\n--- Análisis de clases con reflection [Uso interno] ---")
    listOf(Pedido::class, Producto::class, Cliente::class).forEach { inspeccionarClase(it) }
}