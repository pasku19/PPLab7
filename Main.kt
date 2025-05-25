import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class InregistrareIstoric(val timp: Long, val comanda: String) : Comparable<InregistrareIstoric> {
    override fun compareTo(alta: InregistrareIstoric): Int = this.timp.compareTo(alta.timp)
}

fun convertesteTimp(data: String): Long {
    val format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val dataTimp = LocalDateTime.parse(data, format)
    return dataTimp.toEpochSecond(java.time.ZoneOffset.UTC)
}

fun citesteIstoric(caleFisier: String, limita: Int = 50): MutableList<InregistrareIstoric> {
    val linii = File(caleFisier).readLines().reversed()
    val inregistrari = mutableListOf<InregistrareIstoric>()

    var timp: Long? = null
    var comanda: String? = null

    for (linie in linii) {
        when {
            linie.startsWith("Start-Date:") -> timp = convertesteTimp(linie.removePrefix("Start-Date: ").trim())
            linie.startsWith("Commandline:") -> comanda = linie.removePrefix("Commandline: ").trim()
        }
        if (timp != null && comanda != null) {
            inregistrari.add(InregistrareIstoric(timp, comanda))
            if (inregistrari.size == limita) break
            timp = null
            comanda = null
        }
    }
    return inregistrari
}

fun <T : Comparable<T>> maxim(a: T, b: T): T {
    return if (a > b) a else b
}

fun <T> inlocuieste(map: MutableMap<Long, T>, vechi: T, nou: T) where T : InregistrareIstoric {
    val cheie = vechi.timp
    if (map.containsKey(cheie)) {
        map[cheie] = nou
    }
}

fun main() {
    val caleFisier = "src/history.log" //folosim o simulare a unor comenzi
    val inregistrari = citesteIstoric(caleFisier)

    val mapaIstoric = mutableMapOf<Long, InregistrareIstoric>()
    for (inregistrare in inregistrari) {
        mapaIstoric[inregistrare.timp] = inregistrare
    }

    println("Ultimele 50 de comenzi:")
    mapaIstoric.values.forEach { println("${it.timp}: ${it.comanda}") }
}
