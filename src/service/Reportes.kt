package service

import model.EstadoPedido
import model.Pedido
import model.PeriodoPromocion
import model.Producto
import model.Usuario
import utils.esPromocionable
import utils.formatearFecha
import utils.formatearPesos

// Menú del día y periodo de promoción vigente
fun mostrarMenuDelDia(catalogo: List<Producto>) {
    println("\n--- Menú del día ---")
    catalogo.forEach { println(it.detallarProducto()) }
    println("\n${PeriodoPromocion.NOMBRE}: ${PeriodoPromocion.PORCENTAJE_DESCUENTO}% de descuento en pedidos con productos promocionables del " +
            "${formatearFecha(PeriodoPromocion.INICIO)} al ${formatearFecha(PeriodoPromocion.FIN)}")
}

// Polimorfismo: clientes y repartidores tratados como Usuario
fun mostrarUsuarios(usuarios: List<Usuario>) {
    println("\n--- Usuarios registrados [Uso interno] ---")
    usuarios.forEach { println("  ${it.describir()} | ${it.contacto()}") }
}

// Reporte del día: recorre el arreglo con for y agrupa los pedidos con filter
fun generarReporteDelDia(pedidos: Array<Pedido>) {
    println("\n========== REPORTE DE PEDIDOS DEL DÍA [Uso interno] ==========")
    for (pedido in pedidos) {
        val productos = pedido.items.joinToString(", ") { "${it.cantidad}x ${it.producto.nombre}" }
        println("#${pedido.idPedido}  ${pedido.cliente.nombre}")
        println("       Productos: $productos")
        println("       Total:     ${formatearPesos(pedido.total)} | Estado: ${pedido.estado.descripcion}")
    }

    val completados = pedidos.filter { it.estado == EstadoPedido.ENTREGADO }
    val pendientes = pedidos.filter { !it.estado.esFinal }
    val rechazados = pedidos.filter { it.estado == EstadoPedido.RECHAZADO }
    val validos = pedidos.filter { it.estado != EstadoPedido.RECHAZADO }

    // Set: elimina los productos repetidos entre pedidos
    val productosVendidos: Set<String> = validos.flatMap { it.items }.map { it.producto.nombre }.toSet()

    println("------------------------------------------------------------")
    println("Completados (${completados.size}): ${listarIds(completados)}")
    println("Pendientes  (${pendientes.size}): ${listarIds(pendientes)}")
    println("Rechazados  (${rechazados.size}): ${listarIds(rechazados)}")
    println("Productos vendidos hoy: ${productosVendidos.joinToString(", ")}")
    println("Total vendido: ${formatearPesos(validos.sumOf { it.total })}")
    println("============================================================")
}

private fun listarIds(pedidos: List<Pedido>): String =
    if (pedidos.isEmpty()) "-" else pedidos.joinToString(", ") { "#${it.idPedido}" }

// Resumen del pedido: confirmación para el cliente y la pizzería
// Desglose en orden: subtotal, descuento, promoción, IVA incluido y total final
fun mostrarResumen(pedido: Pedido) {
    val cliente = pedido.cliente

    println("\n============== RESUMEN DEL PEDIDO #${pedido.idPedido} ==============")
    println("  Fecha:       ${formatearFecha(pedido.fecha)}")
    println("CLIENTE")
    println("  Nombre:      ${cliente.nombre}")
    println("  Dirección:   ${cliente.direccion}")
    println("  Teléfono:    ${cliente.telefono}")
    println("  Correo:      ${cliente.correo}")
    println("PRODUCTOS")
    for (item in pedido.items) {
        val marca = if (esPromocionable(item.producto)) "*" else " "
        val nombre = item.producto.nombre.padEnd(15)
        val precio = formatearPesos(item.producto.precio).padStart(8)
        val subtotal = formatearPesos(item.subtotal()).padStart(9)
        println(" $marca$nombre x${item.cantidad}  $precio c/u  $subtotal")
    }
    if (pedido.productosPromocionables().isNotEmpty()) println("  (*) Producto promocionable (@Promocionable)")
    println("TOTALES")
    println("  Subtotal:    ${formatearPesos(pedido.subtotal)}")
    println("  Descuento:   ${if (pedido.descuento > 0) "-${formatearPesos(pedido.descuento)} (${Pedido.PORCENTAJE_DESCUENTO}%)" else "No aplica"}")
    val textoPromocion = when {
        pedido.descuentoPromocion > 0 ->
            "-${formatearPesos(pedido.descuentoPromocion)} (${PeriodoPromocion.NOMBRE} ${PeriodoPromocion.PORCENTAJE_DESCUENTO}%)"
        !PeriodoPromocion.estaVigente(pedido.fecha) -> "No aplica (fuera del periodo)"
        else -> "No aplica (sin productos promocionables)"
    }
    println("  Promoción:   $textoPromocion")
    println("  IVA (${Pedido.PORCENTAJE_IVA}%):   ${formatearPesos(pedido.iva)} incluido (neto ${formatearPesos(pedido.neto)})")
    println("  Total final: ${formatearPesos(pedido.total)}")
    println("ESTADO")
    println("  Estado:      ${pedido.estado.descripcion}")
    println("  Repartidor:  ${pedido.repartidor?.nombre ?: "Sin asignar"}")
    println("  Notas:       ${pedido.notasParaMostrar()}")
    println("======================================================")
}