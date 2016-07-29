package pro.gofman.trade;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by roman on 10.07.16.
 */

public class DB {

    private dbHelper mHelper;
    private SQLiteDatabase mDatabase;


    public DB(Context context) {
        mHelper = new dbHelper(context);
        mDatabase = mHelper.getWritableDatabase();
    }

    public void execSQL(String sql) {
        mDatabase.execSQL(sql);
    }

    public boolean addItems(String sql) {

        mDatabase.execSQL(sql);

        return true;
    }

    public int getItemsCount() {
        int result = 0;
        Cursor c = mDatabase.rawQuery("SELECT COUNT(id_i) as count FROM items", null);

        if ( c != null ) {
            if ( c.moveToFirst() ) {
                result = c.getInt( 0 );

                c.close();
            }
        }

        return result;
    }

    public List<Items> getItems() {
        List<Items> r = new ArrayList<Items>();

        Cursor c = mDatabase.rawQuery("SELECT * FROM items", null);
        if ( c != null ) {
            if ( c.moveToFirst() ) {
                do {

                    Items i = new Items();
                    i.withName( c.getString( c.getColumnIndex("name") ) );
                    i.withDescription( "Код номенклатуры: " + String.valueOf( c.getInt( c.getColumnIndex("id_i") ) ) );

                    r.add(i);

                } while ( c.moveToNext() );

                c.close();
            }
        }

        return r;
    }

    public List<Items> getCoords() {
        List<Items> r = new ArrayList<Items>();

        Cursor c = mDatabase.rawQuery("SELECT * FROM coords", null);
        if ( c != null ) {
            if ( c.moveToFirst() ) {
                do {

                    Items i = new Items();
                    i.withName( c.getString( c.getColumnIndex("lat") ) + "," + c.getString( c.getColumnIndex("lon") ) );
                    i.withDescription( "Провайдер: " + c.getString( c.getColumnIndex("provider") ) + " Время: " + String.valueOf( c.getInt( c.getColumnIndex("atime") ) ) );

                    r.add(i);

                } while ( c.moveToNext() );

                c.close();
            }
        }

        return r;
    }

    public JSONObject getCoords2() throws JSONException {
        JSONObject r = new JSONObject();
        r.put("head", "setLogCoord");

        JSONArray points = new JSONArray();
        int i = 0;

        Cursor c = mDatabase.rawQuery("SELECT * FROM coords", null);
        if ( c != null ) {
            if ( c.moveToFirst() ) {
                do {

                    JSONObject coord = new JSONObject();
                    coord.put( "lat", c.getString( c.getColumnIndex("lat") ) );
                    coord.put( "lon", c.getString( c.getColumnIndex("lon") ) );

                    JSONObject point = new JSONObject();
                    point.put( "coord", coord );
                    point.put( "time", c.getInt( c.getColumnIndex("atime") )  );

                    Log.i("DB", point.toString() );


                    points.put( i, point );
                    i++;

                } while ( c.moveToNext() );
            }
        }

        if ( points.length() > 0 ) {

            JSONObject opoints = new JSONObject();
            opoints.put("points",  points );

            r.put( "body", opoints );

        }


        return r;
    }



    private static class dbHelper extends SQLiteOpenHelper {

        protected static final int DATABASE_VERSION = 1;
        protected static final String DATABASE_NAME = "trade";

        private Context context;

        public dbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase s) {

            String sql = "";
            InputStream is = this.context.getResources().openRawResource(R.raw.database);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;

            try {
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {
                //Log.i("JSON", baos.toString("UTF-8") );
                JSONObject database = new JSONObject(baos.toString("UTF-8"));
                Log.i("JSON", database.getString("database"));

                JSONArray tables = database.getJSONArray("tables");
                JSONObject table;

                for (int i = 0; i < tables.length(); i++) {
                    table = tables.getJSONObject(i);

                    sql = SQLBuilderCreateTable(table.getString("table"), table.getJSONArray("fields"));
                    Log.i("SQLCREATE", sql);

                    s.execSQL(sql);
                }

            } catch (UnsupportedEncodingException | JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase s, int i, int i1) {

        }

        private String SQLBuilderCreateTable(String t, JSONArray a) throws JSONException {
            int length = a.length();

            String result = "";
            String d;
            Boolean pk, wr;

            if (length > 0) {
                wr = false;
                result = "CREATE TABLE " + t + " ( ";
                for (int i = 0; i < length; i++) {

                    pk = a.getJSONObject(i).optBoolean("primary_key");
                    result += a.getJSONObject(i).getString("field") + " ";
                    d = i == length - 1 ? " " : ", ";
                    result += a.getJSONObject(i).getString("type");
                    if (pk) {
                        result += " PRIMARY KEY";
                        wr = true;
                    }
                    result += d;
                }
                result += ")";
                if (wr) result += " WITHOUT ROWID";
                result += ";";
            }

            return result;
        }

    } // dbHelper
}
