package pro.gofman.trade.Docs;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import pro.gofman.trade.R;

/**
 * Created by roman on 14.10.16.
 */

public class Docs extends AbstractItem<Docs, Docs.ViewHolder> {

    private int d_id = 0;
    private int d_num = 0;
    private String d_date = "";
    private String d_delivery_date = "";
    private String ca_name = "";
    private String pd_name = "";
    private String pd_adr = "";

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.recycleview_docs;
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
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

    public void setDate(String d_date) {
        this.d_date = d_date;
    }

    public String getDate() {
        return this.d_date;
    }

    public void setDateDelivery(String d_date) {
        this.d_delivery_date = d_date;
    }

    public String getDateDelivery() {
        return this.d_delivery_date;
    }

    public void setCountragent(String ca_name) {
        this.ca_name = ca_name;
    }

    public String getCountragent() {
        return this.ca_name;
    }

    public void setPointDelivery(String pd_name) {
        this.pd_name = pd_name;
    }

    public String getPointDelivery() {
        return this.pd_name;
    }

    public void setPointAdr(String pd_adr) {
        this.pd_adr = pd_adr;
    }

    public String getPointAdr() {
        return this.pd_adr;
    }


    @Override
    public void bindView(Docs.ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        holder.doc_num.setText("ГРС" + String.valueOf(d_num) + " от " + d_date);
        holder.doc_desc.setText(pd_name);
        holder.pd_adr.setText(pd_adr);
        holder.items_amount.setText("Позиций: " + String.valueOf(d_id * 2 + 10));
        holder.doc_summa.setText("Сумма: " + String.valueOf(d_id * d_num * 43) + " руб.");
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView doc_num;
        protected TextView doc_desc;
        protected TextView pd_adr;
        protected TextView items_amount, doc_summa;

        public ViewHolder(View view) {
            super(view);
            this.doc_num = (TextView) view.findViewById(R.id.recycleview_doc_num);
            this.doc_desc = (TextView) view.findViewById(R.id.recycleview_doc_desc);
            this.pd_adr = (TextView) view.findViewById(R.id.recycleview_pd_adr);
            this.items_amount = (TextView) view.findViewById(R.id.recycleview_items_postion);
            this.doc_summa = (TextView) view.findViewById(R.id.recycleview_doc_summa);
        }
    }

}
