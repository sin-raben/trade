package pro.gofman.trade.Coords;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import java.util.ArrayList;
import java.util.List;

import pro.gofman.trade.DB;
import pro.gofman.trade.R;
import pro.gofman.trade.Trade;

public class CoordsActivity extends AppCompatActivity {

    private DB db;
    private FastItemAdapter ia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeBlue);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coords);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.worker);
        toolbar.setTitle(R.string.title_activity_coords);


        RecyclerView r = (RecyclerView) findViewById(R.id.recyclerview_coords);

        db = Trade.getWritableDatabase();
        ia = new FastItemAdapter();
        ia.withSelectable(false);
        ia.withOnClickListener(new FastAdapter.OnClickListener<CoordAbstractItem>() {
            @Override
            public boolean onClick(View v, IAdapter<CoordAbstractItem> adapter, CoordAbstractItem item, int position) {
                Log.i("CLICK", String.valueOf(position));
                return true;
            }
        });

        r.setLayoutManager( new LinearLayoutManager( this ) );
        r.setAdapter( ia );

        ia.setNewList( getCoords() );

    }


    private List<CoordAbstractItem> getCoords() {
        List<CoordAbstractItem> r = new ArrayList<>();
        String dt;

        Cursor c = db.rawQuery("SELECT * FROM coords", null);
        if ( c != null ) {
            if ( c.moveToFirst() ) {
                do {
                    //Date d = new Date( Long.valueOf( c.getLong(c.getColumnIndex("atime")) )*1000 );
                    //dt = df.format( d );


                    CoordAbstractItem i = new CoordAbstractItem();
                    i.setObj( new CoordObject() );

                    i.getObj().setLan( c.getString( c.getColumnIndex("lat") ) );
                    i.getObj().setLon( c.getString( c.getColumnIndex("lon") ) );
                    i.getObj().setProvider( c.getString( c.getColumnIndex("provider") ) );
                    i.getObj().setTime( c.getInt( c.getColumnIndex("atime") ) );

                    r.add(i);

                } while ( c.moveToNext() );

                c.close();
            }
        }

        return r;
    }


}
