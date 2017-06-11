package pro.gofman.trade.Coords;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mikepenz.fastadapter.items.AbstractItem;
import java.util.List;
import pro.gofman.trade.R;

/**
 * Created by roman on 03.12.16.
 */

public class CoordAbstractItem  extends AbstractItem<CoordAbstractItem, CoordAbstractItem.ViewHolder> {


    private CoordObject co;

    public CoordObject getObj() {
        return co;
    }

    public void setObj(CoordObject co) {
        this.co = co;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.recycleview_items;
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public void bindView(CoordAbstractItem.ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        holder.name.setText( this.co.getLan() );
        holder.description.setText( this.co.getLon() );
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
