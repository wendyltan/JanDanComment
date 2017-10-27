package xyz.wendyltanpcy.jandancomment.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.recyclerview.BaseRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import xyz.wendyltanpcy.jandancomment.GCCommentsActivity;
import xyz.wendyltanpcy.jandancomment.R;
import xyz.wendyltanpcy.jandancomment.helper.SerializableMap;

/**
 * Created by Wendy on 2017/10/18.
 */

public class DuanziListAdapter extends BaseRecyclerAdapter<DuanziListAdapter.ViewHolder> {

    private List<SerializableMap> commentList = new ArrayList<>();
    private Context mContext;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        mContext = parent.getContext();
        View v = LayoutInflater.from(mContext).inflate(
                R.layout.duanzi_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v, true);
        return vh;
    }

    @Override
    public ViewHolder getViewHolder(View view) {
        return new ViewHolder(view,true);
    }

    public DuanziListAdapter(List<SerializableMap> list){
        commentList = list;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position, boolean isItem) {
        final SerializableMap Maph =  commentList.get(position);
        Map<String,String> map = Maph.getMap();
        holder.userName.setText(map.get("userName"));
        holder.number.setText(map.get("number"));
        holder.time.setText(map.get("time"));
        holder.content.setText(map.get("content"));
        holder.against.setText(map.get("against"));
        holder.support.setText(map.get("support"));
        holder.tucao.setText(map.get("tucao"));
        holder.tucao_prefix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GCCommentsActivity.actionStart(mContext,Maph);
            }
        });

        holder.contextMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.showContextMenu();
            }
        });

    }



    @Override
    public int getAdapterItemViewType(int position) {
        return 0;
    }

    @Override
    public int getAdapterItemCount() {
        return commentList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{

        TextView userName,number,time,content,against,support,tucao,tucao_prefix;
        ImageView contextMenuButton;

        public ViewHolder(View view,boolean isItem){
            super(view);
            if (isItem) {
                userName = view.findViewById(R.id.user_name);
                number = view.findViewById(R.id.publish_number);
                time = view.findViewById(R.id.publish_time);
                content = view.findViewById(R.id.content);
                support = view.findViewById(R.id.support);
                against = view.findViewById(R.id.against);
                tucao = view.findViewById(R.id.tucao);
                tucao_prefix = view.findViewById(R.id.tucao_prefix);
                contextMenuButton = view.findViewById(R.id.duanzi_contextMenu);
            }

            view.setOnCreateContextMenuListener(this);


        }
        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            //menuInfo is null
            menu.add(0, 1, getAdapterPosition(), "复制段子");
            menu.add(0, 2, getAdapterPosition(), "分享");
        }

    }

}
