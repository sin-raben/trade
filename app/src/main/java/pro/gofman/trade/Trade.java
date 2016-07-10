package pro.gofman.trade;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by roman on 10.07.16.
 */

public class Trade extends Application {

    public SQLiteDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();

        this.db = new DB(this.getApplicationContext()).getWritableDatabase();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        this.db.close();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
