import model.Cliente
import model.Pedido
import model.Pizza
import model.Repartidor
import java.util.Locale

// Formato de moneda
fun formatearPesos(monto: Double): String =
    "$" + String.format(Locale.forLanguageTag("es-CL"), "%,.0f", monto)

fun main() {

    println("=== Sistema de Pedidos – Pizzería ===")

    // PASO 1: Datos base (menú de pizzas y repartidores)
    val pepperoni = Pizza("Pepperoni", 8990, 10)
    val cuatroQuesos = Pizza("Cuatro Quesos", 9990, 5)
    val napolitana = Pizza("Napolitana", 8490, 2)

    val repartidores = listOf(
        Repartidor("Carlos", true),
        Repartidor("María", true),
        Repartidor("Luis", false)
    )

    // PASO 2: ENTRADA de datos del cliente (readLine + manejo de nulos/errores)
    print("Ingrese nombre del cliente: ")
    val nombre: String = readLine()?.ifBlank { "Cliente sin nombre" } ?: "Cliente sin nombre"

    print("Ingrese dirección de entrega: ")
    val direccion: String = readLine().orEmpty().ifBlank { "Retiro en local" }

    print("Ingrese teléfono (9 dígitos): ")
    val telefono: Long = try {
        // CONVERSIÓN String → Long; si es null o inválido se asigna valor por defecto
        val numero = readLine()?.trim()?.toLong() ?: 0L
        if (numero.toString().length == 9) numero else {
            println("⚠ El teléfono debe tener 9 dígitos. Se asignará 0 por defecto.")
            0L
        }
    } catch (e: NumberFormatException) {
        println("⚠ Teléfono inválido. Se asignará 0 por defecto.")
        0L
    }

    val cliente = Cliente(nombre, direccion, telefono)

    // SALIDA: mostrar datos registrados
    println("\n--- Cliente registrado ---")
    println("Nombre:    ${cliente.nombre}")
    println("Dirección: ${cliente.direccion}")
    println("Teléfono:  ${if (cliente.telefono == 0L) "No informado" else cliente.telefono}")

    // Selección de pizza y cantidad
    println("\n--- Menú ---")
    println("1. ${pepperoni.nombrePizza} - ${formatearPesos(pepperoni.precioUnitario.toDouble())}")
    println("2. ${cuatroQuesos.nombrePizza} - ${formatearPesos(cuatroQuesos.precioUnitario.toDouble())}")
    println("3. ${napolitana.nombrePizza} - ${formatearPesos(napolitana.precioUnitario.toDouble())}")
    print("Seleccione una pizza (1-3): ")
    val opcion: Int = readLine()?.toIntOrNull() ?: 1

    // when como expresión
    val pizzaElegida: Pizza = when (opcion) {
        1 -> pepperoni
        2 -> cuatroQuesos
        3 -> napolitana
        else -> {
            println("⚠ Opción inválida. Se asignará ${pepperoni.nombrePizza} por defecto.")
            pepperoni
        }
    }

    print("Ingrese cantidad de pizzas: ")
    val cantidad: Int = try {
        val valor = readLine()?.toInt() ?: 1
        if (valor > 0) valor else {
            println("⚠ La cantidad debe ser mayor a 0. Se asignará 1 por defecto.")
            1
        }
    } catch (e: NumberFormatException) {
        println("⚠ Cantidad inválida. Se asignará 1 por defecto.")
        1
    }

    val pedido = Pedido(1001L, cliente, pizzaElegida, cantidad)

    // PASO 3: Cálculo del total con descuento
    pedido.calcularTotal()
    println("\n--- Cálculo del pedido ---")
    println("Subtotal:  ${formatearPesos(pedido.calcularSubtotal())}")
    println("Descuento: ${if (pedido.calcularDescuento() > 0) "10% (-${formatearPesos(pedido.calcularDescuento())})" else "No aplica"}")
    println("Total:     ${formatearPesos(pedido.totalPedido)}")

    // PASO 4: Verificación de stock (if como expresión)
    val hayStock: Boolean = pizzaElegida.ingredientesDisponibles >= pedido.cantidadPizzas

    val mensajeStock: String = if (hayStock) {
        pizzaElegida.ingredientesDisponibles -= pedido.cantidadPizzas   // reduce inventario
        pedido.estadoPedido = "Preparación"
        "✔ Stock suficiente. Pedido confirmado. Ingredientes restantes: ${pizzaElegida.ingredientesDisponibles}"
    } else {
        pedido.estadoPedido = "Rechazado"
        "✘ Ingredientes insuficientes (disponibles: ${pizzaElegida.ingredientesDisponibles}). El pedido no puede ser completado."
    }
    println("\n--- Verificación de stock ---")
    println(mensajeStock)

    // PASO 5: Asignación de repartidor (solo si el pedido fue confirmado)
    if (hayStock) {
        val repartidorAsignado: Repartidor? = repartidores.firstOrNull { it.disponible }
        println("\n--- Asignación de repartidor ---")
        if (repartidorAsignado != null) {
            repartidorAsignado.disponible = false
            pedido.repartidor = repartidorAsignado
            pedido.estadoPedido = "En Camino"
            println("✔ Pedido asignado a ${repartidorAsignado.nombre}.")
        } else {
            println("⚠ No hay repartidores disponibles. El pedido queda en preparación.")
        }
    }

    // PASO 6: Resumen del pedido
    mostrarResumen(pedido)
}

// Resumen ordenado como confirmación para cliente y pizzería
fun mostrarResumen(pedido: Pedido) {
    println("\n========== RESUMEN DEL PEDIDO #${pedido.idPedido} ==========")
    println("CLIENTE")
    println("  Nombre:     ${pedido.cliente.nombre}")
    println("  Dirección:  ${pedido.cliente.direccion}")
    println("  Teléfono:   ${if (pedido.cliente.telefono == 0L) "No informado" else pedido.cliente.telefono}")
    println("PIZZA")
    println("  Nombre:     ${pedido.pizza.nombrePizza}")
    println("  Precio:     ${formatearPesos(pedido.pizza.precioUnitario.toDouble())}")
    println("PEDIDO")
    println("  Cantidad:   ${pedido.cantidadPizzas}")
    println("  Descuento:  ${if (pedido.cantidadPizzas > 3) "10%" else "No aplica"}")
    println("  Total:      ${formatearPesos(pedido.totalPedido)}")
    println("  Estado:     ${pedido.estadoPedido}")
    println("  Repartidor: ${pedido.repartidor?.nombre ?: "Sin asignar"}")
    println("=================================================")
}