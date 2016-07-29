package pro.gofman.trade;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.materialdrawer.holder.StringHolder;

/**
 * Created by roman on 14.07.16.
 */

public class Items extends AbstractItem<Items, Items.ViewHolder> {

    public String name = "";
    public String description = "";

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

    @Override
    public void bindView(ViewHolder holder) {
        super.bindView(holder);

        holder.name.setText(name);
        holder.description.setText(description);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView name;
        protected TextView description;

        public ViewHolder(View view) {
            super(view);
            this.name = (TextView) view.findViewById(R.id.recycleview_item_name);
            this.description = (TextView) view.findViewById(R.id.recycleview_item_desc);
        }
    }
}
