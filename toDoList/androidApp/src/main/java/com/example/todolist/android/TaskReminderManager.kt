import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.todolist.android.R
import com.example.todolist.android.Task
import java.time.LocalDate

object TaskReminderManager {

    private const val CHANNEL_ID = "task_notifications"
    private var notificationId = 0

    fun setTaskReminder(context: Context, task: Task) {
        val deadline = task.date?.let { LocalDate.parse(it) }
        deadline?.let {
            if (LocalDate.now().isAfter(deadline)) {
                //Date limite dépassée
                showNotification(context, "Tâche en retard", "La tâche ${task.name} est en retard.")
            } else if (LocalDate.now().plusDays(1).isAfter(deadline)) {
                //Date limite dans moins de 24h
                showNotification(context, "Tâche urgente", "La tâche ${task.name} est urgente.")
            }
        }
    }

    private fun showNotification(context: Context, title: String, message: String) {
        val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager?.notify(notificationId++, builder.build())
    }

    fun createNotificationChannel(context: Context) {
        val name = context.getString(R.string.channel_name)
        val descriptionText = context.getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}
