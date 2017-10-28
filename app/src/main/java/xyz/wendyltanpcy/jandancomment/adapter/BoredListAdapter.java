package xyz.wendyltanpcy.jandancomment.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.andview.refreshview.recyclerview.BaseRecyclerAdapter;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import xyz.wendyltanpcy.jandancomment.R;
import xyz.wendyltanpcy.jandancomment.helper.GlideApp;
import xyz.wendyltanpcy.jandancomment.helper.PictureHandle;
import xyz.wendyltanpcy.jandancomment.helper.ProgressTarget;

/**
 * Created by Wendy on 2017/10/25.
 */

public class BoredListAdapter extends BaseRecyclerAdapter<BoredListAdapter.ViewHolder> {



    /**
     * Created by Wendy on 2017/10/18.
     */

    private List<Map<String,String>> boredList = new ArrayList<>();
    private Context mContext;


    @Override
    public  BoredListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.boring_list_item, parent, false);
        mContext = parent.getContext();
        ViewHolder vh = new ViewHolder(v, true);

        return vh;
    }

    @Override
    public ViewHolder getViewHolder(View view) {
        return new ViewHolder(view,true);
    }

    public  BoredListAdapter(List<Map<String, String>> list){
        boredList = list;
    }


    @Override
    public void onBindViewHolder(final BoredListAdapter.ViewHolder holder, int position, boolean isItem) {

        Map<String, String> map = boredList.get(position);
        final String url = map.get("boring");


        holder.userName.setText(map.get("userName"));
        holder.number.setText(map.get("number"));
        holder.time.setText(map.get("time"));


        ImageView image = holder.boringContent;


        final MyProgressTarget<Bitmap> myProgressTarget = new MyProgressTarget<>(mContext,new BitmapImageViewTarget(image), holder.progressBar);
        myProgressTarget.setModel(url);

        GlideApp.with(mContext).asBitmap().load(url).centerCrop().into(myProgressTarget);

        holder.against.setText(map.get("against"));
        holder.support.setText(map.get("support"));
        holder.boringContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PictureHandle.actionStart(mContext,url);
            }
        });



    }

    static class MyProgressTarget<Z> extends ProgressTarget<String, Z> {

        private final ProgressBar progressBar;
        private String TAG = "HELLO";

        public MyProgressTarget(Context context, Target<Z> target, ProgressBar progressBar) {
            super(context,target);
            this.progressBar = progressBar;
        }

        @Override
        public float getGranualityPercentage() {
            return super.getGranualityPercentage();
        }

        @Override
        protected void onConnecting() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onDownloading(long bytesRead, long expectedLength) {
            Log.d(TAG, "onDownloading: " + (int) (100 * bytesRead / expectedLength));
            progressBar.setProgress((int) (100 * bytesRead / expectedLength));
        }

        @Override
        protected void onDownloaded() {
            Log.e("zzzz", "onDownloaded");
        }

        @Override
        protected void onDelivered() {
            progressBar.setProgress(100);
            progressBar.setVisibility(View.GONE);
        }
    }




    @Override
    public int getAdapterItemViewType(int position) {
        return 0;
    }

    @Override
    public int getAdapterItemCount() {
        return boredList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView userName,number,time,against,support;
        ImageView boringContent;
        ProgressBar progressBar;

        public ViewHolder(View view,boolean isItem){
            super(view);
            if (isItem) {
                userName = view.findViewById(R.id.user_name);
                number = view.findViewById(R.id.publish_number);
                time = view.findViewById(R.id.publish_time);
                boringContent = view.findViewById(R.id.boring_content);
                support = view.findViewById(R.id.support);
                against = view.findViewById(R.id.against);
                progressBar = view.findViewById(R.id.progressBar);
            }

        }

    }

}

