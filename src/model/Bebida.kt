package model

// Sin @Promocionable: no participa en promociones
class Bebida(
    nombre: String,
    precio: Long
) : Producto(nombre, precio, emptyMap()) {

    override val categoria: CategoriaProducto
        get() = CategoriaProducto.BEBIDA
}