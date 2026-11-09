package demo

import model.Bebida
import model.Cliente
import model.Combo
import model.IngredienteExtra
import model.Ingredientes
import model.ItemPedido
import model.Pedido
import model.Pizza
import model.Producto
import service.Inventario
import service.registrarCliente
import service.registrarProducto
import service.registrarReabastecimiento
import utils.esPromocionable
import utils.inspeccionarClase
import utils.listarClasesPromocionables

// Demostraciones de validaciones
// Incluyen entradas inválidas intencionales, por eso se separan del flujo normal de main()

// Anotaciones + reflection: qué clases de producto califican para promociones
fun demostrarPromocionables(catalogo: List<Producto>) {
    println("\n--- Promociones (@Promocionable) [Uso interno] ---")
    listarClasesPromocionables(listOf(Pizza::class, Bebida::class, IngredienteExtra::class, Combo::class))
    println("  Elegibles en el menú: " + catalogo.filter { esPromocionable(it) }.joinToString(", ") { it.nombre })
}

// Registro de productos: precios inválidos (try-catch), un producto válido y un duplicado (equals/hashCode)
fun demostrarRegistroProductos() {
    println("\n--- Registro de productos [Uso interno] ---")
    registrarProducto("Hawaiana", "abc", mapOf(Ingredientes.MASA to 1, Ingredientes.QUESO to 2))
    registrarProducto("Napolitana", "-500", mapOf(Ingredientes.MASA to 1, Ingredientes.QUESO to 2))
    registrarProducto("Margarita", "8490",
        mapOf(Ingredientes.MASA to 1, Ingredientes.SALSA_TOMATE to 1, Ingredientes.QUESO to 2))
    registrarProducto("PEPPERONI", "9500", mapOf(Ingredientes.MASA to 1, Ingredientes.QUESO to 2))   // mismo nombre y categoría
}

// Cliente duplicado: mismo nombre y correo escritos distinto -> se retorna el registro existente
fun demostrarClienteDuplicado(original: Cliente) {
    val duplicado = registrarCliente(original.nombre.lowercase(), original.telefono,
        original.correo.uppercase(), "Otra dirección 1")
    println("  ¿El duplicado retorna el mismo registro de ${original.nombre}? ${duplicado === original}")
}

// Range de cantidades: solo se aceptan entre 1 y 100 unidades por producto
fun demostrarRangoCantidades(producto: Producto) {
    println("\n--- Validación de cantidades (rango ${ItemPedido.RANGO_CANTIDAD}) [Uso interno] ---")
    for (cantidad in listOf(0, 150, 2)) {
        try {
            ItemPedido(producto, cantidad)
            println("  Cantidad $cantidad: válida")
        } catch (e: IllegalArgumentException) {
            println("  Error: ${e.message}")
        }
    }
}

// Reabastecimiento con cantidades inválidas (try-catch-finally) y luego una válida
fun demostrarReabastecimiento(inventario: Inventario) {
    println("\n--- Reabastecimiento [Uso interno] ---")
    registrarReabastecimiento(inventario, Ingredientes.CHAMPINONES, "-5")
    registrarReabastecimiento(inventario, Ingredientes.CHAMPINONES, "diez")
    registrarReabastecimiento(inventario, Ingredientes.CHAMPINONES, "10")
}

// Reflection: análisis dinámico de las entidades clave
fun demostrarReflection() {
    println("\n--- Análisis de clases con reflection [Uso interno] ---")
    listOf(Pedido::class, Producto::class, Cliente::class).forEach { inspeccionarClase(it) }
}