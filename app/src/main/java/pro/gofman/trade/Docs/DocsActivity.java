package pro.gofman.trade.Docs;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import pro.gofman.trade.R;
import pro.gofman.trade.Trade;

public class DocsActivity extends AppCompatActivity {

    private FastItemAdapter ia;
    private DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.worker);
        toolbar.setTitle(R.string.title_activity_docs);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_docs);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        RecyclerView r = (RecyclerView) findViewById(R.id.recyclerview_docs);
        r.setLayoutManager( new LinearLayoutManager( this ) );

        db = Trade.getWritableDatabase();
        ia = new FastItemAdapter();

        ia.withSelectable(false);
        ia.withOnClickListener(new FastAdapter.OnClickListener<Docs>() {
            @Override
            public boolean onClick(View v, IAdapter<Docs> adapter, Docs doc, int position) {
                DB db = Trade.getWritableDatabase();
                Log.i("CLICK", String.valueOf(position) + " " + String.valueOf(doc.getID()) + " " + db.getSearchString( doc.getID() ));

                Toast.makeText( Trade.getAppContext(), "Открываем документ №" + String.valueOf( doc.getID() ), Toast.LENGTH_SHORT ).show();
                return true;
            }
        });

        r.setLayoutManager( new LinearLayoutManager( this ) );
        r.addItemDecoration( new LineDividerItemDecoration( this ) );
        r.setAdapter( ia );

        ia.setNewList( db.getDocs() );
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.search).setIcon(new IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_search).color(Color.WHITE).actionBar());

        SearchView sv = (SearchView) menu.findItem(R.id.search).getActionView();
        sv.setQueryHint( getString(R.string.search_docs) );

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
                    //ia.setNewList( db.getItemsSearch( s ) );
                    //Toast.makeText( Trade.getAppContext(), "Найдено: " + String.valueOf( fia.getAdapterItemCount() ), Toast.LENGTH_SHORT ).show();
                } else {
                    if (TextUtils.isEmpty(s)) {
                        //Log.d("SearchClear", "33");
                        //ia.setNewList( db.getItems() );
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
                //Log.d("SearchClose", "22");
                //ia.setNewList( db.getItems() );
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
                //Log.d("SearchClose", "11");
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
