package model

// Sin @Promocionable: no participa en promociones
class Bebida(
    nombre: String,
    precio: Int
) : Producto(nombre, precio, emptyMap()) {

    override val categoria: CategoriaProducto
        get() = CategoriaProducto.BEBIDA
}