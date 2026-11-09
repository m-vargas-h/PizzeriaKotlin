package service

import model.Bebida
import model.Cliente
import model.Ingredientes
import model.Pedido
import model.Pizza
import model.Producto
import utils.formatearPesos
import utils.obtenerPromocion

// Resumen final: integra operadores, desestructuración, anotaciones e igualdad de objetos
fun mostrarResumenAvanzado(pedidoA: Pedido, pedidoB: Pedido, pedidoOtroCliente: Pedido, catalogo: List<Producto>) {
    println("\n=============== RESUMEN FINAL - CONCEPTOS AVANZADOS ===============")

    // 1) Operator overloading: pedidoA + pedidoB
    println("\n1) PEDIDOS COMBINADOS (operador +)")
    val combinado = pedidoA + pedidoB
    println("  #${pedidoA.idPedido}: ${detalleItems(pedidoA)} -> ${formatearPesos(pedidoA.total)}")
    println("  #${pedidoB.idPedido}: ${detalleItems(pedidoB)} -> ${formatearPesos(pedidoB.total)}")
    println("  #${combinado.idPedido} = #${pedidoA.idPedido} + #${pedidoB.idPedido}: ${detalleItems(combinado)}")
    println("    Subtotal:  ${formatearPesos(combinado.subtotal)}")
    println("    Descuento: -${formatearPesos(combinado.descuento)} (${combinado.cantidadPizzas()} pizzas)")
    println("    Promoción: -${formatearPesos(combinado.descuentoPromocion)}")
    println("    Total:     ${formatearPesos(combinado.total)}")
    println("    Notas:     ${combinado.notasParaMostrar()}")
    try {
        pedidoA + pedidoOtroCliente
    } catch (e: IllegalArgumentException) {
        println("  #${pedidoA.idPedido} + #${pedidoOtroCliente.idPedido}: ${e.message}")
    }

    // 2) Desestructuración de Cliente y Pedido
    println("\n2) DESESTRUCTURACIÓN")
    val (nombre, correo, telefono) = combinado.cliente
    println("  Cliente -> nombre: $nombre | correo: $correo | teléfono: $telefono")
    val (clientePedido, productos, total) = combinado
    println("  Pedido #${combinado.idPedido} -> cliente: ${clientePedido.nombre} | " +
            "productos: ${productos.joinToString(", ") { "${it.cantidad}x ${it.producto.nombre}" }} | total: ${formatearPesos(total)}")

    // 3) Anotaciones: productos marcados con @Promocionable (leídos con reflection)
    println("\n3) PRODUCTOS PROMOCIONABLES (@Promocionable)")
    catalogo.forEach { producto ->
        obtenerPromocion(producto)?.let { println("  * ${producto.nombre} [${producto.categoria.descripcion}] - ${it.descripcion}") }
    }
    println("  En el pedido #${combinado.idPedido}: ${combinado.productosPromocionables().joinToString(", ") { it.nombre }}")

    // 4) Igualdad de objetos: equals / hashCode sobrescritos
    println("\n4) IGUALDAD DE OBJETOS (equals / hashCode)")
    val cliente = pedidoA.cliente
    val copiaCliente = Cliente(cliente.nombre.uppercase(), cliente.telefono, cliente.correo, "Otra dirección 99")
    val otroCliente = pedidoOtroCliente.cliente
    println("  Clientes (nombre + correo):")
    println("    ${cliente.nombre} == ${copiaCliente.nombre} (otra dirección): ${cliente == copiaCliente}")
    println("    ${cliente.nombre} === ${copiaCliente.nombre} (misma instancia): ${cliente === copiaCliente}")
    println("    ${cliente.nombre} == ${otroCliente.nombre}: ${cliente == otroCliente}")
    println("    Set [${cliente.nombre}, ${copiaCliente.nombre}, ${otroCliente.nombre}] -> " +
            "${setOf(cliente, copiaCliente, otroCliente).size} clientes únicos")

    val pepperoni = catalogo.first { it.nombre == "Pepperoni" }
    val pepperoniOtroPrecio = Pizza("pepperoni", 9500, mapOf(Ingredientes.MASA to 1, Ingredientes.QUESO to 2))
    val margaritaPizza = Pizza("Margarita", 8490,
        mapOf(Ingredientes.MASA to 1, Ingredientes.SALSA_TOMATE to 1, Ingredientes.QUESO to 2))
    val margaritaBebida = Bebida("Margarita", 3990)
    println("  Productos (nombre + categoría):")
    println("    $pepperoni == $pepperoniOtroPrecio (otro precio): ${pepperoni == pepperoniOtroPrecio}")
    println("    $margaritaPizza == $margaritaBebida: ${margaritaPizza == margaritaBebida}")
    println("    Set [$pepperoni, $pepperoniOtroPrecio, $margaritaPizza, $margaritaBebida] -> " +
            "${setOf(pepperoni, pepperoniOtroPrecio, margaritaPizza, margaritaBebida).size} productos únicos")

    println("\n===================================================================")
    println("Resumen generado como confirmación para el cliente y la pizzería.")
}

private fun detalleItems(pedido: Pedido): String =
    pedido.items.joinToString(", ") { "${it.cantidad}x ${it.producto.nombre}" }