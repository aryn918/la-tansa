package com.latansa.app;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.*;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    TaskDb db; LinearLayout root,list,top; boolean feminine=true; int bg,card,ink,accent,muted;
    int dp(float x){return (int)(x*getResources().getDisplayMetrics().density);}
    @Override public void onCreate(Bundle b){super.onCreate(b);db=new TaskDb(this); feminine=getPreferences(0).getBoolean("feminine",true); if(Build.VERSION.SDK_INT>=33 && checkSelfPermission("android.permission.POST_NOTIFICATIONS")!=0) requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"},4); render();}
    void colors(){ if(feminine){bg=Color.rgb(255,245,248);card=Color.WHITE;ink=Color.rgb(67,48,59);accent=Color.rgb(198,104,139);muted=Color.rgb(143,116,129);}else{bg=Color.rgb(19,25,38);card=Color.rgb(35,44,62);ink=Color.rgb(239,243,250);accent=Color.rgb(100,155,220);muted=Color.rgb(166,181,202);} }
    TextView text(String s,int size,int color,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);if(bold)t.setTypeface(null,Typeface.BOLD);t.setGravity(Gravity.CENTER_VERTICAL);return t;}
    GradientDrawable shape(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;}
    LinearLayout.LayoutParams lp(int w,int h){return new LinearLayout.LayoutParams(w<0?w:dp(w),h<0?h:dp(h));}
    void render(){colors();getWindow().setStatusBarColor(bg);getWindow().setNavigationBarColor(bg);getWindow().getDecorView().setSystemUiVisibility(feminine?View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR|View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR:0);
      root=new LinearLayout(this);root.setOrientation(1);root.setPadding(dp(18),dp(18),dp(18),dp(12));root.setBackgroundColor(bg);setContentView(root);
      LinearLayout header=new LinearLayout(this);header.setGravity(Gravity.CENTER_VERTICAL);header.setOrientation(0);
      LinearLayout titles=new LinearLayout(this);titles.setOrientation(1);TextView name=text("لا تنسى",28,ink,true);TextView sub=text("رتّب يومك… والباقي علينا",13,muted,false);titles.addView(name);titles.addView(sub);header.addView(titles,new LinearLayout.LayoutParams(0,-2,1));
      Button theme=button(feminine?"🌙 الثيم الذكوري":"🌸 الثيم الأنثوي",accent,Color.WHITE);theme.setOnClickListener(v->{feminine=!feminine;getPreferences(0).edit().putBoolean("feminine",feminine).apply();render();});header.addView(theme);root.addView(header,lp(-1,-2));
      TextView today=text("مهامي",20,ink,true);LinearLayout.LayoutParams mt=lp(-1,-2);mt.topMargin=dp(25);mt.bottomMargin=dp(10);root.addView(today,mt);
      ScrollView sc=new ScrollView(this);list=new LinearLayout(this);list.setOrientation(1);sc.addView(list);root.addView(sc,new LinearLayout.LayoutParams(-1,0,1));
      Button add=button("＋  إضافة مهمة جديدة",accent,Color.WHITE);LinearLayout.LayoutParams ap=lp(-1,52);ap.topMargin=dp(12);root.addView(add,ap);add.setOnClickListener(v->addDialog());fill();
    }
    Button button(String s,int color,int fg){Button b=new Button(this);b.setText(s);b.setTextColor(fg);b.setTextSize(13);b.setAllCaps(false);b.setBackground(shape(color,18));return b;}
    void fill(){list.removeAllViews();ArrayList<Task> tasks=db.all();if(tasks.isEmpty()){TextView empty=text("ما عندك مهام حالياً ✨\nاضغطي إضافة مهمة وابدئي بتنظيم يومك",16,muted,false);empty.setGravity(Gravity.CENTER);list.addView(empty,lp(-1,180));return;}
      SimpleDateFormat fmt=new SimpleDateFormat("dd/MM  •  hh:mm a",Locale.getDefault());
      for(Task t:tasks){LinearLayout row=new LinearLayout(this);row.setOrientation(1);row.setPadding(dp(14),dp(10),dp(12),dp(10));row.setBackground(shape(card,18));LinearLayout.LayoutParams rp=lp(-1,-2);rp.bottomMargin=dp(9);list.addView(row,rp);
        LinearLayout line=new LinearLayout(this);line.setGravity(Gravity.CENTER_VERTICAL);CheckBox check=new CheckBox(this);check.setButtonTintList(android.content.res.ColorStateList.valueOf(accent));check.setChecked(t.done);line.addView(check);
        TextView title=text(t.title,16,ink,true);if(t.done)title.setPaintFlags(title.getPaintFlags()|16);line.addView(title,new LinearLayout.LayoutParams(0,-2,1));TextView del=text("✕",18,muted,true);line.addView(del);del.setPadding(dp(10),dp(4),dp(4),dp(4));row.addView(line);
        TextView detail=text(fmt.format(new Date(t.time))+"  •  "+t.repeat+(t.note.isEmpty()?"":"\n"+t.note),12,muted,false);detail.setPadding(dp(44),0,0,0);row.addView(detail);
        check.setOnCheckedChangeListener((v,is)->{db.done(t.id,is);if(is)cancel(t.id);else schedule(t);fill();});del.setOnClickListener(v->{new AlertDialog.Builder(this).setMessage("حذف هذه المهمة؟").setNegativeButton("إلغاء",null).setPositiveButton("حذف",(d,w)->{cancel(t.id);db.delete(t.id);fill();}).show();});
      }
    }
    void addDialog(){LinearLayout box=new LinearLayout(this);box.setPadding(dp(8),dp(8),dp(8),0);box.setOrientation(1);EditText title=new EditText(this);title.setSingleLine();title.setHint("اسم المهمة *");box.addView(title);EditText note=new EditText(this);note.setHint("ملاحظات (اختياري)");box.addView(note);
      Spinner repeat=new Spinner(this);String[] reps={"مرة واحدة","يومياً","أسبوعياً","شهرياً"};repeat.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,reps));box.addView(repeat);
      Calendar cal=Calendar.getInstance();final int[] yy={cal.get(Calendar.YEAR)},mm={cal.get(Calendar.MONTH)},dd={cal.get(Calendar.DAY_OF_MONTH)},hh={cal.get(Calendar.HOUR_OF_DAY)},mi={cal.get(Calendar.MINUTE)};
      Button date=button("اختيار التاريخ والوقت",card,ink);box.addView(date,lp(-1,48));TextView picked=text("",12,muted,false);box.addView(picked);Runnable update=()->{Calendar c=Calendar.getInstance();c.set(yy[0],mm[0],dd[0],hh[0],mi[0],0);picked.setText(new SimpleDateFormat("EEEE، dd/MM/yyyy • hh:mm a",Locale.getDefault()).format(c.getTime()));};update.run();date.setOnClickListener(v->{DatePickerDialog d=new DatePickerDialog(this,(view,y,m,day)->{yy[0]=y;mm[0]=m;dd[0]=day;TimePickerDialog tp=new TimePickerDialog(this,(tv,h,min)->{hh[0]=h;mi[0]=min;update.run();},hh[0],mi[0],false);tp.show();},yy[0],mm[0],dd[0]);d.show();});
      new AlertDialog.Builder(this).setTitle("مهمة جديدة").setView(box).setNegativeButton("إلغاء",null).setPositiveButton("حفظ",(dialog,w)->{String s=title.getText().toString().trim();if(s.isEmpty()){Toast.makeText(this,"اكتبي اسم المهمة أولاً",Toast.LENGTH_SHORT).show();return;}Calendar c=Calendar.getInstance();c.set(yy[0],mm[0],dd[0],hh[0],mi[0],0);long when=c.getTimeInMillis();String r=reps[repeat.getSelectedItemPosition()];long id=db.add(s,note.getText().toString().trim(),when,r);Task t=new Task(id,s,note.getText().toString().trim(),when,r,false);schedule(t);fill();}).show();
    }
    void schedule(Task t){if(t.time<System.currentTimeMillis())return;Intent i=new Intent(this,ReminderReceiver.class);i.putExtra("id",t.id);i.putExtra("title",t.title);i.putExtra("note",t.note);i.putExtra("repeat",t.repeat);PendingIntent pi=PendingIntent.getBroadcast(this,(int)t.id,i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);if(Build.VERSION.SDK_INT>=31 && am.canScheduleExactAlarms())am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,t.time,pi);else am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,t.time,pi);}
    void cancel(long id){Intent i=new Intent(this,ReminderReceiver.class);PendingIntent pi=PendingIntent.getBroadcast(this,(int)id,i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);((AlarmManager)getSystemService(ALARM_SERVICE)).cancel(pi);pi.cancel();}
}
