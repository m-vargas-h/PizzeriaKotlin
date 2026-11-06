package model

// Ingrediente adicional: descuenta 1 unidad de ese ingrediente del inventario
// Sin @Promocionable: no participa en promociones
class IngredienteExtra(
    val ingrediente: String,
    precio: Int
) : Producto("Extra $ingrediente", precio, mapOf(ingrediente to 1)) {

    override val categoria: CategoriaProducto
        get() = CategoriaProducto.INGREDIENTE
}