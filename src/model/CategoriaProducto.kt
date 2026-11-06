package model

// Categoría del producto: junto con el nombre define si dos productos son iguales
enum class CategoriaProducto(val descripcion: String) {
    PIZZA("Pizza"),
    BEBIDA("Bebida"),
    INGREDIENTE("Ingrediente adicional"),
    COMBO("Combo")
}