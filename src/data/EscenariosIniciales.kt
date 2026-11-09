package data

import model.Cliente
import model.ItemPedido
import model.Pedido
import model.Producto
import service.registrarCliente
import java.time.LocalDate

// Clientes con los que parte el día (se validan teléfono y correo al registrarlos)
fun registrarClientesIniciales(): List<Cliente> = listOf(
    registrarCliente("Juan Pérez", "+56 9 8765 4321", "juan@mail.com", "Av. Central 123"),
    registrarCliente("Ana Soto", "912345678", "ana.soto@mail.cl", "Los Aromos 456"),
    registrarCliente("Pedro Rojas", "87651234", "pedro@correo.cl", null),
    registrarCliente("Sofía Díaz", "12345", "sofia@mail", "Pasaje 7 #89")
)

// Pedidos precargados (el menú llega en el orden de crearMenu())s
fun crearPedidosIniciales(clientes: List<Cliente>, menu: List<Producto>): List<Pedido> {
    val (juan, ana, pedro, sofia) = clientes
    val (pepperoni, _, vegetariana, bebida, extraQueso) = menu
    val comboPareja = menu[5]
    return listOf(
        Pedido(1001L, juan,
            listOf(ItemPedido(pepperoni, 2), ItemPedido(vegetariana, 1), ItemPedido(comboPareja, 1)),
            fecha = LocalDate.of(2026, 11, 5)),
        Pedido(1002L, ana, listOf(ItemPedido(pepperoni, 4)), "Tocar el timbre dos veces",
            LocalDate.of(2026, 10, 28)),
        Pedido(1003L, pedro, listOf(ItemPedido(bebida, 2), ItemPedido(extraQueso, 1)), "",
            LocalDate.of(2026, 11, 12)),
        Pedido(1004L, sofia, listOf(ItemPedido(vegetariana, 3)),
            fecha = LocalDate.of(2026, 11, 5))
    )
}

// Pedido adicional de Juan: se combina con el #1001 usando el operador + en el resumen final
fun crearPedidoAdicional(cliente: Cliente, menu: List<Producto>): Pedido {
    val (pepperoni, _, _, bebida) = menu
    return Pedido(1006L, cliente, listOf(ItemPedido(pepperoni, 1), ItemPedido(bebida, 2)),
        "Agregar servilletas", LocalDate.of(2026, 11, 5))
}