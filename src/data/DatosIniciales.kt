package data

import model.Producto
import model.Repartidor
import service.Inventario

// Datos base con los que parte el día

fun crearInventario(): Inventario = Inventario(
    mapOf("Masa" to 40, "Salsa de tomate" to 40, "Queso" to 80, "Pepperoni" to 60, "Champiñones" to 5)
)

fun crearMenu(): List<Producto> = listOf(
    Producto("Pepperoni", 8990, mapOf("Masa" to 1, "Salsa de tomate" to 1, "Queso" to 2, "Pepperoni" to 3)),
    Producto("Cuatro Quesos", 9990, mapOf("Masa" to 1, "Queso" to 4)),
    Producto("Vegetariana", 9490, mapOf("Masa" to 1, "Salsa de tomate" to 1, "Queso" to 2, "Champiñones" to 2))
)

fun crearRepartidores(): List<Repartidor> = listOf(
    Repartidor("Carlos", "+56 9 11112222", "carlos@pizzeria.cl", 4.0),
    Repartidor("María", "+56 9 33334444", "maria@pizzeria.cl", 1.5),
    Repartidor("Luis", "+56 9 55556666", "luis@pizzeria.cl", 0.8, disponible = false)
)