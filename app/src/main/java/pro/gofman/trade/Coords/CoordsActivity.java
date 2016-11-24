package pro.gofman.trade.Coords;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import pro.gofman.trade.DB;
import pro.gofman.trade.Items.Items;
import pro.gofman.trade.R;
import pro.gofman.trade.Trade;

public class CoordsActivity extends AppCompatActivity {

    private DB db;
    private FastItemAdapter ia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        ia.withOnClickListener(new FastAdapter.OnClickListener<Items>() {
            @Override
            public boolean onClick(View v, IAdapter<Items> adapter, Items item, int position) {
                Log.i("CLICK", String.valueOf(position));
                return true;
            }
        });

        r.setLayoutManager( new LinearLayoutManager( this ) );
        r.setAdapter( ia );

        ia.setNewList( db.getCoords() );

    }

}
