package pro.gofman.trade.News;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.ArrayList;
import java.util.List;

import pro.gofman.trade.DB;
import pro.gofman.trade.Items.ItemAbstractItem;
import pro.gofman.trade.Items.ItemObject;
import pro.gofman.trade.LineDividerItemDecoration;
import pro.gofman.trade.R;
import pro.gofman.trade.Trade;

public class NewsActivity extends AppCompatActivity {
    private DB db;
    private FastItemAdapter ia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.worker);
        toolbar.setTitle(R.string.title_activity_news);

        // Получаем входящие параметры
        /*if (getIntent().getExtras() != null) {
            String cmd = getIntent().getExtras().getString( ITEMS_PARAM );
        }*/


        db = Trade.getWritableDatabase();

        RecyclerView r = (RecyclerView) findViewById(R.id.recyclerview_news);
        r.setLayoutManager( new LinearLayoutManager( this ) );


        ia = new FastItemAdapter();

        DB db = Trade.getWritableDatabase();
        ia.setNewList( getNews() );

        ia.withSelectable(false);
        ia.withOnClickListener(new FastAdapter.OnClickListener<NewsAbstractItem>() {
            @Override
            public boolean onClick(View v, IAdapter<NewsAbstractItem> adapter, NewsAbstractItem item, int position) {
                //DB db = Trade.getWritableDatabase();
                //Log.i("CLICK", String.valueOf(position) + " " + String.valueOf(item.getObj().getID()) + " " + db.getSearchString( item.getObj().getID() ));

                Toast.makeText( v.getContext(), "Новость: " + String.valueOf( item.getObj().getID() ), Toast.LENGTH_SHORT ).show();
                return true;
            }
        });

        r.setLayoutManager( new LinearLayoutManager( this ) );
        r.addItemDecoration( new LineDividerItemDecoration( this ) );
        r.setAdapter( ia );
    }

    // Достаем из базы список всей номенклатуры
    private List<NewsAbstractItem> getNews() {
        List<NewsAbstractItem> r = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT * FROM news ORDER BY n_date", null);
        if ( c != null ) {
            if ( c.moveToFirst() ) {
                do {

                    NewsAbstractItem i = new NewsAbstractItem();
                    i.setObj( new NewsObject() );

                    i.getObj().setID( c.getInt( c.getColumnIndex("n_id") ));
                    i.getObj().setTitle( c.getString( c.getColumnIndex("n_title") ) );
                    i.getObj().setText( c.getString( c.getColumnIndex("n_text")  ) );
                    i.getObj().setType( c.getInt(c.getColumnIndex("n_type" ) ) );

                    r.add(i);

                } while ( c.moveToNext() );

                c.close();
            }
        }

        return r;
    }

    private List<NewsAbstractItem> getItemsSearch(String s) {
        List<NewsAbstractItem> r = new ArrayList<>();
/*
        String sql = "";
        sql = "SELECT " +
                "s.i_id, i.i_name" +
                " FROM " +
                "item_search s JOIN items i ON ( s.i_id = i.i_id ) " +
                " WHERE " + "s.value MATCH '" + s.trim().toUpperCase() + "'";

        Log.i("Search", sql);
        Cursor c = db.rawQuery( sql, null);
        if ( c != null ) {
            if ( c.moveToFirst() ) {
                do {

                    NewsAbstractItem i = new NewsAbstractItem();
                    i.setObj( new NewsObject() );
                    i.getObj().setID( c.getInt( c.getColumnIndex("i_id") ));
                    i.getObj().setName( c.getString( c.getColumnIndex("i_name") ) );
                    i.getObj().setDescription( "Код номенклатуры: " + String.valueOf( c.getInt( c.getColumnIndex("i_id") ) ) );

                    r.add(i);


                } while ( c.moveToNext() );

                c.close();
            }
        }*/
        return r;
    }

}
