package service

import model.Cliente
import model.Pizza
import model.Producto
import utils.formatearPesos
import utils.validarCorreo
import utils.validarTelefono

// Set: con equals/hashCode sobrescritos, un cliente repetido no se agrega dos veces
private val clientesRegistrados = mutableSetOf<Cliente>()

// LinkedHashSet: mantiene el orden del menú y no admite productos duplicados
private val catalogo = linkedSetOf<Producto>()

// Registra un cliente validando sus datos; los campos vacíos o nulos toman valores por defecto
// Si ya existe un cliente igual (mismo nombre y correo) se retorna el registro existente
fun registrarCliente(nombre: String?, telefono: String?, correo: String?, direccion: String?): Cliente {
    // Normaliza el nombre: quita espacios extra y capitaliza cada palabra
    val nombreFinal = nombre?.takeIf { it.isNotBlank() }
        ?.trim()
        ?.split(" ")
        ?.filter { it.isNotBlank() }
        ?.joinToString(" ") { palabra -> palabra.lowercase().replaceFirstChar { it.uppercase() } }
        ?: "Cliente sin nombre"
    val direccionFinal = direccion?.takeIf { it.isNotBlank() }?.trim() ?: "Retiro en local"
    val cliente = Cliente(nombreFinal, validarTelefono(telefono), validarCorreo(correo), direccionFinal)

    // add retorna false si el Set ya contiene un cliente igual
    if (!clientesRegistrados.add(cliente)) {
        println("  Cliente duplicado: ${cliente.nombre} (${cliente.correo}) ya está registrado. Se usa el registro existente.")
        return clientesRegistrados.first { it == cliente }
    }
    return cliente
}

// Agrega un producto al catálogo; retorna false si ya existe (mismo nombre y categoría)
fun agregarAlCatalogo(producto: Producto): Boolean {
    val agregado = catalogo.add(producto)
    if (!agregado) {
        println("  Producto duplicado: ${producto.nombre} (${producto.categoria.descripcion}) ya existe en el catálogo.")
    }
    return agregado
}

fun obtenerCatalogo(): List<Producto> = catalogo.toList()

// Registra una pizza nueva a partir del precio ingresado como texto
// NumberFormatException se captura primero porque es subclase de IllegalArgumentException
fun registrarProducto(nombre: String, precioTexto: String, ingredientes: Map<String, Int>): Producto? =
    try {
        val pizza = Pizza(nombre, precioTexto.trim().toLong(), ingredientes)
        if (agregarAlCatalogo(pizza)) {
            println("  Producto registrado: ${pizza.nombre} (${formatearPesos(pizza.precio)})")
            pizza
        } else {
            null
        }
    } catch (e: NumberFormatException) {
        println("  Error: el precio \"$precioTexto\" de $nombre no es un número válido. Corrija el dato.")
        null
    } catch (e: IllegalArgumentException) {
        println("  Error: ${e.message}")
        null
    }

// Reabastece un ingrediente
fun registrarReabastecimiento(inventario: Inventario, ingrediente: String, cantidadTexto: String) {
    try {
        val cantidad = cantidadTexto.trim().toInt()
        inventario.reabastecer(ingrediente, cantidad)
        println("  Se agregaron $cantidad de $ingrediente.")
    } catch (e: NumberFormatException) {
        println("  Error: la cantidad \"$cantidadTexto\" no es un número. Ingrese un entero positivo.")
    } catch (e: IllegalArgumentException) {
        println("  Error: ${e.message}")
    } finally {
        // finally se ejecuta siempre, haya o no error
        println("  Stock actual de $ingrediente: ${inventario.cantidadDisponible(ingrediente)}")
    }
}