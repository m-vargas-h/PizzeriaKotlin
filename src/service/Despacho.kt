package service

import model.EstadoPedido
import model.Pedido
import model.Repartidor

// Asigna el repartidor disponible más cercano: ordena por distancia y usa find
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

    repartidor.disponible = false
    pedido.repartidor = repartidor
    pedido.cambiarEstado(EstadoPedido.EN_CAMINO)
    println("Pedido #${pedido.idPedido} asignado a ${repartidor.nombre} (${repartidor.distanciaKm} km).")
    return repartidor
}