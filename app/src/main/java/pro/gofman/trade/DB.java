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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;;import pro.gofman.trade.Docs.Docs;
import pro.gofman.trade.Items.ItemObject;
import pro.gofman.trade.Items.Items;


/**
 * Created by roman on 10.07.16.
 */

public class DB {

    private dbHelper mHelper;
    private SQLiteDatabase mDatabase;
    private SimpleDateFormat df;

    protected static final int OPTION_CONNECTION = 1;
    protected static final int OPTION_AUTH = 2;
    protected static final int OPTION_COORD = 3;

    public DB(Context context) {
        mHelper = new dbHelper(context);
        mDatabase = mHelper.getWritableDatabase();

        df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    }

    public String getOptions(int o_id) {
        String r = "";
        Cursor c = mDatabase.rawQuery("SELECT o_data FROM options WHERE o_id = " + String.valueOf( o_id ), null );
        if ( c != null ) {
            if ( c.moveToFirst() ) {
                r = c.getString(0);
                c.close();
            }
        }
        return r;
    }
    public boolean setOptions(int o_id, String json) {

        ContentValues cv = new ContentValues();
        cv.put( "o_data", json );

        return 1 == mDatabase.update( "options", cv, "o_id = " + String.valueOf(o_id), null );
    }

    public void execSQL(String sql) {
        mDatabase.execSQL(sql);
    }

    public void insert(String tn, ContentValues cv) {
        mDatabase.insert(tn, null, cv);
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

    // Список всех документов
    public List<Docs> getDocs() {
        List<Docs> r = new ArrayList<>();

        Cursor c = mDatabase.rawQuery( "SELECT * FROM docs", null );
        if ( c != null ) {
            if ( c.moveToFirst() ) {
                do {
                    Docs d = new Docs();

                    Date d_date = new Date(c.getLong(c.getColumnIndex("d_date")) * 1000);

                    d.setID(c.getInt(c.getColumnIndex("d_id")));
                    d.setNumber(c.getInt(c.getColumnIndex("d_num")));
                    d.setDate(df.format(d_date));
                    d.setPointDelivery("Солнечный круг №135, Ворошиловский, 50");
                    d.setPointAdr("344003, Ростов-на-Дону г., Ворошиловский пр-кт, д. 50");


                    r.add(d);

                } while ( c.moveToNext() );
            }

            c.close();
        }


        return r;
    }

    // Список всей номенклатуры
    public List<Items> getItems() {
        List<Items> r = new ArrayList<>();

        Cursor c = mDatabase.rawQuery("SELECT * FROM items", null);
        if ( c != null ) {
            if ( c.moveToFirst() ) {
                do {

                    Items i = new Items();
                    i.setID( c.getInt( c.getColumnIndex("i_id") ));
                    i.withName( c.getString( c.getColumnIndex("i_name") ) );
                    i.withDescription( "Код номенклатуры: " + String.valueOf( c.getInt( c.getColumnIndex("i_id") ) ) );

                    r.add(i);

                } while ( c.moveToNext() );

                c.close();
            }
        }

        return r;
    }

    public List<Items> getItemsSearch(String s) {
        List<Items> r = new ArrayList<>();

        /*

        SELECT
            s.i_id,
            i.i_name
        FROM
            item_search s
            JOIN items i ON ( s.i_id = i.i_id )
        WHERE
            s.value MATCH 'подстрока'


         */

        String sql = "";
        sql = "SELECT " + "s.i_id,i.i_name" + " FROM " + "item_search s JOIN items i ON ( s.i_id = i.i_id ) " + " WHERE " + "s.value MATCH '" + s.trim().toUpperCase() + "'";
        //sql = "SELECT " + "s.i_id,i.i_name" + " FROM " + "item_search s JOIN items i ON ( s.i_id = i.i_id ) ";

        Log.d("Search", sql);
        Cursor c = mDatabase.rawQuery( sql, null);
        if ( c != null ) {
            if ( c.moveToFirst() ) {
                do {

                    Items i = new Items();
                    i.setID( c.getInt( c.getColumnIndex("i_id") ));
                    i.withName( c.getString( c.getColumnIndex("i_name") ) );
                    i.withDescription( "Код номенклатуры: " + String.valueOf( c.getInt( c.getColumnIndex("i_id") ) ) );

                    r.add(i);

                    Log.d("Search", c.getString( c.getColumnIndex("i_name") ));

                } while ( c.moveToNext() );

                c.close();
            }
        }

        return r;
    }
    public List<ItemObject> getItemSearch(String s) {
        List<ItemObject> r = new ArrayList<>();

        /*

        SELECT
            s.i_id,
            i.i_name
        FROM
            item_search s
            JOIN items i ON ( s.i_id = i.i_id )
        WHERE
            s.value MATCH 'подстрока'


         */

        String sql = "";
        sql = "SELECT " + "s.i_id, i.i_name" + " FROM " + "item_search s JOIN items i ON ( s.i_id = i.i_id ) " + " WHERE " + "s.value MATCH '" + s.trim().toUpperCase() + "'";
        //sql = "SELECT " + "s.i_id,i.i_name" + " FROM " + "item_search s JOIN items i ON ( s.i_id = i.i_id ) ";

        Log.i("Search", sql);
        Cursor c = mDatabase.rawQuery( sql, null);
        if ( c != null ) {
            if ( c.moveToFirst() ) {
                do {

                    ItemObject i = new ItemObject();
                    i.setID( c.getInt( c.getColumnIndex("i_id") ));
                    i.setName( c.getString( c.getColumnIndex("i_name") ) );
                    i.setDescription( "Код номенклатуры: " + String.valueOf( c.getInt( c.getColumnIndex("i_id") ) ) );

                    r.add(i);

                    Log.i("Search", c.getString( c.getColumnIndex("i_name") ));

                } while ( c.moveToNext() );

                c.close();
            }
        }

        return r;
    }


    public String getSearchString(int id) {
        String r = "";
        Cursor c = mDatabase.rawQuery( "SELECT value FROM item_search WHERE i_id = " + String.valueOf(id), null );
        if ( c != null ) {
            if ( c.moveToFirst() ) {

                r = c.getString( c.getColumnIndex("value") );

                c.close();
            }
        }
        return r;

    }


    public List<Items> getCoords() {
        List<Items> r = new ArrayList<>();
        String dt;

        Cursor c = mDatabase.rawQuery("SELECT * FROM coords", null);
        if ( c != null ) {
            if ( c.moveToFirst() ) {
                do {


                    //dt = new Date( c.getInt( c.getColumnIndex("atime")*1000 )).toString();

                    Date d = new Date( Long.valueOf( c.getLong(c.getColumnIndex("atime")) )*1000 );
                    dt = df.format( d );


                    Items i = new Items();
                    i.withName( c.getString( c.getColumnIndex("lat") ) + "," + c.getString( c.getColumnIndex("lon") ) );
                    i.withDescription( "Провайдер: " + c.getString( c.getColumnIndex("provider") ) + " Время: " + dt );

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

                c.close();
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

                    sql = SQLBuilderCreateTable( table.getString("table"), table.getJSONArray("fields"), table.optBoolean("fts3") );

                    s.execSQL(sql);
                    Log.i("SQLCREATE", sql);

                    if ( table.optJSONArray("values") != null ) {
                        FillTable(table.getString("table"), table.optJSONArray("values"), s);
                        //s.execSQL(sql);
                        //Log.i("SQLFILL", sql);
                    }

                }

            } catch (UnsupportedEncodingException | JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase s, int i, int i1) {

        }

        // t - Наименование таблицы
        // a - Столбцы
        // f - Свойства таблицы (пока одно)
        private String SQLBuilderCreateTable(String t, JSONArray a, Boolean f) throws JSONException {
            int length = a.length();

            String result = "";
            String d;
            Boolean pk, uk, wr;

            if (length > 0) {
                wr = false;

                if ( f ) {
                    result = "CREATE VIRTUAL TABLE " + t + " USING fts3 ( ";
                } else {
                    result = "CREATE TABLE " + t + " ( ";
                }

                for (int i = 0; i < length; i++) {

                    pk = a.getJSONObject(i).optBoolean("primary_key", false);
                    uk = a.getJSONObject(i).optBoolean("unique_key", false);
                    result += a.getJSONObject(i).getString("field") + " ";
                    d = i == length - 1 ? " " : ", ";
                    result += a.getJSONObject(i).getString("type");
                    if ( pk ) {
                        if ( !f ) result += " PRIMARY KEY";
                        wr = true;
                    }
                    if ( uk ) {
                        result += " UNIQUE KEY";
                    }
                    result += d;
                }
                result += ")";
                if (wr && !f) result += " WITHOUT ROWID";
                result += ";";
            }

            return result;
        }
        private void FillTable(String t, JSONArray a, SQLiteDatabase s) throws JSONException {
            int length = a.length();
            JSONArray f;


            if (length > 0) {
                f = a.getJSONObject(0).names();
                ContentValues cv;

                for (int i=0; i < length; i++) {
                    cv = new ContentValues();
                    for (int j=0; j < f.length(); j++) {
                        cv.put( f.getString(j), a.getJSONObject(i).getString(f.getString(j)) );
                        Log.i("SQLVALUES", f.getString(j) + " : " + a.getJSONObject(i).getString(f.getString(j)) );
                    }
                    s.insert(t, null, cv);

                }

            }

        }

    } // dbHelper
}
