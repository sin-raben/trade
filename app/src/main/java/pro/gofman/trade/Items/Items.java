package pro.gofman.trade.Items;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import pro.gofman.trade.R;

/**
 * Created by roman on 14.07.16.
 */

public class Items extends AbstractItem<Items, Items.ViewHolder> {


    public String name = "";
    public String description = "";
    private int i_id = 0;


    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.recycleview_items;
    }

    public Items withName(String Name) {
        this.name = new String(Name);
        return this;
    }

    public Items withDescription(String description) {
        this.description = new String(description);
        return this;
    }

    public void setID(int id) {
        this.i_id = id;
    }
    public int getID() {
        return this.i_id;
    }


 /*   @Override
    public void bindView(ViewHolder holder) {
        super.bindView(holder);

        holder.name.setText(name);
        holder.description.setText(description);
    }*/

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        Log.i("ia", "010");

        holder.name.setText(name);
        holder.description.setText(description);


    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView name;
        protected TextView description;

        public ViewHolder(View view) {
            super(view);

            Log.i("ia", "010");

            this.name = (TextView) view.findViewById(R.id.recycleview_item_name);
            this.description = (TextView) view.findViewById(R.id.recycleview_item_desc);
        }
    }
}
