package data

import model.Bebida
import model.Combo
import model.IngredienteExtra
import model.Ingredientes
import model.Pizza
import model.Producto
import model.Repartidor
import service.Inventario

// Datos base con los que parte el día

fun crearInventario(): Inventario = Inventario(
    mapOf(
        Ingredientes.MASA to 40,
        Ingredientes.SALSA_TOMATE to 40,
        Ingredientes.QUESO to 80,
        Ingredientes.PEPPERONI to 60,
        Ingredientes.CHAMPINONES to 5
    )
)

fun crearMenu(): List<Producto> {
    val pepperoni = Pizza("Pepperoni", 8990, mapOf(
        Ingredientes.MASA to 1, Ingredientes.SALSA_TOMATE to 1, Ingredientes.QUESO to 2, Ingredientes.PEPPERONI to 3))
    val cuatroQuesos = Pizza("Cuatro Quesos", 9990, mapOf(
        Ingredientes.MASA to 1, Ingredientes.QUESO to 4))
    val vegetariana = Pizza("Vegetariana", 9490, mapOf(
        Ingredientes.MASA to 1, Ingredientes.SALSA_TOMATE to 1, Ingredientes.QUESO to 2, Ingredientes.CHAMPINONES to 2))
    val bebida = Bebida("Bebida 1.5L", 2490)
    val extraQueso = IngredienteExtra(Ingredientes.QUESO, 1200)
    val comboPareja = Combo("Combo Pareja", 19990, listOf(pepperoni, cuatroQuesos, bebida))
    return listOf(pepperoni, cuatroQuesos, vegetariana, bebida, extraQueso, comboPareja)
}

// distanciaKm: distancia de cada repartidor al local al inicio del turno
fun crearRepartidores(): List<Repartidor> = listOf(
    Repartidor("Carlos", "+56 9 11112222", "carlos@pizzeria.cl", 4.0),
    Repartidor("María", "+56 9 33334444", "maria@pizzeria.cl", 1.5),
    Repartidor("Luis", "+56 9 55556666", "luis@pizzeria.cl", 0.8, disponible = false)
)