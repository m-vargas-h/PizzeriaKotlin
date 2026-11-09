# Pizzería en Kotlin

Aplicación de consola en Kotlin para la gestión operativa de una pizzería: registro de clientes, catálogo de productos (pizzas, bebidas, ingredientes adicionales y combos), inventario de ingredientes, pedidos con promociones, despacho con repartidores, reporte diario y resumen de cada pedido.

El sistema está pensado para el **personal de la pizzería**. Las secciones marcadas con `[Uso interno]` son información para el local; el resumen de cada pedido sirve como confirmación para el cliente y la pizzería.

## Tecnologías

- Kotlin 2.4.20 (JVM) · JDK 21
- IntelliJ IDEA (build system IntelliJ)
- `kotlin-reflect` 2.4.20 (dependencia Maven, necesaria para anotaciones y reflection)

## Ejecución

1. Abrir el proyecto en IntelliJ IDEA.
2. Si la dependencia `kotlin-reflect` no se resuelve automáticamente: **File > Project Structure > Modules > Dependencies > + > Library > From Maven** → `org.jetbrains.kotlin:kotlin-reflect:2.4.20`.
3. Ejecutar `fun main()` en `src/Main.kt`.

```
============== SISTEMA DE GESTIÓN - PIZZERÍA ==============
1. Gestión de pedidos
2. Demostraciones (validación del sistema)
0. Salir
```

### Gestión de pedidos (operación normal)

| Opción | Descripción |
|---|---|
| 1. Ver menú del día | Catálogo con categorías, precios, ingredientes y periodo de promoción |
| 2. Ingresar nuevo pedido | Datos del cliente validados, selección de productos y verificación de inventario; queda *En preparación* o *Rechazado* |
| 3. Despachar pedido | *Listo* → *En camino* con el repartidor disponible más cercano → *Entregado*. Si es retiro en local, pasa a *Entregado* sin repartidor |
| 4. Ver reporte del día | Pedidos de la jornada agrupados en completados, pendientes y rechazados |
| 5. Ver resumen de un pedido | Por número (`1001` o `#1001`) |
| 6. Ver inventario | Stock actual de ingredientes |
| 7. Reabastecer ingrediente | Cantidad validada con `try-catch-finally` |

### Demostraciones (validación del sistema)

Cada demostración parte con una **jornada nueva** (inventario, catálogo y registros propios), por lo que sus entradas inválidas intencionales no afectan la gestión de pedidos y se pueden repetir con el mismo resultado.

| Opción | Muestra |
|---|---|
| 1. Validaciones Regex y Ranges | Teléfono y correo (válidos e inválidos), rango de cantidades, rango de fechas de promoción |
| 2. Anotaciones y reflection | Clases `@Promocionable` y análisis de `Pedido`, `Producto` y `Cliente` |
| 3. Igualdad y registro sin duplicados | `equals()`/`hashCode()` evitando productos y clientes duplicados |
| 4. Flujo de pedidos precargados | Procesamiento, rechazo, reabastecimiento, lote, reporte, resúmenes e inventario |
| 5. Resumen final de conceptos avanzados | Operador `+`, desestructuración, promocionables e igualdad |
| 6. Ejecutar demostración completa | Todas las anteriores en orden sobre una misma jornada |

## Estructura

```
PizzeriaKotlin/
└── src/
    ├── Main.kt                     # Punto de entrada: inicia el menú principal
    ├── annotations/
    │   └── Promocionable.kt        # Anotación @Promocionable (RUNTIME, CLASS)
    ├── app/
    │   └── Jornada.kt              # Estado de una jornada: inventario, menú, repartidores, registro y pedidos
    ├── data/
    │   ├── DatosIniciales.kt       # Inventario, menú y repartidores base
    │   └── EscenariosIniciales.kt  # Clientes y pedidos precargados para las demostraciones
    ├── demo/
    │   └── Demostraciones.kt       # Demostraciones del menú (incluyen entradas inválidas intencionales)
    ├── model/
    │   ├── Usuario.kt              # Clase base: nombre, teléfono, correo
    │   ├── Cliente.kt              # Hereda de Usuario; equals/hashCode y desestructuración
    │   ├── Repartidor.kt           # Hereda de Usuario; disponibilidad encapsulada
    │   ├── Producto.kt             # Clase abstracta; equals/hashCode por nombre y categoría
    │   ├── Pizza.kt / Combo.kt     # Productos @Promocionable
    │   ├── Bebida.kt / IngredienteExtra.kt
    │   ├── CategoriaProducto.kt    # Pizza, Bebida, Ingrediente adicional, Combo
    │   ├── Ingredientes.kt         # Nombres de ingredientes centralizados
    │   ├── ItemPedido.kt           # Producto + cantidad (rango 1..100)
    │   ├── Pedido.kt               # Montos, promociones, operador +, desestructuración
    │   ├── PeriodoPromocion.kt     # Rango de fechas de la promoción
    │   └── EstadoPedido.kt         # Estados del pedido
    ├── service/
    │   ├── Registro.kt             # Clientes y catálogo sin duplicados (Set); reabastecimiento
    │   ├── Inventario.kt           # Stock, verificación, lotes y reabastecimiento
    │   ├── GestionPedidos.kt       # Procesamiento y despacho (retornan el estado final)
    │   ├── Despacho.kt             # Asignación del repartidor disponible más cercano
    │   ├── Notificaciones.kt       # Avisos al cliente con ?.let
    │   ├── Reportes.kt             # Menú del día, usuarios, reporte del día y resumen del pedido
    │   └── ResumenAvanzado.kt      # Resumen final de conceptos avanzados
    ├── ui/
    │   ├── MenuPrincipal.kt        # Menú principal, gestión de pedidos y demostraciones
    │   └── Consola.kt              # Ingreso de pedidos con reingreso de datos inválidos
    └── utils/
        ├── Validaciones.kt         # Regex de teléfono y correo
        ├── Inspector.kt            # Reflection: anotaciones, propiedades y métodos
        ├── FormatoMoneda.kt        # Formato CLP y cálculo de porcentajes en pesos enteros
        └── FormatoFecha.kt         # Formato dd-MM-yyyy
```

## Decisiones de diseño

- **Formato de teléfono**: el formato sugerido `+XX (XXX) XXX-XXXX` supone 10 dígitos nacionales, y un número chileno tiene 9 (`9` + 8 dígitos). Se usa el formato real de Chile, `+56 9 12345678`. Se aceptan entradas como `912345678`, `87651234` o `+56 9 1234 5678`. Un teléfono inválido se solicita nuevamente por consola; en los datos precargados se asigna `+56 9 00000000`.
- **Descuentos**: 10% si el pedido supera 3 pizzas (incluye las pizzas de un combo). La promoción del 15% se aplica sobre el total después del descuento por cantidad, si la fecha está entre el 01-11-2026 y el 30-11-2026 y el pedido incluye al menos un producto `@Promocionable`.
- **Montos**: en `Long` y pesos enteros, porque el peso chileno no usa decimales. Los porcentajes se redondean al peso más cercano (0,5 hacia arriba). Subtotal, descuentos, IVA y total se calculan una sola vez al crear el pedido.
- **Pedido combinado**: recibe un número desde `#9001` y solo se muestra en el resumen. No se procesa nuevamente para no descontar dos veces los ingredientes de los pedidos originales.
- **Distancia del repartidor**: `distanciaKm` es la distancia del repartidor **al local** en el momento del despacho, una referencia fija independiente de la dirección del cliente.
- **Jornada**: la gestión de pedidos usa una jornada durante toda la sesión y cada demostración crea la suya, para separar la operación de las pruebas.

## Mejoras aplicadas según feedback (semana 2)

| Observación | Cambio |
|---|---|
| Extraer demostraciones y datos precargados del flujo principal | Menú con Gestión de pedidos y Demostraciones; `demo/`, `data/EscenariosIniciales.kt` y `app/Jornada.kt` |
| Encapsular estados mutables | `repartidor` y `disponible` con `private set`; cambios solo con `asignarRepartidor()`, `tomarPedido()` y `entregarPedido()` |
| Calcular el subtotal una sola vez | Montos calculados al crear el pedido y reutilizados en descuentos, IVA, total y resúmenes |
| Tipos apropiados para importes | Montos en `Long` (pesos enteros) en lugar de `Double` |
| Centralizar ingredientes | `object Ingredientes` con constantes |
| Resultado explícito del procesamiento | `procesarPedido()` y `despacharPedido()` retornan el `EstadoPedido` final |
| Referencia de distancia | Documentada como distancia al local |
| Orden del desglose | Subtotal → Descuento → Promoción → IVA incluido → Total final |

## Estados

**Pedido**

```
Pendiente → En preparación → Listo → En camino → Entregado
Pendiente → Rechazado                 (faltan ingredientes)
Listo → Entregado                     (retiro en local)
```

*Entregado* y *Rechazado* son estados finales y no admiten cambios.

**Repartidor**

```
Disponible → Ocupado (tomarPedido, al asignarse un pedido Listo) → Disponible (entregarPedido)
```

Un repartidor ocupado no puede tomar otro pedido y solo puede entregar el pedido que tiene asignado.

## Matriz de escenarios

| Escenario | Entrada | Resultado esperado |
|---|---|---|
| Pedido con entrega | #1001: 2 Pepperoni, 1 Vegetariana, 1 Combo Pareja (05-11-2026) | *Entregado* por María; 10% + 15%; total $36.307 |
| Fuera del periodo de promoción | #1002: 4 Pepperoni (28-10-2026) | Solo 10%; promoción "fuera del periodo"; total $32.364 |
| Sin productos promocionables | #1003: 2 Bebidas + Extra Queso | Promoción "sin productos promocionables"; total $6.180 |
| Ingredientes insuficientes | #1004: 3 Vegetarianas con 3 champiñones en stock | *Rechazado*; pasos de reabastecimiento; sin notificación (correo inválido) |
| Datos de contacto inválidos (precargados) | Teléfono `12345`, correo `sofia@mail` | Valores por defecto `+56 9 00000000` y `correo@invalido.com` |
| Datos de contacto inválidos (consola) | Teléfono `888888888`, correo `m123` | Mensaje de error y reingreso |
| Cantidad fuera de rango | `0`, `150` | Error del rango 1-100; en consola se solicita nuevamente |
| Precio inválido | `abc`, `-500` | Error; el producto no se registra |
| Reabastecimiento inválido | `-5`, `diez` | Error; el stock no cambia (`finally` muestra el stock) |
| Producto duplicado | `PEPPERONI` (Pizza) | Rechazado por existir en el catálogo |
| Cliente duplicado | `juan pérez`, `JUAN@mail.com` | Se retorna el registro existente (`===` → `true`) |
| Combinar pedidos del mismo cliente | #1001 + #1006 | #9001 con 3 Pepperoni agrupados; total $46.994 |
| Combinar pedidos de distintos clientes | #1001 + #1002 | Error: solo se combinan pedidos del mismo cliente |
| Notas nulas o vacías | `null`, `""` | Se muestra "Sin notas" |
| Despacho con retiro en local | Pedido sin dirección | *Listo* → *Entregado* sin repartidor |
| Opción de menú inválida | `x`, `9` | Mensaje de opción inválida; el programa continúa |

## Mejoras pendientes

- **Separar presentación y lógica**: el modelo y los servicios aún imprimen mensajes directamente; lo ideal es que retornen resultados y la capa `ui` los presente.
- **Pruebas automatizadas**: implementar con JUnit los escenarios de la matriz anterior.
- **Distancia según destino**: calcular la cercanía del repartidor respecto de la dirección del cliente.