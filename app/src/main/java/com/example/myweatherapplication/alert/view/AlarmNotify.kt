package com.example.myweatherapplication.alert.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.myweatherapplication.R
import com.example.myweatherapplication.database.LocalDataSourceImp
import com.example.myweatherapplication.model.Repository
import com.example.myweatherapplication.network.RemoteDataSourceImp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat

class AlarmNotify(private var context: Context, workerParams: WorkerParameters):
    Worker(context, workerParams) {
    override fun doWork(): Result {
        setAlerts()
        return Result.success()
    }

    private fun setAlerts(): String {
        var currentDescription = ""
        val repository = Repository.getInstance(

           RemoteDataSourceImp.getInstance(),
            LocalDataSourceImp.getInstance(context)
        )

        CoroutineScope(Dispatchers.Main).launch {
            currentDescription = repository.localSource.getWeatherFromDataBase().first()
                .get(0).list.get(0).weather.get(0).description + " " + DecimalFormat("#").format( repository.localSource.getWeatherFromDataBase().first().get(0)
                .list.get(0).main.temp  - 273.15) + "°C"

            display(currentDescription)
//            withContext(Dispatchers.IO){
//                launch {
//                    repository.deleteAlertFromRoom(AlertFragment.alarmID)
//                }
//            }
        }
        return currentDescription
    }


    private fun display(currentDescription: String) {
        val mBuilder = NotificationCompat.Builder(applicationContext, "notify_001")
        val intent = Intent(applicationContext, context::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        mBuilder.setContentIntent(pendingIntent)
        mBuilder.setSmallIcon(R.drawable.ic_wea)
        mBuilder.setContentTitle(context.getString(R.string.alert))
        mBuilder.setContentText(currentDescription)

        val mNotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "Your_channel_id"
        val channel = NotificationChannel(
            channelId,
            "Channel human readable title",
            NotificationManager.IMPORTANCE_HIGH
        )
        mNotificationManager.createNotificationChannel(channel)
        mBuilder.setChannelId(channelId)
        mNotificationManager.notify(0, mBuilder.build())
    }

}

