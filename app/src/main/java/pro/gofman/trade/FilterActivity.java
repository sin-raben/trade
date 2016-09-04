package pro.gofman.trade;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
//import com.mikepenz.fastadapter.app.items.ExpandableItem;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

//import com.mikepenz.fastadapter_extensions.ActionModeHelper;

public class FilterActivity extends AppCompatActivity {

    private FastAdapter<Items> mFastAdapter;
    // private HeaderAdapter<SampleItem> mHeaderAdapter;
    private ItemAdapter<Items> mItemAdapter;
    // private ActionModeHelper mActionModeHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);


        mFastAdapter = new FastAdapter<>();
        mItemAdapter = new ItemAdapter<>();


        mFastAdapter.withSelectable(true);
        mFastAdapter.withMultiSelect(true);
        mFastAdapter.withSelectOnLongClick(true);



        //get our recyclerView and do basic setup
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new DefaultItemAnimator());
        //rv.setAdapter(stickyHeaderAdapter.wrap(mItemAdapter.wrap(mHeaderAdapter.wrap(mFastAdapter))));

        //final StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(stickyHeaderAdapter);
        //rv.addItemDecoration(decoration);

    }

    private void setItems() {
        List<Items> items = new ArrayList<>();
        int size = new Random().nextInt(25) + 10;
        for (int i = 1; i <= size; i++) {
            if (i % 6 == 0) {
                ExpandableItem expandableItem = new ExpandableItem().withName("Test " + i).withHeader(headers[i / 5]).withIdentifier(100 + i);
                List<Items> subItems = new LinkedList<>();
                for (int ii = 1; ii <= 3; ii++) {
                    ExpandableItem subItem = new ExpandableItem().withName("-- SubTest " + ii).withHeader(headers[i / 5]).withIdentifier(1000 + ii);

                    List<Items> subSubItems = new LinkedList<>();
                    for (int iii = 1; iii <= 3; iii++) {
                        subSubItems.add(new SampleItem().withName("---- SubSubTest " + iii).withHeader(headers[i / 5]).withIdentifier(10000 + iii));
                    }
                    subItem.withSubItems(subSubItems);

                    subItems.add(subItem);
                }
                expandableItem.withSubItems(subItems);
                items.add(expandableItem);
            } else {
                items.add(new SampleItem().withName("Test " + i).withHeader(headers[i / 5]).withIdentifier(i));
            }
        }
        mItemAdapter.set(items);
    }
}
