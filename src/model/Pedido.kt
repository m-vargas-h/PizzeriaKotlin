package model

import java.time.LocalDate
import kotlin.math.round

// Pedido con varios productos; los precios ya incluyen IVA
class Pedido(
    val idPedido: Long,
    val cliente: Cliente,
    val items: List<ItemPedido>,
    val notas: String? = null,                // opcional
    val fecha: LocalDate = LocalDate.now()    // define si aplica la promoción
) {
    // private set: el estado solo cambia mediante cambiarEstado()
    var estado: EstadoPedido = EstadoPedido.PENDIENTE
        private set

    var repartidor: Repartidor? = null  // null hasta que se asigne

    // Operadores seguros: si no hay notas (null o vacías) se muestra "Sin notas"
    fun notasParaMostrar(): String = notas?.takeIf { it.isNotBlank() } ?: "Sin notas"

    fun cantidadPizzas(): Int = items.sumOf { it.cantidad }

    fun calcularSubtotal(): Double = items.sumOf { it.subtotal() }

    // 10% de descuento si el pedido supera 3 pizzas (redondeado a pesos enteros)
    fun calcularDescuento(): Double =
        if (cantidadPizzas() > MINIMO_PIZZAS_DESCUENTO) round(calcularSubtotal() * TASA_DESCUENTO) else 0.0

    // Range: verifica si la fecha del pedido está dentro del periodo de promoción
    fun aplicaPromocion(): Boolean = PeriodoPromocion.estaVigente(fecha)

    // Descuento de promoción sobre el total, después del descuento por cantidad (redondeado a pesos enteros)
    fun calcularDescuentoPromocion(): Double =
        if (aplicaPromocion()) round((calcularSubtotal() - calcularDescuento()) * PeriodoPromocion.TASA_DESCUENTO) else 0.0

    // Total final (IVA incluido)
    fun calcularTotal(): Double = calcularSubtotal() - calcularDescuento() - calcularDescuentoPromocion()

    // Desglose del IVA contenido en el total
    fun calcularNeto(): Double = calcularTotal() / (1 + TASA_IVA)
    fun calcularIva(): Double = calcularTotal() - calcularNeto()

    fun cambiarEstado(nuevoEstado: EstadoPedido): Boolean {
        if (estado.esFinal) {
            println("El pedido #$idPedido ya está ${estado.descripcion} y no puede cambiar de estado.")
            return false
        }
        estado = nuevoEstado
        return true
    }

    companion object {
        const val TASA_IVA = 0.19
        const val TASA_DESCUENTO = 0.10
        const val MINIMO_PIZZAS_DESCUENTO = 3
    }
}