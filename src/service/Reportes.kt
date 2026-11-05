package service

import model.EstadoPedido
import model.Pedido
import model.PeriodoPromocion
import utils.formatearFecha
import utils.formatearPesos

// Reporte del día: recorre el arreglo con for y agrupa los pedidos con filter
fun generarReporteDelDia(pedidos: Array<Pedido>) {
    println("\n========== REPORTE DE PEDIDOS DEL DÍA [Uso interno] ==========")
    for (pedido in pedidos) {
        val pizzas = pedido.items.joinToString(", ") { "${it.cantidad}x ${it.producto.nombre}" }
        println("#${pedido.idPedido}  ${pedido.cliente.nombre}")
        println("       Pizzas: $pizzas")
        println("       Total:  ${formatearPesos(pedido.calcularTotal())} | Estado: ${pedido.estado.descripcion}")
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
    println("Total vendido: ${formatearPesos(validos.sumOf { it.calcularTotal() })}")
    println("============================================================")
}

private fun listarIds(pedidos: List<Pedido>): String =
    if (pedidos.isEmpty()) "-" else pedidos.joinToString(", ") { "#${it.idPedido}" }

// Resumen del pedido: confirmación para el cliente y la pizzería
fun mostrarResumen(pedido: Pedido) {
    val cliente = pedido.cliente
    val descuento = pedido.calcularDescuento()
    val promocion = pedido.calcularDescuentoPromocion()
    val porcentajeDescuento = (Pedido.TASA_DESCUENTO * 100).toInt()
    val porcentajePromocion = (PeriodoPromocion.TASA_DESCUENTO * 100).toInt()
    val porcentajeIva = (Pedido.TASA_IVA * 100).toInt()

    println("\n============== RESUMEN DEL PEDIDO #${pedido.idPedido} ==============")
    println("  Fecha:       ${formatearFecha(pedido.fecha)}")
    println("CLIENTE")
    println("  Nombre:      ${cliente.nombre}")
    println("  Dirección:   ${cliente.direccion}")
    println("  Teléfono:    ${cliente.telefono}")
    println("  Correo:      ${cliente.correo}")
    println("PRODUCTOS")
    for (item in pedido.items) {
        val nombre = item.producto.nombre.padEnd(15)
        val precio = formatearPesos(item.producto.precio).padStart(8)
        val subtotal = formatearPesos(item.subtotal()).padStart(9)
        println("  $nombre x${item.cantidad}  $precio c/u  $subtotal")
    }
    println("TOTALES")
    println("  Subtotal:    ${formatearPesos(pedido.calcularSubtotal())}")
    println("  Descuento:   ${if (descuento > 0) "-${formatearPesos(descuento)} ($porcentajeDescuento%)" else "No aplica"}")
    println("  Promoción:   ${if (promocion > 0) "-${formatearPesos(promocion)} (${PeriodoPromocion.NOMBRE} $porcentajePromocion%)" else "No aplica (fuera del periodo)"}")
    println("  Total final: ${formatearPesos(pedido.calcularTotal())}")
    println("  Impuestos:   IVA $porcentajeIva% incluido ${formatearPesos(pedido.calcularIva())} (neto ${formatearPesos(pedido.calcularNeto())})")
    println("ESTADO")
    println("  Estado:      ${pedido.estado.descripcion}")
    println("  Repartidor:  ${pedido.repartidor?.nombre ?: "Sin asignar"}")
    println("  Notas:       ${pedido.notasParaMostrar()}")
    println("======================================================")
}