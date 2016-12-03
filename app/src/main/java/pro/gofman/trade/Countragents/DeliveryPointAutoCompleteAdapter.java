package pro.gofman.trade.Countragents;

/**
 * Created by roman on 03.12.16.
 */

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pro.gofman.trade.DB;
import pro.gofman.trade.Items.ItemObject;
import pro.gofman.trade.R;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pro.gofman.trade.DB;
import pro.gofman.trade.R;
import pro.gofman.trade.Trade;

/**
 * Created by roman on 29.11.16.
 */
/*
public class ItemsAutoCompleteAdapter extends CursorAdapter implements android.widget.AdapterView.OnItemClickListener {

    private DB mDB;
    private Context mContext;

    public ItemsAutoCompleteAdapter(Context context, DB db, int flags) {
        super(context);
        mContext = context;
        mDB = db;

    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        if ( getFilterQueryProvider() != null ) {
            return getFilterQueryProvider().runQuery(constraint);
        }

        Cursor c = null; // mDB.getItemsSearch( constraint.toString() );


        return c;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}

*/


public class DeliveryPointAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private Context mContext;
    private List<DeliveryPointObject> mItems;
    private DB mDB;

    public DeliveryPointAutoCompleteAdapter(Context context, DB db) {
        mContext = context;
        mItems = new ArrayList<DeliveryPointObject>();

        mItems.add( new DeliveryPointObject() );
        mDB = db;
        Log.i("ADAPTER", "3");
    }


    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public DeliveryPointObject getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Log.i("ADAPTER", "1");
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.items_dropdown_autocomplete, viewGroup, false);
        }

        DeliveryPointObject item = getItem( i );
        ((TextView) view.findViewById(R.id.textItems)).setText(item.getName());
        ((TextView) view.findViewById(R.id.textGroup)).setText(item.getAdr());

        return view;
    }

    @Override
    public Filter getFilter() {
        Log.i("ADAPTER", "2");
        Filter f = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                Log.i("ADAPTER", "performFiltering: " + charSequence.toString() );
                FilterResults filterResults = new FilterResults();
                if (charSequence != null) {

                    List<DeliveryPointObject> items = findDeliverPoints( charSequence.toString() );

                    filterResults.values = items;
                    filterResults.count = items.size();

                    Log.i("ADAPTER", "Count: " + String.valueOf(items.size()) );
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults != null && filterResults.count > 0) {
                    mItems = (List<DeliveryPointObject>) filterResults.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };

        return f;
    }

    private List<DeliveryPointObject> findDeliverPoints(String s) {
        Log.i("ADAPTER", "4");
        List<DeliveryPointObject> r = new ArrayList<>();

        String sql = "";
        sql = "SELECT " +
                "s.dp_id, dp.dp_name, a.adr_str" +
                " FROM " +
                "dp_search s JOIN point_delivery dp ON ( s.dp_id = dp.dp_id ) " +
                "JOIN addresses a ON ( s.dp_id = a.any_id AND a.adrt_id = 3) " +
                " WHERE " + "s.value MATCH '" + s.trim().toUpperCase() + "'";
        //sql = "SELECT " + "s.i_id,i.i_name" + " FROM " + "item_search s JOIN items i ON ( s.i_id = i.i_id ) ";

        Log.i("Search", sql);
        Cursor c = mDB.rawQuery( sql, null);
        if ( c != null ) {
            if ( c.moveToFirst() ) {
                do {

                    DeliveryPointObject i = new DeliveryPointObject();

                    i.setID( c.getInt( c.getColumnIndex("dp_id") ));
                    i.setName( c.getString( c.getColumnIndex("dp_name") ) );
                    i.setAdr( c.getString( c.getColumnIndex("adr_str") ) );

                    r.add(i);

                    //Log.i("Search", c.getString( c.getColumnIndex("i_name") ));

                } while ( c.moveToNext() );

                c.close();
            }
        }

        return r;
    }
}

