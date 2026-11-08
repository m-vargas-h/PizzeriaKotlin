package service

import model.EstadoPedido
import model.Pedido
import model.Repartidor

// Asigna el repartidor disponible más cercano al local: ordena por distancia y usa find
fun asignarRepartidor(pedido: Pedido, repartidores: List<Repartidor>): Repartidor? {
    if (pedido.estado != EstadoPedido.LISTO) {
        println("El pedido #${pedido.idPedido} debe estar Listo para despacharse (estado: ${pedido.estado.descripcion}).")
        return null
    }

    val repartidor = repartidores.sortedBy { it.distanciaKm }.find { it.disponible }
    if (repartidor == null) {
        println("No hay repartidores disponibles para el pedido #${pedido.idPedido}.")
        return null
    }

    // tomarPedido actualiza en conjunto disponibilidad, repartidor asignado y estado del pedido
    if (!repartidor.tomarPedido(pedido)) {
        println("No se pudo asignar a ${repartidor.nombre} el pedido #${pedido.idPedido}.")
        return null
    }
    println("Pedido #${pedido.idPedido} asignado a ${repartidor.nombre} (${repartidor.distanciaKm} km del local).")
    return repartidor
}