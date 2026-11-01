package service

import model.Pedido

// let: solo se notifica si el cliente tiene un correo válido (no nulo)
fun notificarCliente(pedido: Pedido, mensaje: String) {
    pedido.cliente.correoParaNotificar()?.let { correo ->
        println("  [Notificación] Para $correo: Pedido #${pedido.idPedido} - $mensaje")
    } ?: println("  [Notificación] ${pedido.cliente.nombre} no tiene correo válido; no se envía aviso.")
}