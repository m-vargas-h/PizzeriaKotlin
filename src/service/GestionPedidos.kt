package service

import model.EstadoPedido
import model.Pedido
import model.Repartidor

// Lleva un pedido hasta el estado objetivo, notificando al cliente en cada etapa
// Retorna el estado final para que quien lo invoque conozca el desenlace sin interpretar mensajes
fun procesarPedido(
    pedido: Pedido,
    inventario: Inventario,
    repartidores: List<Repartidor>,
    estadoObjetivo: EstadoPedido
): EstadoPedido {
    println()
    // Anotaciones: identifica en tiempo de ejecución los productos elegibles para promociones
    val promocionables = pedido.productosPromocionables()
    println("Pedido #${pedido.idPedido} - productos promocionables: " +
            if (promocionables.isEmpty()) "ninguno" else promocionables.joinToString(", ") { it.nombre })

    if (!inventario.aceptarPedido(pedido)) {
        notificarCliente(pedido, "no pudimos aceptar tu pedido por falta de ingredientes")
        return pedido.estado
    }
    notificarCliente(pedido, "tu pedido está en preparación")
    if (estadoObjetivo == EstadoPedido.EN_PREPARACION) return pedido.estado

    pedido.cambiarEstado(EstadoPedido.LISTO)
    if (estadoObjetivo == EstadoPedido.LISTO) return pedido.estado

    val repartidor = asignarRepartidor(pedido, repartidores) ?: return pedido.estado
    notificarCliente(pedido, "tu pedido va en camino con ${repartidor.nombre}")
    if (estadoObjetivo == EstadoPedido.EN_CAMINO) return pedido.estado

    if (repartidor.entregarPedido(pedido)) {
        notificarCliente(pedido, "tu pedido fue entregado. ¡Buen provecho!")
    }
    return pedido.estado
}

// Despacha un pedido en preparación: Listo -> (retiro en local) Entregado,
// o Listo -> En camino con el repartidor más cercano -> Entregado. Retorna el estado final
fun despacharPedido(pedido: Pedido, repartidores: List<Repartidor>): EstadoPedido {
    println()
    if (pedido.estado != EstadoPedido.EN_PREPARACION) {
        println("El pedido #${pedido.idPedido} no está en preparación (estado: ${pedido.estado.descripcion}).")
        return pedido.estado
    }

    pedido.cambiarEstado(EstadoPedido.LISTO)
    notificarCliente(pedido, "tu pedido está listo")

    if (pedido.cliente.esRetiroEnLocal()) {
        pedido.cambiarEstado(EstadoPedido.ENTREGADO)
        println("Pedido #${pedido.idPedido} retirado en el local por ${pedido.cliente.nombre}.")
        notificarCliente(pedido, "retiraste tu pedido en el local. ¡Buen provecho!")
        return pedido.estado
    }

    val repartidor = asignarRepartidor(pedido, repartidores) ?: return pedido.estado
    notificarCliente(pedido, "tu pedido va en camino con ${repartidor.nombre}")
    if (repartidor.entregarPedido(pedido)) {
        notificarCliente(pedido, "tu pedido fue entregado. ¡Buen provecho!")
    }
    return pedido.estado
}