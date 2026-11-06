package ui

import model.ItemPedido
import model.Pedido
import model.Producto
import service.registrarCliente
import utils.esCorreoValido
import utils.esTelefonoValido
import utils.formatearPesos

private fun leerTexto(mensaje: String): String? {
    print(mensaje)
    return readLine()
}

// Repite la pregunta hasta que el dato sea válido; retorna null solo si se corta la entrada
private fun leerValidado(mensaje: String, mensajeError: String, esValido: (String) -> Boolean): String? {
    while (true) {
        val entrada = leerTexto(mensaje) ?: return null
        if (esValido(entrada)) return entrada
        println("  $mensajeError")
    }
}

// Ingreso de un pedido por consola; retorna null si no se agregan productos
fun ingresarPedido(idPedido: Long, menu: List<Producto>): Pedido? {
    println("\n============== NUEVO PEDIDO POR CONSOLA ==============")
    val nombre = leerTexto("Nombre del cliente: ")
    val telefono = leerValidado(
        "Teléfono (ej: 912345678): ",
        "Teléfono inválido. Debe tener 9 dígitos y comenzar con 9 (ej: 912345678 o +56 9 12345678)."
    ) { esTelefonoValido(it) }
    val correo = leerValidado(
        "Correo electrónico: ",
        "Correo inválido. Use el formato nombre@dominio.com e intente nuevamente."
    ) { esCorreoValido(it) }
    val direccion = leerTexto("Dirección de entrega (vacío = retiro en local): ")
    val cliente = registrarCliente(nombre, telefono, correo, direccion)

    val items = mutableListOf<ItemPedido>()
    while (true) {
        println("\n--- Menú ---")
        menu.forEachIndexed { indice, producto ->
            println("${indice + 1}. ${producto.nombre} [${producto.categoria.descripcion}] - ${formatearPesos(producto.precio)}")
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

// Solicita la cantidad hasta que esté dentro del rango permitido; retorna null solo si se corta la entrada
private fun leerItem(producto: Producto): ItemPedido? {
    val rango = ItemPedido.RANGO_CANTIDAD
    while (true) {
        val entrada = leerTexto("Cantidad de ${producto.nombre} (${rango.first}-${rango.last}): ") ?: return null
        try {
            val item = ItemPedido(producto, entrada.trim().toInt())
            println("  Agregado: ${item.cantidad}x ${producto.nombre}")
            return item
        } catch (e: NumberFormatException) {
            println("  Cantidad inválida: debe ser un número entero. Intente nuevamente.")
        } catch (e: IllegalArgumentException) {
            println("  Error: ${e.message} Intente nuevamente.")
        }
    }
}