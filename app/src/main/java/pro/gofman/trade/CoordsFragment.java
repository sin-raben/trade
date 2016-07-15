package pro.gofman.trade;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;


public class CoordsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;


    public CoordsFragment() {
        // Required empty public constructor
    }


    public static CoordsFragment newInstance() {
        CoordsFragment fragment = new CoordsFragment();

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


        FastItemAdapter ia = new FastItemAdapter();
        ia.withSelectable(false);
        ia.withOnClickListener(new FastAdapter.OnClickListener<Items>() {
            @Override
            public boolean onClick(View v, IAdapter<Items> adapter, Items item, int position) {
                Log.i("CLICK", String.valueOf(position));
                return true;
            }
        });


        r.setLayoutManager( new LinearLayoutManager( v.getContext() ) );
        r.setAdapter( ia );

        DB db = Trade.getWritableDatabase();



        ia.add( db.getCoords() );




        return v;
    }


}