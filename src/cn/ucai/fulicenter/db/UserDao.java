package cn.ucai.fulicenter.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.bean.UserBean;

/**
 * Created by sks on 2016/4/5.
 */
public class UserDao extends SQLiteOpenHelper {
    public static final String Id = "_id";
    public static final String TABLE_NAME = "user";

    public UserDao(Context context) {
        super(context, "user.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists "+ TABLE_NAME +"( " +
                I.User.ID +" integer primary key autoincrement, " +
                I.User.UID +" integer unique not null, " +
                I.User.USER_NAME +" varchar unique not null, " +
                I.User.NICK +" varchar, " +
                I.User.PASSWORD +" varchar, " +
                I.User.AVATAR +" varchar, " +
                I.User.HEADER +" varchar, " +
                I.User.UN_READ_MSG_COUNT +" int default(0) " +
                ");";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public boolean addUser(UserBean user){
        ContentValues values = new ContentValues();
        values.put(I.User.UID,user.getId());
        values.put(I.User.USER_NAME,user.getUserName());
        values.put(I.User.NICK,user.getNick());
        values.put(I.User.PASSWORD,user.getPassword());
        values.put(I.User.AVATAR,user.getAvatar());
        values.put(I.User.HEADER,user.getHeader());
        values.put(I.User.UN_READ_MSG_COUNT,user.getUnreadMsgCount());
        SQLiteDatabase db = getWritableDatabase();
        long insert = db.insert(TABLE_NAME, null, values);
        return insert>0;
    }

    public UserBean findUserByUserName(String userName){
        if(userName==null)return null;
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select * from "+ TABLE_NAME + " where " + I.User.USER_NAME  + "=?";
        Cursor c = db.rawQuery(sql,new String []{userName});
        if(c.moveToNext()){
            int uid = c.getInt(c.getColumnIndex(I.User.UID));
            String nick = c.getString(c.getColumnIndex(I.User.NICK));
            String password = c.getString(c.getColumnIndex(I.User.PASSWORD));
            String avatar = c.getString(c.getColumnIndex(I.User.AVATAR));
            String header = c.getString(c.getColumnIndex(I.User.HEADER));
            int unReaderMsgCount = c.getInt(c.getColumnIndex(I.User.UN_READ_MSG_COUNT));
            UserBean user = new UserBean(uid,"ok",userName,nick,password,avatar,unReaderMsgCount);
            return user;
        }
        return null;
    }

    public boolean updateUser(UserBean user){
        ContentValues values = new ContentValues();
        values.put(I.User.UID,user.getId());
        values.put(I.User.USER_NAME,user.getUserName());
        values.put(I.User.NICK,user.getNick());
        values.put(I.User.PASSWORD,user.getPassword());
        values.put(I.User.AVATAR,user.getAvatar());
        values.put(I.User.HEADER,user.getHeader());
        values.put(I.User.UN_READ_MSG_COUNT,user.getUnreadMsgCount());
        SQLiteDatabase db = getWritableDatabase();
        long insert = db.update(TABLE_NAME, values,I.User.USER_NAME+"=?",new String[]{user.getUserName()});
        return insert>0;
    }
}
