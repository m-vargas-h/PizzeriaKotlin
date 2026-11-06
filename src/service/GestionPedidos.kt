package service

import model.EstadoPedido
import model.Pedido
import model.Repartidor

// Lleva un pedido hasta el estado objetivo, notificando al cliente en cada etapa
fun procesarPedido(
    pedido: Pedido,
    inventario: Inventario,
    repartidores: List<Repartidor>,
    estadoObjetivo: EstadoPedido
) {
    println()
    // Anotaciones: identifica en tiempo de ejecución los productos elegibles para promociones
    val promocionables = pedido.productosPromocionables()
    println("Pedido #${pedido.idPedido} - productos promocionables: " +
            if (promocionables.isEmpty()) "ninguno" else promocionables.joinToString(", ") { it.nombre })

    if (!inventario.aceptarPedido(pedido)) {
        notificarCliente(pedido, "no pudimos aceptar tu pedido por falta de ingredientes")
        return
    }
    notificarCliente(pedido, "tu pedido está en preparación")
    if (estadoObjetivo == EstadoPedido.EN_PREPARACION) return

    pedido.cambiarEstado(EstadoPedido.LISTO)
    if (estadoObjetivo == EstadoPedido.LISTO) return

    val repartidor = asignarRepartidor(pedido, repartidores) ?: return
    notificarCliente(pedido, "tu pedido va en camino con ${repartidor.nombre}")
    if (estadoObjetivo == EstadoPedido.EN_CAMINO) return

    if (repartidor.entregarPedido(pedido)) {
        notificarCliente(pedido, "tu pedido fue entregado. ¡Buen provecho!")
    }
}