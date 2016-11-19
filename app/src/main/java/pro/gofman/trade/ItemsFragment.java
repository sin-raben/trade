package pro.gofman.trade;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;


public class ItemsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;


    public ItemsFragment() {
        // Required empty public constructor
    }


    public static ItemsFragment newInstance() {
        ItemsFragment fragment = new ItemsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_items, container, false);
        RecyclerView r = (RecyclerView) v.findViewById(R.id.recycler_view);


        r.setLayoutManager( new LinearLayoutManager( v.getContext() ) );



        Log.i("ia", "001");

        FastItemAdapter ia = Trade.getFastItemAdapter();
        //FastItemAdapter ia = new FastItemAdapter();

        DB db = Trade.getWritableDatabase();
        ia.setNewList( db.getItems() );


        Log.i("ia", "002");
        ia.withSelectable(false);
        ia.withOnClickListener(new FastAdapter.OnClickListener<Items>() {
            @Override
            public boolean onClick(View v, IAdapter<Items> adapter, Items item, int position) {
                DB db = Trade.getWritableDatabase();
                Log.i("CLICK", String.valueOf(position) + " " + String.valueOf(item.getID()) + " " + db.getSearchString( item.getID() ));

                Toast.makeText( Trade.getAppContext(), db.getSearchString( item.getID() ), Toast.LENGTH_SHORT ).show();
                return true;
            }
        });

        Log.i("ia", "003");
        r.setLayoutManager( new LinearLayoutManager( v.getContext() ) );
        r.addItemDecoration( new LineDividerItemDecoration( this.getContext() ) );
        r.setAdapter( ia );


        Log.i("ia", "004");



        Log.i("ia", "005");



        Log.i("ia", "006");
        //ia.setNewList( db.getItemsSearch( "готов* соси*" ) );



        return v;
    }


}