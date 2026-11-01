import model.*
import service.*

fun main() {
    val inventario = Inventario(mapOf("Masa" to 10, "Salsa de tomate" to 10, "Queso" to 15, "Pepperoni" to 12))

    println("--- Registro de clientes ---")
    val cliente1 = registrarCliente("Juan Pérez", "+56 9 8765 4321", "Juan@Mail.com", "Av. Central 123")
    val cliente2 = registrarCliente("  ", "12345", "ana@mail", null)
    println(cliente1.describir() + " | " + cliente1.contacto())
    println(cliente2.describir() + " | " + cliente2.contacto())

    println("\n--- Registro de productos ---")
    val pepperoni = registrarProducto("Pepperoni", "8990", mapOf("Masa" to 1, "Salsa de tomate" to 1, "Queso" to 2, "Pepperoni" to 3))
    registrarProducto("Hawaiana", "abc", mapOf("Masa" to 1))
    registrarProducto("Napolitana", "-500", mapOf("Masa" to 1))

    println("\n--- Reabastecimiento ---")
    registrarReabastecimiento(inventario, "Queso", "5")
    registrarReabastecimiento(inventario, "Queso", "-3")
    registrarReabastecimiento(inventario, "Queso", "diez")

    println("\n--- Notas y notificaciones ---")
    pepperoni?.let { producto ->
        val pedido1 = Pedido(1001L, cliente1, listOf(ItemPedido(producto, 2)), "Sin cebolla, por favor")
        val pedido2 = Pedido(1002L, cliente2, listOf(ItemPedido(producto, 1)))
        println("Notas #1001: ${pedido1.notasParaMostrar()}")
        println("Notas #1002: ${pedido2.notasParaMostrar()}")
        notificarCliente(pedido1, "tu pedido fue recibido")
        notificarCliente(pedido2, "tu pedido fue recibido")
    }
}