package xyz.wendyltanpcy.jandancomment.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.andview.refreshview.recyclerview.BaseRecyclerAdapter;
import com.bumptech.glide.request.target.DrawableImageViewTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import xyz.wendyltanpcy.jandancomment.R;
import xyz.wendyltanpcy.jandancomment.helper.GlideApp;
import xyz.wendyltanpcy.jandancomment.helper.MyProgressTarget;
import xyz.wendyltanpcy.jandancomment.helper.PictureHandle;

/**
 * Created by Wendy on 2017/10/25.
 */

public class GirlListAdapter extends BaseRecyclerAdapter<GirlListAdapter.ViewHolder> {

    private List<Map<String,String>>girlList = new ArrayList<>();
    private Context mContext;


    @Override
    public  ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.girls_list_item, parent, false);
        mContext = parent.getContext();
        ViewHolder vh = new GirlListAdapter.ViewHolder(v, true);
        return vh;
    }

    @Override
    public ViewHolder getViewHolder(View view) {
        return new ViewHolder(view,true);
    }

    public GirlListAdapter(List<Map<String, String>> list){
        girlList = list;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position, boolean isItem) {
        Map<String,String> map = girlList.get(position);
        holder.userName.setText(map.get("userName"));
        holder.number.setText(map.get("number"));
        holder.time.setText(map.get("time"));
        final String url  = map.get("girls");

        ImageView image = holder.girlContent;


        final MyProgressTarget<Drawable> myProgressTarget = new MyProgressTarget<>(mContext,new DrawableImageViewTarget(image), holder.progressBar);
        myProgressTarget.setModel(url);

        GlideApp.with(mContext).asDrawable().load(url).placeholder(R.mipmap.icon).into(myProgressTarget);
        holder.against.setText(map.get("against"));
        holder.support.setText(map.get("support"));
        holder.girlContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PictureHandle.actionStart(mContext,url);
            }
        });

    }


    @Override
    public int getAdapterItemViewType(int position) {
        return 0;
    }

    @Override
    public int getAdapterItemCount() {
        return girlList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView userName,number,time,against,support;
        ImageView girlContent;
        ProgressBar progressBar;

        public ViewHolder(View view,boolean isItem){
            super(view);
            if (isItem) {
                userName = view.findViewById(R.id.user_name);
                number = view.findViewById(R.id.publish_number);
                time = view.findViewById(R.id.publish_time);
                girlContent = view.findViewById(R.id.girl_content);
                support = view.findViewById(R.id.support);
                against = view.findViewById(R.id.against);
                progressBar = view.findViewById(R.id.progressBar);
            }

        }

    }

}
