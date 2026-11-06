package com.latansa.app;

import android.content.*;
import android.app.*;

public class BootReceiver extends BroadcastReceiver {
 @Override public void onReceive(Context c,Intent i) {
   TaskDb db=new TaskDb(c); AlarmManager am=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
   for(Task t:db.all()) if(!t.done && t.time>System.currentTimeMillis()) {
     Intent r=new Intent(c,ReminderReceiver.class);r.putExtra("id",t.id);r.putExtra("title",t.title);r.putExtra("note",t.note);r.putExtra("repeat",t.repeat);
     PendingIntent pi=PendingIntent.getBroadcast(c,(int)t.id,r,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
     am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,t.time,pi);
   }
 }
}
