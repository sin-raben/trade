package pro.gofman.trade.Countragents;

import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

import java.util.ArrayList;
import java.util.List;

import pro.gofman.trade.DB;
import pro.gofman.trade.LineDividerItemDecoration;
import pro.gofman.trade.R;
import pro.gofman.trade.Trade;

public class DeliveryPointActivity extends AppCompatActivity {

    private DB db;
    private FastItemAdapter ia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_point);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.worker);
        toolbar.setTitle(R.string.title_activity_dp);


        db = Trade.getWritableDatabase();

        RecyclerView r = (RecyclerView) findViewById(R.id.recyclerview_items);
        r.setLayoutManager( new LinearLayoutManager( this ) );


        ia = new FastItemAdapter<>();

        DB db = Trade.getWritableDatabase();
        ia.setNewList( getDeliveryPoint() );

        ia.withSelectable(false);
        ia.withOnClickListener(new OnClickListener<DeliveryPointAbstractItem>() {
            @Override
            public boolean onClick(View v, IAdapter<DeliveryPointAbstractItem> adapter, DeliveryPointAbstractItem item, int position) {

                Toast.makeText( Trade.getAppContext(), String.valueOf( item.getObj().getID() ), Toast.LENGTH_SHORT ).show();
                return true;
            }
        });

        r.setLayoutManager( new LinearLayoutManager( this ) );
        r.addItemDecoration( new LineDividerItemDecoration( this ) );
        r.setAdapter( ia );
    }

    // Получаем из базы весь список Точек доставки
    private List<DeliveryPointAbstractItem> getDeliveryPoint() {
        List<DeliveryPointAbstractItem> r = new ArrayList<>();

        Log.i("SELECTDP", "Старт");
        Cursor c = db.rawQuery("SELECT " +
                "dp.dp_name, " +
                "dp.dp_id, " +
                "a.adr_str " +
                "FROM " +
                "  point_delivery dp " +
                "  JOIN addresses a ON (dp.dp_id = a.any_id AND a.adrt_id = 3) " +
                "ORDER BY dp.dp_name", null);

        Log.i("SELECTDP", "Стoп " + String.valueOf(c.getCount()));

        if ( c != null ) {
            if ( c.moveToFirst() ) {
                do {

                    DeliveryPointAbstractItem i = new DeliveryPointAbstractItem();
                    DeliveryPointObject o = new DeliveryPointObject();

                    o.setID( c.getInt( c.getColumnIndex("dp_id") ));
                    o.setName( c.getString( c.getColumnIndex("dp_name") ) );
                    o.setAdr( c.getString( c.getColumnIndex("adr_str") ) );

                    i.setObj(o);
                    r.add(i);

                } while ( c.moveToNext() );

                c.close();
            }
        }

        Log.i("SELECTDP", "Ещё Стoп");

        return r;
    }
    public List<DeliveryPointAbstractItem> getDPSearch(String s) {
        List<DeliveryPointAbstractItem> r = new ArrayList<>();

        String sql = "";
        /*
        sql = "SELECT " +
                "s.dp_id" +
                " FROM " +
                "dp_search s" +
                " WHERE " + "s.value MATCH '" + s.trim().toUpperCase() + "'";

        */

        ///*
        sql = "SELECT " +
                "s.dp_id, dp.dp_name, a.adr_str" +
                " FROM " +
                "dp_search s LEFT JOIN point_delivery dp ON ( s.dp_id = dp.dp_id ) " +
                "JOIN addresses a ON ( s.dp_id = a.any_id AND a.adrt_id = 3) " +
                "WHERE " + "s.value MATCH '" + s.trim().toUpperCase() + "'";
        //*/

        /*
        sql = "SELECT " +
                "s.dp_id, dp.dp_name, a.adr_str" +
                " FROM " +
                "dp_search s, point_delivery dp, addresses a " +
                "WHERE " + "s.value MATCH '" + s.trim().toUpperCase() + "' AND " +
                "dp.dp_id = s.dp_id AND (a.any_id = s.dp_id AND a.adrt_id = 3)";
       */

        Log.i("Search", sql);
        Cursor c = db.rawQuery( sql, null);
        Log.i("Search", "Запрос выполнен " + String.valueOf( c.getCount() ) );

        ///*
        if ( c != null ) {
            if ( c.moveToFirst() ) {
                do {

                    DeliveryPointAbstractItem i = new DeliveryPointAbstractItem();
                    i.setObj( new DeliveryPointObject() );
                    i.getObj().setID( c.getInt( c.getColumnIndex("dp_id") ));
                    i.getObj().setName( c.getString( c.getColumnIndex("dp_name") ) );
                    i.getObj().setAdr( c.getString( c.getColumnIndex("adr_str") ) );

                    r.add(i);
                    //Log.i("Search", c.getString( c.getColumnIndex("dp_name") ));
                } while ( c.moveToNext() );

                c.close();
            }
        }
        //*/

        return r;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.search).setIcon(new IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_search).color(Color.WHITE).actionBar());

        SearchView sv = (SearchView) menu.findItem(R.id.search).getActionView();
        sv.setQueryHint( getString(R.string.search_dp) );

        // Изменение текста в SearchView
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                if ( s.length() > 2 && !TextUtils.isEmpty(s) ) {
                    ia.setNewList( getDPSearch( s ) );
                    //Toast.makeText( Trade.getAppContext(), "Найдено: " + String.valueOf( fia.getAdapterItemCount() ), Toast.LENGTH_SHORT ).show();
                } else {
                    if (TextUtils.isEmpty(s)) {

                        ia.setNewList( getDeliveryPoint() );
                        Toast.makeText(Trade.getAppContext(), "Всего: " + String.valueOf( ia.getAdapterItemCount()), Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });

        // Закрытие SearchView
        MenuItem si = menu.findItem(R.id.search);
        MenuItemCompat.setOnActionExpandListener(si, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                ia.setNewList( getDeliveryPoint() );
                Toast.makeText( Trade.getAppContext(), "Всего: " + String.valueOf( ia.getAdapterItemCount() ), Toast.LENGTH_SHORT ).show();
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch ( item.getItemId() ) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        //if ( dw != null && dw.isDrawerOpen() ) {
        //    dw.closeDrawer();
        //} else
        super.onBackPressed();
    }

}
