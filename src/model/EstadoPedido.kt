package model

// Estados posibles de un pedido
enum class EstadoPedido(val descripcion: String) {
    PENDIENTE("Pendiente"),
    EN_PREPARACION("En preparación"),
    LISTO("Listo"),
    EN_CAMINO("En camino"),
    ENTREGADO("Entregado"),
    RECHAZADO("Rechazado");

    // Estados que ya no admiten cambios
    val esFinal: Boolean
        get() = this == ENTREGADO || this == RECHAZADO
}