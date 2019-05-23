package com.softsolstudi.jugnoo.mapzonealert;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    Bundle bundle;
    String title="";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ME", "Notification started");
        bundle=intent.getExtras();
        if (bundle!=null){
            title=bundle.getString("title");
        }
        //Intent notificationIntent=new Intent(context,NavigationDrawer.class);
        final Intent notificationIntent = new Intent(context, MapsActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_medina)
                        .setSound(Settings.System.DEFAULT_RINGTONE_URI)
                        .setContentTitle(title)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setContentText("Time is Up, Click to Trake Youre Bus");

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());

    }
}
