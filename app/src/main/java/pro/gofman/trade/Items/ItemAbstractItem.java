package pro.gofman.trade.Items;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.mikepenz.fastadapter.items.AbstractItem;
import java.util.List;
import pro.gofman.trade.R;

/**
 * Created by roman on 14.07.16.
 */

public class ItemAbstractItem extends AbstractItem<ItemAbstractItem, ItemAbstractItem.ViewHolder> {

    private ItemObject io;

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.recycleview_items;
    }

    public ItemObject getObj() {
        return io;
    }

    public void setObj(ItemObject io) {
        this.io = io;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        holder.name.setText( this.io.getName() );
        holder.description.setText( this.io.getDescription() );
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