package utils

import annotations.Promocionable
import model.Producto
import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties

// Reflection: lee la anotación @Promocionable de la clase del producto en tiempo de ejecución
fun obtenerPromocion(producto: Producto): Promocionable? = producto::class.findAnnotation<Promocionable>()

fun esPromocionable(producto: Producto): Boolean = obtenerPromocion(producto) != null

// Revisa qué clases de producto están marcadas con @Promocionable
fun listarClasesPromocionables(clases: List<KClass<out Producto>>) {
    for (clase in clases) {
        val promocion = clase.findAnnotation<Promocionable>()
        val estado = promocion?.let { "Promocionable - ${it.descripcion}" } ?: "No promocionable"
        println("  ${(clase.simpleName ?: "?").padEnd(17)} $estado")
    }
}

// Reflection: lista las propiedades y métodos públicos de una clase
fun inspeccionarClase(clase: KClass<*>) {
    println("Clase ${clase.simpleName}")

    val propiedades = clase.memberProperties
        .filter { it.visibility == KVisibility.PUBLIC }
        .sortedBy { it.name }
    println("  Propiedades (${propiedades.size}):")
    propiedades.forEach { println("    - ${it.name}: ${(it.returnType.classifier as? KClass<*>)?.simpleName}") }

    val metodos = clase.memberFunctions
        .filter { it.visibility == KVisibility.PUBLIC }
        .map { it.name }
        .distinct()
        .sorted()
    println("  Métodos (${metodos.size}): ${metodos.joinToString(", ")}")
}