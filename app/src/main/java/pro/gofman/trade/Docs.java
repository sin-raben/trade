package pro.gofman.trade;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by roman on 14.10.16.
 */

public class Docs extends AbstractItem<Docs, Docs.ViewHolder> {


    private int d_id = 0;
    private int d_num = 0;
    private int d_date = 0;
    private int d_delivery_date = 0;
    private int ca_id = 0;
    private int pd_id = 0;



    private JSONObject head;
    private JSONObject body;


    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.recycleview_docs;
    }

    public void setID(int id) {
        this.d_id = id;
    }
    public int getID() {
        return this.d_id;
    }

    public void setNumber(int num) {
        this.d_num = num;
    }
    public int getNumber() {
        return this.d_num;
    }

    public void setDate(int d_date) {
        this.d_date = d_date;
    }
    public int getDate() { return this.d_date; }

    public void setDateDelivery(int d_date) {
        this.d_delivery_date = d_date;
    }
    public int getDateDelivery() { return this.d_delivery_date; }

    public void setCountragent(int ca_id) {
        this.ca_id = ca_id;
    }
    public int getCountragent() { return this.ca_id; }

    public void setPointDelivery(int pd_id) {
        this.pd_id = pd_id;
    }
    public int getPointDelivery() { return this.pd_id; }




    public void setHead(JSONObject o) {
        this.head = o;
    }
    public JSONObject getHead() { return this.head; }

    public void setBody(JSONObject o) {
        this.body = o;
    }
    public JSONObject getBody() { return this.body; }



    @Override
    public void bindView(Docs.ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        //holder.name.setText(name);
        //holder.description.setText(description);
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
