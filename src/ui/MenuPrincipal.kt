package ui

import app.Jornada
import demo.demostrarAnotacionesYReflection
import demo.demostrarFlujoPedidos
import demo.demostrarIgualdad
import demo.demostrarRegexYRanges
import demo.demostrarResumenFinal
import demo.ejecutarDemostracionCompleta
import model.EstadoPedido
import model.Pedido
import service.despacharPedido
import service.generarReporteDelDia
import service.mostrarMenuDelDia
import service.mostrarResumen
import service.procesarPedido
import service.registrarReabastecimiento

private const val OPCION_INVALIDA = "  Opción inválida. Intente nuevamente."

// Menú principal: separa la operación de la pizzería de las demostraciones del sistema
fun iniciarMenuPrincipal() {
    // Gestión de pedidos trabaja sobre la misma jornada durante toda la sesión
    val jornada = Jornada()
    while (true) {
        println("\n============== SISTEMA DE GESTIÓN - PIZZERÍA ==============")
        println("1. Gestión de pedidos")
        println("2. Demostraciones (validación del sistema)")
        println("0. Salir")
        when (leerOpcion()) {
            1 -> menuGestionPedidos(jornada)
            2 -> menuDemostraciones()
            0, null -> {
                println("Sistema finalizado. ¡Hasta pronto!")
                return
            }
            else -> println(OPCION_INVALIDA)
        }
    }
}

// Lee una opción numérica: -1 si no es un número, null si se corta la entrada
private fun leerOpcion(): Int? {
    val entrada = leerTexto("Seleccione una opción: ") ?: return null
    return entrada.trim().toIntOrNull() ?: -1
}

// ======================= GESTIÓN DE PEDIDOS =======================

private fun menuGestionPedidos(jornada: Jornada) {
    while (true) {
        println("\n------------------- GESTIÓN DE PEDIDOS -------------------")
        println("1. Ver menú del día")
        println("2. Ingresar nuevo pedido")
        println("3. Despachar pedido")
        println("4. Ver reporte del día")
        println("5. Ver resumen de un pedido")
        println("6. Ver inventario")
        println("7. Reabastecer ingrediente")
        println("0. Volver")
        when (leerOpcion()) {
            1 -> mostrarMenuDelDia(jornada.registro.obtenerCatalogo())
            2 -> ingresarNuevoPedido(jornada)
            3 -> despacharDesdeConsola(jornada)
            4 -> mostrarReporte(jornada)
            5 -> mostrarResumenPorNumero(jornada)
            6 -> jornada.inventario.mostrarInventario()
            7 -> reabastecerDesdeConsola(jornada)
            0, null -> return
            else -> println(OPCION_INVALIDA)
        }
    }
}

// Ingresa un pedido por consola y lo deja en preparación (o rechazado si faltan ingredientes)
private fun ingresarNuevoPedido(jornada: Jornada) {
    val pedido = ingresarPedido(jornada.proximoIdPedido(), jornada.registro) ?: return
    val estado = procesarPedido(pedido, jornada.inventario, jornada.repartidores, EstadoPedido.EN_PREPARACION)
    jornada.agregarPedido(pedido)
    println("  Resultado: #${pedido.idPedido} ${estado.descripcion}")
}

// Muestra los pedidos en preparación y despacha el elegido
private fun despacharDesdeConsola(jornada: Jornada) {
    val enPreparacion = jornada.pedidosDelDia().filter { it.estado == EstadoPedido.EN_PREPARACION }
    if (enPreparacion.isEmpty()) {
        println("  No hay pedidos en preparación para despachar.")
        return
    }
    println("  Pedidos en preparación: " + enPreparacion.joinToString(", ") { "#${it.idPedido} (${it.cliente.nombre})" })
    val pedido = leerPedido(jornada) ?: return
    val estado = despacharPedido(pedido, jornada.repartidores)
    println("  Resultado: #${pedido.idPedido} ${estado.descripcion}")
}

private fun mostrarReporte(jornada: Jornada) {
    val pedidos = jornada.pedidosDelDia()
    if (pedidos.isEmpty()) println("  Aún no hay pedidos registrados en la jornada.")
    else generarReporteDelDia(pedidos)
}

private fun mostrarResumenPorNumero(jornada: Jornada) {
    if (jornada.pedidosDelDia().isEmpty()) {
        println("  Aún no hay pedidos registrados en la jornada.")
        return
    }
    leerPedido(jornada)?.let { mostrarResumen(it) }
}

// Lista los ingredientes con su stock y reabastece el elegido (cantidad validada con try-catch)
private fun reabastecerDesdeConsola(jornada: Jornada) {
    val ingredientes = jornada.inventario.ingredientes()
    println("\n--- Reabastecer ingrediente ---")
    ingredientes.forEachIndexed { indice, nombre ->
        println("${indice + 1}. $nombre (stock: ${jornada.inventario.cantidadDisponible(nombre)})")
    }
    val opcion = leerOpcion() ?: return
    val ingrediente = ingredientes.getOrNull(opcion - 1)
    if (ingrediente == null) {
        println(OPCION_INVALIDA)
        return
    }
    val cantidad = leerTexto("Cantidad a agregar: ") ?: return
    registrarReabastecimiento(jornada.inventario, ingrediente, cantidad)
}

// Solicita un número de pedido existente (acepta "1001" o "#1001"); null si no se encuentra
private fun leerPedido(jornada: Jornada): Pedido? {
    val id = leerTexto("Número de pedido: ")?.trim()?.removePrefix("#")?.toLongOrNull()
    val pedido = id?.let { jornada.buscarPedido(it) }
    if (pedido == null) println("  No existe un pedido con ese número.")
    return pedido
}

// ======================= DEMOSTRACIONES =======================

private fun menuDemostraciones() {
    while (true) {
        println("\n---------- DEMOSTRACIONES (VALIDACIÓN DEL SISTEMA) ----------")
        println("Cada demostración parte con una jornada nueva y no afecta la gestión de pedidos.")
        println("1. Validaciones Regex y Ranges")
        println("2. Anotaciones y reflection")
        println("3. Igualdad y registro sin duplicados")
        println("4. Flujo de pedidos precargados")
        println("5. Resumen final de conceptos avanzados")
        println("6. Ejecutar demostración completa")
        println("0. Volver")
        when (leerOpcion()) {
            1 -> demostrarRegexYRanges(Jornada())
            2 -> demostrarAnotacionesYReflection(Jornada())
            3 -> demostrarIgualdad(Jornada())
            4 -> demostrarFlujoPedidos(Jornada())
            5 -> demostrarResumenFinal(Jornada())
            6 -> ejecutarDemostracionCompleta()
            0, null -> return
            else -> {
                println(OPCION_INVALIDA)
                continue
            }
        }
        leerTexto("\nPresione Enter para volver al menú de demostraciones...")
    }
}