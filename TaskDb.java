package com.latansa.app;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import java.util.*;

public class TaskDb extends SQLiteOpenHelper {
    public TaskDb(Context c) { super(c, "latansa.db", null, 1); }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE tasks(id INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT,note TEXT,when_ms INTEGER,repeat TEXT,done INTEGER DEFAULT 0)");
    }
    public void onUpgrade(SQLiteDatabase db,int oldV,int newV) {}
    public long add(String title,String note,long when,String repeat) {
        ContentValues v=new ContentValues(); v.put("title",title);v.put("note",note);v.put("when_ms",when);v.put("repeat",repeat);v.put("done",0);
        return getWritableDatabase().insert("tasks",null,v);
    }
    public ArrayList<Task> all() {
        ArrayList<Task> a=new ArrayList<>(); Cursor c=getReadableDatabase().rawQuery("SELECT * FROM tasks ORDER BY when_ms",null);
        try { while(c.moveToNext()) a.add(new Task(c.getLong(0),c.getString(1),c.getString(2),c.getLong(3),c.getString(4),c.getInt(5)==1)); } finally {c.close();}
        return a;
    }
    public void done(long id,boolean value) { ContentValues v=new ContentValues();v.put("done",value?1:0);getWritableDatabase().update("tasks",v,"id=?",new String[]{String.valueOf(id)}); }
    public void delete(long id) { getWritableDatabase().delete("tasks","id=?",new String[]{String.valueOf(id)}); }
}
