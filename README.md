# Pizzería en Kotlin 

Aplicación de consola en Kotlin que gestiona el flujo básico de pedidos de una pizzería: registro de clientes, cálculo del total con descuento, verificación de stock de ingredientes y asignación de repartidores.

## Tecnologías

- Kotlin 2.x
- IntelliJ IDEA (build system IntelliJ)
- JDK 21

## Estructura

```
PizzeriaKotlin/
└── src/
    ├── Main.kt              # Flujo principal, entrada/salida y resumen del pedido
    └── model/
        ├── Cliente.kt       # nombre, dirección, teléfono
        ├── Pizza.kt         # nombre, precio unitario, ingredientes disponibles
        ├── Pedido.kt        # id, cantidad, total, estado, repartidor + cálculos
        └── Repartidor.kt    # nombre, disponibilidad
```

## Requerimientos implementados

| Requerimiento | Implementación |
|---|---|
| Variables y tipos de datos | Clases en `model/` con tipos explícitos (`String`, `Int`, `Long`, `Double`, `Boolean`) y uso de `val`/`var` según mutabilidad |
| Entrada y salida de datos | `readLine()` con manejo de nulos (`?.`, `?:`, `ifBlank`) y `try/catch` para teléfono y cantidad, asignando valores por defecto |
| Total con descuento | Subtotal = precio × cantidad; 10% de descuento si el pedido supera 3 pizzas (`if` como expresión) |
| Verificación de stock | `if` como expresión que descuenta inventario y confirma, o rechaza el pedido con mensaje |
| Asignación de repartidor | Se asigna el primer repartidor disponible y el pedido pasa a "En Camino" |
| Resumen del pedido | Función `mostrarResumen()` en `Main.kt` con datos del cliente, pizza y pedido |

## Ejecución

1. Abrir el proyecto en IntelliJ IDEA.
2. Abrir `src/Main.kt`.
3. Ejecutar con el botón ▶ junto a `fun main()`.
4. Ingresar los datos solicitados por consola.

## Ejemplo de ejecución

```
Ingrese nombre del cliente: Juan Pérez
Ingrese dirección de entrega: Av. Central 123
Ingrese teléfono (8 dígitos, sin +569): +569 87654321
Seleccione una pizza (1-3): 1
Ingrese cantidad de pizzas: 4

========== RESUMEN DEL PEDIDO #1001 ==========
CLIENTE
  Nombre:     Juan Pérez
  Dirección:  Av. Central 123
  Teléfono:   +56987654321
PIZZA
  Nombre:     Pepperoni
  Precio:     $8.990
PEDIDO
  Cantidad:   4
  Descuento:  10%
  Total:      $32.364
  Estado:     En Camino
  Repartidor: Carlos
=================================================
```

### Casos validados

- **Pedido con descuento** (4 pizzas): aplica 10% y asigna repartidor.
- **Entradas vacías o inválidas**: asigna valores por defecto sin detener el programa.
- **Stock insuficiente**: el pedido queda "Rechazado" y sin repartidor.
