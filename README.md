# Pizzería en Kotlin

Aplicación de consola en Kotlin para la gestión operativa de una pizzería: registro de clientes, inventario de ingredientes, pedidos con varios productos, asignación de repartidores, reporte diario y resumen de cada pedido.

El sistema está pensado para ser operado por el **personal de la pizzería**. Las secciones marcadas con `[Uso interno]` son información para el local; el resumen de cada pedido sirve como confirmación para el cliente y la pizzería.

## Tecnologías

- Kotlin 2.x
- IntelliJ IDEA (build system IntelliJ)
- JDK 21

## Estructura

```
PizzeriaKotlin/
└── src/
    ├── Main.kt                    # Flujo principal: pedidos precargados, pedido por consola, reporte y resúmenes
    ├── data/
    │   └── DatosIniciales.kt      # Menú, inventario inicial y repartidores
    ├── model/
    │   ├── Usuario.kt             # Clase base (open): nombre, teléfono, correo
    │   ├── Cliente.kt             # Hereda de Usuario: dirección
    │   ├── Repartidor.kt          # Hereda de Usuario: distancia, disponibilidad, entregarPedido()
    │   ├── Producto.kt            # Nombre, precio (IVA incluido), ingredientes, detallarProducto()
    │   ├── ItemPedido.kt          # Producto + cantidad
    │   ├── Pedido.kt              # Productos, estado, notas, cálculos y cambiarEstado()
    │   └── EstadoPedido.kt        # Enum de estados del pedido
    ├── service/
    │   ├── Inventario.kt          # Map de ingredientes, verificación, lotes y reabastecimiento
    │   ├── Despacho.kt            # Asignación del repartidor disponible más cercano
    │   ├── GestionPedidos.kt      # Flujo de estados de un pedido con notificaciones
    │   ├── Registro.kt            # Registro de clientes, productos y reabastecimiento (try-catch)
    │   ├── Notificaciones.kt      # Avisos al cliente con ?.let
    │   └── Reportes.kt            # Reporte del día y resumen del pedido
    ├── ui/
    │   └── Consola.kt             # Ingreso de un pedido por consola
    └── utils/
        ├── Validaciones.kt        # Validación de teléfono y correo (Regex)
        └── FormatoMoneda.kt       # Formato de pesos chilenos
```

## Requerimientos implementados

| Requerimiento | Implementación |
|---|---|
| **Flujos de control** | `when` en `Inventario.verificarDisponibilidad()` evalúa cada ingrediente (sin stock / insuficiente / suficiente) e indica los pasos para reabastecer |
| **Arreglos** | `Array<Pedido>` con los pedidos del día; reporte con `for` y agrupación de completados, pendientes y rechazados con `filter` |
| **Strings** | Validación de teléfono y correo con `Regex`, valores por defecto `(000) 000-0000` y `correo@invalido.com`; formateo con `replace`, `substring`, `takeLast`, capitalización de nombres y plantillas `$variable` / `${expresión}` |
| **Funciones** | `Pedido.calcularTotal()` con descuento del 10% sobre 3 pizzas e IVA desglosado; `Inventario.prepararLote()` calcula ingredientes de un lote y actualiza el inventario |
| **Colecciones** | `Map` para el inventario, `listOf` de repartidores con `sortedBy` + `find` para el más cercano disponible, `Set` de productos vendidos |
| **Clases y objetos** | `Producto` con `detallarProducto()`; `Pedido` con lista de productos, cálculo de totales y `cambiarEstado()` (estado encapsulado con `private set`) |
| **Herencia y polimorfismo** | `Usuario` (open) especializado en `Cliente` y `Repartidor`; `describir()` sobrescrito; `entregarPedido()` en `Repartidor` |
| **Manejo de errores** | `try-catch` para precio inválido, cantidad no numérica o negativa (pedido e inventario); `finally` en reabastecimiento; validaciones con `require` |
| **Null safety** | Notas opcionales con `?.` y `?:` (`"Sin notas"`); notificaciones solo a clientes con correo válido mediante `?.let` |
| **Resumen** | Datos del cliente, productos (precio y cantidad), subtotal, descuento, total, IVA incluido y estado |

## Estados del pedido

`Pendiente` → `En preparación` → `Listo` → `En camino` → `Entregado`, o bien `Rechazado` si faltan ingredientes.

## Ejecución

1. Abrir el proyecto en IntelliJ IDEA.
2. Ejecutar `fun main()` en `src/Main.kt`.
3. El sistema procesa 4 pedidos precargados y luego solicita un pedido por consola.
4. Al final muestra el reporte del día, el resumen de cada pedido y el inventario.

## Casos validados

- **Pedido entregado** (#1001): se asigna el repartidor disponible más cercano y se notifica cada etapa.
- **Pedido con descuento** (#1002): 4 pizzas, aplica 10%.
- **Retiro en local** (#1003): cliente sin dirección.
- **Pedido rechazado** (#1004): ingredientes insuficientes; cliente con teléfono y correo inválidos (valores por defecto, sin notificación).
- **Errores de entrada**: precio no numérico o negativo, reabastecimiento con cantidad negativa o no numérica, opción de menú y cantidad inválidas (se vuelven a solicitar).
