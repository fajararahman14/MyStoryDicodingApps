import android.widget.TextView
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun TextView.withDateFormat(createdAt: String) {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val date = format.parse(createdAt) as Date
    val dateFormat = DateFormat.getDateInstance(DateFormat.FULL).format(date)
    this.text = dateFormat
}