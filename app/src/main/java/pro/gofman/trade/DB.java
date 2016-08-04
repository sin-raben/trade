package pro.gofman.trade;

import android.content.ContentValues;
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

    public void insert(String dn, ContentValues cv) {
        mDatabase.insert(dn, null, cv);
    }


    public int getItemsCount() {
        int result = 0;
        Cursor c = mDatabase.rawQuery("SELECT COUNT(i_id) as count FROM items", null);

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
                    i.withName( c.getString( c.getColumnIndex("i_name") ) );
                    i.withDescription( "Код номенклатуры: " + String.valueOf( c.getInt( c.getColumnIndex("i_id") ) ) );

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
                    s.execSQL(sql);
                    Log.i("SQLCREATE", sql);

                    if ( table.optJSONArray("values") != null ) {
                        sql = SQLBuilderFillTable(table.getString("table"), table.optJSONArray("values"));
                        s.execSQL(sql);
                        Log.i("SQLFILL", sql);
                    }

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
        private String SQLBuilderFillTable(String t, JSONArray a) throws JSONException {
            int length = a.length();
            String result = "", d = "";
            JSONArray f;


            if (length > 0) {
                f = a.getJSONObject(0).names();

                result = "INSERT INTO " + t + " (";

                for (int i=0; i < f.length(); i++) {
                    result += f.getString(i);
                    d = i == f.length() - 1 ? " " : ", ";
                    result += d;
                }

                result += ") VALUES ";

                for (int i=0; i < length; i++) {
                    result += "( ";

                    for (int j=0; j < f.length(); j++ ) {
                        result += "\"" + a.getJSONObject(i).getString( f.getString(j) ) + "\"";
                        d = j == f.length() - 1 ? " " : ", ";
                        result += d;
                    }

                    result += " );";
                }


            }


            return result;
        }

    } // dbHelper
}
