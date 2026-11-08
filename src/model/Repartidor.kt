package model

// Repartidor hereda de Usuario
// distanciaKm: distancia actual del repartidor al local (punto de despacho). Es una referencia fija,
// independiente de la dirección del cliente, y se usa para elegir al más cercano al momento de despachar.
class Repartidor(
    nombre: String,
    telefono: String,
    correo: String,
    val distanciaKm: Double,
    disponible: Boolean = true
) : Usuario(nombre, telefono, correo) {

    // private set: la disponibilidad solo cambia al tomar o entregar un pedido
    var disponible: Boolean = disponible
        private set

    override fun describir(): String = "Repartidor: $nombre (a $distanciaKm km del local)"

    // Invariante: un repartidor ocupado no puede tomar otro pedido
    fun tomarPedido(pedido: Pedido): Boolean {
        if (!disponible || !pedido.asignarRepartidor(this)) return false
        disponible = false
        return true
    }

    // Solo entrega un pedido en camino asignado a él; al entregar vuelve a quedar disponible
    fun entregarPedido(pedido: Pedido): Boolean {
        if (pedido.estado != EstadoPedido.EN_CAMINO || pedido.repartidor !== this) {
            println("El pedido #${pedido.idPedido} no está en camino con $nombre (estado: ${pedido.estado.descripcion}).")
            return false
        }
        pedido.cambiarEstado(EstadoPedido.ENTREGADO)
        disponible = true
        println("$nombre entregó el pedido #${pedido.idPedido} a ${pedido.cliente.nombre}.")
        return true
    }
}