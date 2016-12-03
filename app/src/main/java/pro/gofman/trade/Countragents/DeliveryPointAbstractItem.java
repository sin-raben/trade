package pro.gofman.trade.Countragents;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;
import pro.gofman.trade.R;

/**
 * Created by roman on 03.12.16.
 */

public class DeliveryPointAbstractItem extends AbstractItem<DeliveryPointAbstractItem, DeliveryPointAbstractItem.ViewHolder> {

    private DeliveryPointObject obj;

    public void setObj(DeliveryPointObject o) {
        this.obj = o;
    }

    public DeliveryPointObject getObj() {
        return this.obj;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.recycleview_delivery_point;
    }


    @Override
    public void bindView(DeliveryPointAbstractItem.ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        holder.name.setText( obj.getName() );
        holder.adr.setText( obj.getAdr() );
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView name;
        protected TextView adr;

        public ViewHolder(View view) {
            super(view);

            this.name = (TextView) view.findViewById(R.id.recycleview_dp_name);
            this.adr = (TextView) view.findViewById(R.id.recycleview_dp_adr);
        }
    }

}
