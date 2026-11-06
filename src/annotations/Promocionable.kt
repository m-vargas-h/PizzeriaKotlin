package annotations

// Marca las clases de productos que califican para promociones especiales
// RUNTIME: la anotación queda disponible para leerla con reflection mientras se ejecuta el programa
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Promocionable(val descripcion: String)