package model

// Repartidor hereda de Usuario; distanciaKm se usa para asignar al más cercano
class Repartidor(
    nombre: String,
    telefono: String,
    correo: String,
    val distanciaKm: Double,
    var disponible: Boolean = true
) : Usuario(nombre, telefono, correo) {

    override fun describir(): String = "Repartidor: $nombre (a $distanciaKm km)"

    // Solo se puede entregar un pedido que esté en camino
    fun entregarPedido(pedido: Pedido): Boolean {
        if (pedido.estado != EstadoPedido.EN_CAMINO) {
            println("El pedido #${pedido.idPedido} no está en camino (estado: ${pedido.estado.descripcion}).")
            return false
        }
        pedido.cambiarEstado(EstadoPedido.ENTREGADO)
        disponible = true
        println("$nombre entregó el pedido #${pedido.idPedido} a ${pedido.cliente.nombre}.")
        return true
    }
}