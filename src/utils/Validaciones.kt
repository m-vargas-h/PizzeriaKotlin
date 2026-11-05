package utils

const val TELEFONO_POR_DEFECTO = "+56 9 00000000"
const val CORREO_POR_DEFECTO = "correo@invalido.com"

// 8 dígitos, opcionalmente precedidos por 9, 569 o +569
private val REGEX_TELEFONO = Regex("^(\\+?569|9)?\\d{8}$")
// Formato estándar nombre@dominio.ext
private val REGEX_CORREO = Regex("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")

// Quita espacios, guiones y paréntesis antes de validar
private fun limpiarTelefono(entrada: String?): String =
    entrada.orEmpty().replace(" ", "").replace("-", "").replace("(", "").replace(")", "")

fun esTelefonoValido(entrada: String?): Boolean = REGEX_TELEFONO.matches(limpiarTelefono(entrada))

fun esCorreoValido(entrada: String?): Boolean = REGEX_CORREO.matches(entrada.orEmpty().trim().lowercase())

// Procesa el teléfono y lo deja en formato uniforme: +56 9 12345678 (null si no es válido)
fun formatearTelefono(entrada: String?): String? {
    val limpio = limpiarTelefono(entrada)
    return if (REGEX_TELEFONO.matches(limpio)) "+56 9 ${limpio.takeLast(8)}" else null
}

// Formatea el teléfono o asigna el valor por defecto si no es válido
fun validarTelefono(entrada: String?): String =
    formatearTelefono(entrada) ?: TELEFONO_POR_DEFECTO.also {
        println("  Teléfono inválido (\"${entrada.orEmpty()}\"). Se asigna $TELEFONO_POR_DEFECTO.")
    }

// Normaliza a minúsculas y valida el formato; asigna el valor por defecto si no es válido
fun validarCorreo(entrada: String?): String {
    val correo = entrada.orEmpty().trim().lowercase()
    return if (esCorreoValido(correo)) {
        correo
    } else {
        println("  Correo inválido (\"${entrada.orEmpty()}\"). Se asigna $CORREO_POR_DEFECTO.")
        CORREO_POR_DEFECTO
    }
}