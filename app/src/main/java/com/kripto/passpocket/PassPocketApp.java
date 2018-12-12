package com.kripto.passpocket;

import android.app.Application;

import com.kripto.passpocket.db.DaoMaster;
import com.kripto.passpocket.db.DaoSession;

import org.greenrobot.greendao.database.Database;

public class PassPocketApp extends Application {

    private DaoSession mDaoSession;


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "passpocket-db", null);
        Database db = helper.getWritableDb();
        mDaoSession = new DaoMaster(db).newSession();

    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }
}
