package model

// datos de la pizza
// ingredientesDisponibles es var porque el inventario se descuenta al confirmar pedidos
class Pizza(
    val nombrePizza: String,
    val precioUnitario: Int,
    var ingredientesDisponibles: Int
)