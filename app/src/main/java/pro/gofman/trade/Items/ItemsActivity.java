package pro.gofman.trade.Items;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

import java.util.ArrayList;
import java.util.List;

import pro.gofman.trade.DB;
import pro.gofman.trade.LineDividerItemDecoration;
import pro.gofman.trade.Trade;

import pro.gofman.trade.R;

public class ItemsActivity extends AppCompatActivity {

    private DB db;
    private FastItemAdapter ia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeBlue);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.worker);
        toolbar.setTitle(R.string.title_activity_items);

        db = Trade.getWritableDatabase();

        RecyclerView r = (RecyclerView) findViewById(R.id.recyclerview_items);
        r.setLayoutManager( new LinearLayoutManager( this ) );


        ia = new FastItemAdapter();

        DB db = Trade.getWritableDatabase();
        ia.setNewList( getItems() );

        ia.withSelectable(false);
        ia.withOnClickListener(new FastAdapter.OnClickListener<ItemAbstractItem>() {
            @Override
            public boolean onClick(View v, IAdapter<ItemAbstractItem> adapter, ItemAbstractItem item, int position) {
                DB db = Trade.getWritableDatabase();
                Log.i("CLICK", String.valueOf(position) + " " + String.valueOf(item.getObj().getID()) + " " + db.getSearchString( item.getObj().getID() ));

                Toast.makeText( Trade.getAppContext(), db.getSearchString( item.getObj().getID() ), Toast.LENGTH_SHORT ).show();
                return true;
            }
        });

        r.setLayoutManager( new LinearLayoutManager( this ) );
        r.addItemDecoration( new LineDividerItemDecoration( this ) );
        r.setAdapter( ia );
    }

    // Достаем из базы список всей номенклатуры
    private List<ItemAbstractItem> getItems() {
        List<ItemAbstractItem> r = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT * FROM items", null);
        if ( c != null ) {
            if ( c.moveToFirst() ) {
                do {

                    ItemAbstractItem i = new ItemAbstractItem();
                    i.setObj( new ItemObject() );

                    i.getObj().setID( c.getInt( c.getColumnIndex("i_id") ));
                    i.getObj().setName( c.getString( c.getColumnIndex("i_name") ) );
                    i.getObj().setDescription( "Код номенклатуры: " + String.valueOf( c.getInt( c.getColumnIndex("i_id") ) ) );

                    r.add(i);

                } while ( c.moveToNext() );

                c.close();
            }
        }

        return r;
    }

    private List<ItemAbstractItem> getItemsSearch(String s) {
        List<ItemAbstractItem> r = new ArrayList<>();

        String sql = "";
        sql = "SELECT " + "s.i_id,i.i_name" + " FROM " + "item_search s JOIN items i ON ( s.i_id = i.i_id ) " + " WHERE " + "s.value MATCH '" + s.trim().toUpperCase() + "'";

        Log.d("Search", sql);
        Cursor c = db.rawQuery( sql, null);
        if ( c != null ) {
            if ( c.moveToFirst() ) {
                do {

                    ItemAbstractItem i = new ItemAbstractItem();
                    i.getObj().setID( c.getInt( c.getColumnIndex("i_id") ));
                    i.getObj().setName( c.getString( c.getColumnIndex("i_name") ) );
                    i.getObj().setDescription( "Код номенклатуры: " + String.valueOf( c.getInt( c.getColumnIndex("i_id") ) ) );

                    r.add(i);
                    Log.d("Search", c.getString( c.getColumnIndex("i_name") ));

                } while ( c.moveToNext() );

                c.close();
            }
        }

        return r;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.search).setIcon(new IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_search).color(Color.WHITE).actionBar());

        SearchView sv = (SearchView) menu.findItem(R.id.search).getActionView();
        sv.setQueryHint( getString(R.string.search_items) );

        // Изменение текста в SearchView
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                //Log.d("Search", s );
                if ( s.length() > 2 && !TextUtils.isEmpty(s) ) {
                    ia.setNewList( getItemsSearch( s ) );
                    //Toast.makeText( Trade.getAppContext(), "Найдено: " + String.valueOf( fia.getAdapterItemCount() ), Toast.LENGTH_SHORT ).show();
                } else {
                    if (TextUtils.isEmpty(s)) {

                        ia.setNewList( getItems() );
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

                ia.setNewList( getItems() );
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
