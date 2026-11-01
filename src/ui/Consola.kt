package ui

import model.ItemPedido
import model.Pedido
import model.Producto
import service.registrarCliente
import util.formatearPesos

private fun leerTexto(mensaje: String): String? {
    print(mensaje)
    return readLine()
}

// Ingreso de un pedido por consola; retorna null si no se agregan productos
fun ingresarPedido(idPedido: Long, menu: List<Producto>): Pedido? {
    println("\n============== NUEVO PEDIDO POR CONSOLA ==============")
    val nombre = leerTexto("Nombre del cliente: ")
    val telefono = leerTexto("Teléfono (ej: 912345678): ")
    val correo = leerTexto("Correo electrónico: ")
    val direccion = leerTexto("Dirección de entrega (vacío = retiro en local): ")
    val cliente = registrarCliente(nombre, telefono, correo, direccion)

    val items = mutableListOf<ItemPedido>()
    while (true) {
        println("\n--- Menú ---")
        menu.forEachIndexed { indice, producto ->
            println("${indice + 1}. ${producto.nombre} - ${formatearPesos(producto.precio)}")
        }
        println("0. Terminar pedido")

        val entrada = leerTexto("Seleccione una opción: ") ?: break   // fin de la entrada
        val opcion = entrada.trim().toIntOrNull() ?: -1
        when (opcion) {
            0 -> break
            in 1..menu.size -> leerItem(menu[opcion - 1])?.let { items.add(it) }
            else -> println("  Opción inválida. Ingrese un número entre 0 y ${menu.size}.")
        }
    }

    if (items.isEmpty()) {
        println("Pedido cancelado: no se agregaron productos.")
        return null
    }

    val notas = leerTexto("Notas del pedido (opcional): ")
    return Pedido(idPedido, cliente, items, notas)
}

// Lee la cantidad de un producto controlando valores no numéricos o menores a 1
private fun leerItem(producto: Producto): ItemPedido? =
    try {
        val cantidad = leerTexto("Cantidad de ${producto.nombre}: ").orEmpty().trim().toInt()
        ItemPedido(producto, cantidad).also { println("  Agregado: ${it.cantidad}x ${producto.nombre}") }
    } catch (e: NumberFormatException) {
        println("  Cantidad inválida: debe ser un número entero.")
        null
    } catch (e: IllegalArgumentException) {
        println("  Error: ${e.message}")
        null
    }