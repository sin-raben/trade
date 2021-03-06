package pro.gofman.trade.News;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import pro.gofman.trade.News.NewsObject;
import pro.gofman.trade.R;

/**
 * Created by roman on 04.10.17.
 */


public class NewsAbstractItem extends AbstractItem<NewsAbstractItem, NewsAbstractItem.ViewHolder> {

    private NewsObject no;


    @Override
    public int getType() {
        return no.getType();
    }

    @Override
    public int getLayoutRes() {
        if (no.getType() == 2) {
            return R.layout.recycleview_news_3;
        } else {
            return R.layout.recycleview_news;
        }
    }

    public NewsObject getObj() {
        return no;
    }

    public void setObj(NewsObject no) {
        this.no = no;
    }

    @Override
    public NewsAbstractItem.ViewHolder getViewHolder(View v) {
        return new NewsAbstractItem.ViewHolder(v);
    }

    @Override
    public void bindView(NewsAbstractItem.ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        holder.name.setText( this.no.getTitle() );
        holder.description.setText( this.no.getText() );
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView name;
        protected TextView description;
        protected Button btnOK;

        public ViewHolder(View view) {
            super(view);

            this.name = (TextView) view.findViewById(R.id.recycleview_news_name);
            this.description = (TextView) view.findViewById(R.id.recycleview_news_desc);
            this.btnOK = (Button) view.findViewById(R.id.recycleview_news_btnOK);
//            btnOK.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.i("CARDVIEW", v.toString());
//                }
//            });
        }
    }
}