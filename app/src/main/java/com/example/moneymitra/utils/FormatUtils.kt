import java.text.NumberFormat
import java.util.Locale

fun formatAmount(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
    return formatter.format(amount)
}