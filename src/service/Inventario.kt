package service

import model.EstadoPedido
import model.Pedido

// Inventario de ingredientes en un Map: nombre del ingrediente -> cantidad disponible
class Inventario(stockInicial: Map<String, Int>) {

    // private: el stock solo se modifica a través de los métodos de la clase
    private val stock: MutableMap<String, Int> = stockInicial.toMutableMap()

    fun cantidadDisponible(ingrediente: String): Int = stock[ingrediente] ?: 0

    // Cantidad necesaria de cada ingrediente para preparar un lote de pizzas
    fun calcularNecesarios(ingredientes: Map<String, Int>, cantidadPizzas: Int): Map<String, Int> =
        ingredientes.mapValues { (_, porUnidad) -> porUnidad * cantidadPizzas }

    // Evalúa cada ingrediente e informa los faltantes
    fun verificarDisponibilidad(requeridos: Map<String, Int>): Boolean {
        val faltantes = mutableMapOf<String, Int>()

        for ((ingrediente, necesario) in requeridos) {
            val disponible = cantidadDisponible(ingrediente)
            when {
                disponible == 0 -> {
                    println("  - $ingrediente: sin stock (se necesitan $necesario)")
                    faltantes[ingrediente] = necesario
                }
                disponible < necesario -> {
                    println("  - $ingrediente: insuficiente (hay $disponible, se necesitan $necesario)")
                    faltantes[ingrediente] = necesario - disponible
                }
                else -> {} // stock suficiente
            }
        }

        if (faltantes.isNotEmpty()) mostrarPasosReabastecimiento(faltantes)
        return faltantes.isEmpty()
    }

    // Calcula lo necesario para un lote y actualiza el inventario automáticamente
    fun prepararLote(ingredientes: Map<String, Int>, cantidadPizzas: Int): Boolean {
        val requeridos = calcularNecesarios(ingredientes, cantidadPizzas)
        if (!verificarDisponibilidad(requeridos)) return false
        descontar(requeridos)
        return true
    }

    // Revisa todos los productos del pedido antes de descontar (evita descuentos parciales)
    fun aceptarPedido(pedido: Pedido): Boolean {
        val requeridos = mutableMapOf<String, Int>()
        for (item in pedido.items) {
            calcularNecesarios(item.producto.ingredientes, item.cantidad).forEach { (ingrediente, cantidad) ->
                requeridos[ingrediente] = (requeridos[ingrediente] ?: 0) + cantidad
            }
        }

        println("Verificando ingredientes del pedido #${pedido.idPedido}...")
        return if (verificarDisponibilidad(requeridos)) {
            descontar(requeridos)
            pedido.cambiarEstado(EstadoPedido.EN_PREPARACION)
            println("  Pedido aceptado. Estado: ${pedido.estado.descripcion}")
            true
        } else {
            pedido.cambiarEstado(EstadoPedido.RECHAZADO)
            println("  Pedido rechazado por falta de ingredientes.")
            false
        }
    }

    // require lanza IllegalArgumentException; se capturará con try-catch
    fun reabastecer(ingrediente: String, cantidad: Int) {
        require(cantidad > 0) { "La cantidad a reabastecer debe ser mayor a 0 (recibido: $cantidad)." }
        stock[ingrediente] = cantidadDisponible(ingrediente) + cantidad
    }

    fun mostrarInventario() {
        println("--- Inventario ---")
        stock.forEach { (ingrediente, cantidad) -> println("  ${ingrediente.padEnd(18)} $cantidad") }
    }

    private fun descontar(requeridos: Map<String, Int>) {
        requeridos.forEach { (ingrediente, cantidad) ->
            stock[ingrediente] = cantidadDisponible(ingrediente) - cantidad
        }
    }

    private fun mostrarPasosReabastecimiento(faltantes: Map<String, Int>) {
        println("  Pasos para reabastecer:")
        println("    1. Solicitar al proveedor: " + faltantes.entries.joinToString { "${it.value} de ${it.key}" })
        println("    2. Registrar el ingreso en el inventario (reabastecer).")
        println("    3. Volver a ingresar el pedido.")
    }
}