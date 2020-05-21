package com.xunqinli.verifiterm.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xunqinli.verifiterm.model.RefreshHistroyBean;
import com.xunqinli.verifiterm.model.VerificationNotifyBean;
import com.xunqinli.verifiterm.rxbus.RxBus;
import com.xunqinli.verifiterm.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class MySQLiteHelper extends SQLiteOpenHelper {
    public static final String SPECIAL_DATE = "2020-06-28 00:00:00";

    public MySQLiteHelper(Context context){
        super(context, "verf_record", null, 1);
    }


    //创建数据库sql语句 并 执行,相当于初始化数据库，这里是新建了一张表这个方法继承自SQLiteOpenHelper,
    // 会自动调用，也就是会 当新建了一个DatabaseHelper对象时，就会默认新建一张表user，表里存着名为name项
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table verf_record(num int, name varchar(20), time varchar(20), phone varchar(20), car varchar(20), operater varchar(20), spcode int)";
        db.execSQL(sql);
    }

    //数据库升级操作
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //插入
    public long insertRecord(VerificationNotifyBean data){
        ContentValues values = new ContentValues();
        values.put("num",data.getNum());
        values.put("name",data.getName());
        values.put("time",data.getDateTime());
        values.put("phone",data.getPhone());
        values.put("car",data.getCarNum());
        values.put("operater",data.getOperater());
        values.put("spcode", Tools.getSpecialValue(data.getDateTime()));
        return getWritableDatabase().insert("verf_record", "", values);
    }

    //TODO 删除10天前的
//    public long deleteRecord(){
//        String days10before = getDays10Before();
//        return db.delete("verf_record", "time < ?", new String[]{days10before});
//    }
//
//    private String getDays10Before() {
//        return "";
//    }

    //查询数据
    public List<VerificationNotifyBean> searchRecord(){
        //创建游标对象
        Cursor cursor = getWritableDatabase().query("verf_record", new String[]{"num", "name", "car", "phone", "time", "operater", "spcode"}, "spcode > ?", new String[]{(Tools.getSpecialNowValue()-10)+""}, null, null, null);
        //利用游标遍历所有数据对象
        //为了显示全部，把所有对象连接起来，放到TextView中
        List<VerificationNotifyBean> datas = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                VerificationNotifyBean data = new VerificationNotifyBean(Integer.parseInt(cursor.getString(0)), cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5));
                datas.add(data);
            }while (cursor.moveToNext());
        }

        cursor.close();
        RxBus.getRxBus().post(new RefreshHistroyBean(datas));
        return datas;
    }
}
