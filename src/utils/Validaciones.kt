package utils

const val TELEFONO_POR_DEFECTO = "(000) 000-0000"
const val CORREO_POR_DEFECTO = "correo@invalido.com"

// 8 dígitos, opcionalmente precedidos por 9, 569 o +569
private val REGEX_TELEFONO = Regex("^(\\+?569|9)?\\d{8}$")
private val REGEX_CORREO = Regex("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")

// Limpia espacios/guiones, valida y formatea como "+56 9 1234 5678"
fun validarTelefono(entrada: String?): String {
    val limpio = entrada.orEmpty().replace(" ", "").replace("-", "")
    return if (REGEX_TELEFONO.matches(limpio)) {
        val numero = limpio.takeLast(8)
        "+56 9 ${numero.substring(0, 4)} ${numero.substring(4)}"
    } else {
        println("  Teléfono inválido (\"${entrada.orEmpty()}\"). Se asigna $TELEFONO_POR_DEFECTO.")
        TELEFONO_POR_DEFECTO
    }
}

// Normaliza a minúsculas y valida el formato usuario@dominio.ext
fun validarCorreo(entrada: String?): String {
    val correo = entrada.orEmpty().trim().lowercase()
    return if (REGEX_CORREO.matches(correo)) {
        correo
    } else {
        println("  Correo inválido (\"${entrada.orEmpty()}\"). Se asigna $CORREO_POR_DEFECTO.")
        CORREO_POR_DEFECTO
    }
}