package com.latansa.app;

import android.app.*;
import android.content.*;
import android.os.Build;

public class ReminderReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        String title=intent.getStringExtra("title"); String note=intent.getStringExtra("note"); long id=intent.getLongExtra("id",0);
        NotificationManager nm=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=26) nm.createNotificationChannel(new NotificationChannel("tasks","تذكيرات المهام",NotificationManager.IMPORTANCE_HIGH));
        Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(context,"tasks"):new Notification.Builder(context);
        b.setSmallIcon(android.R.drawable.ic_popup_reminder).setContentTitle("لا تنسى: "+title).setContentText(note==null||note.isEmpty()?"حان وقت إنجاز مهمتك":"تذكير: "+note).setAutoCancel(true);
        nm.notify((int)id,b.build());
        String repeat=intent.getStringExtra("repeat");
        if(repeat!=null && !repeat.equals("مرة واحدة")) {
            long next=System.currentTimeMillis()+(repeat.equals("يومياً")?86400000L:repeat.equals("أسبوعياً")?604800000L:2592000000L);
            Intent again=new Intent(context,ReminderReceiver.class); again.putExtras(intent);
            PendingIntent pi=PendingIntent.getBroadcast(context,(int)id,again,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
            AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE); am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,next,pi);
        }
    }
}
