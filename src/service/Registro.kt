package service

import model.Cliente
import model.Producto
import util.formatearPesos
import util.validarCorreo
import util.validarTelefono

// Registra un cliente validando sus datos; los campos vacíos o nulos toman valores por defecto
fun registrarCliente(nombre: String?, telefono: String?, correo: String?, direccion: String?): Cliente {
    // Normaliza el nombre: quita espacios extra y capitaliza cada palabra
    val nombreFinal = nombre?.takeIf { it.isNotBlank() }
        ?.trim()
        ?.split(" ")
        ?.filter { it.isNotBlank() }
        ?.joinToString(" ") { palabra -> palabra.lowercase().replaceFirstChar { it.uppercase() } }
        ?: "Cliente sin nombre"
    val direccionFinal = direccion?.takeIf { it.isNotBlank() }?.trim() ?: "Retiro en local"
    return Cliente(nombreFinal, validarTelefono(telefono), validarCorreo(correo), direccionFinal)
}

// Registra un producto a partir del precio ingresado como texto
// NumberFormatException se captura primero porque es subclase de IllegalArgumentException
fun registrarProducto(nombre: String, precioTexto: String, ingredientes: Map<String, Int>): Producto? =
    try {
        val precio = precioTexto.trim().toInt()
        Producto(nombre, precio, ingredientes)
            .also { println("  Producto registrado: ${it.nombre} (${formatearPesos(it.precio)})") }
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