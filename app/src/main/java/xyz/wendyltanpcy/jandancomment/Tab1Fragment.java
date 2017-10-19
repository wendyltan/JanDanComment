package xyz.wendyltanpcy.jandancomment;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.andview.refreshview.XRefreshView;
import com.lqr.recyclerview.LQRRecyclerView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.wendyltanpcy.jandancomment.adapter.SimpleAdapter;
import xyz.wendyltanpcy.jandancomment.model.PageInfo;


/**
 * A simple {@link Fragment} subclass.
 */
public class Tab1Fragment extends Fragment implements View.OnClickListener {

//    private ListView infoListView;
    private LQRRecyclerView mRecyclerView;
    private List<Map<String, String>> list = new ArrayList<>();
    private String url_first_half = "http://jandan.net/duan/page-";
    private String url_second_half = "#comments";
    private String next_page_url = "";
    private int currentPage;
    private static int CURRENT_NEWEST = 2850;
    private String url;
    private ProgressDialog dialog;
    private PageInfo mPageInfo;
    private TextView mRefresh,mPageNum,jump;
    private XRefreshView xRefreshView;
    private boolean nextPageExist = false;
    private EditText jumpEdit;




    public Tab1Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab1, container, false);

        mRefresh = v.findViewById(R.id.refresh);
        mPageNum = v.findViewById(R.id.pageNumber);
        jumpEdit = v.findViewById(R.id.editPageNum);
        jumpEdit.clearFocus();
        jumpEdit.setSelectAllOnFocus(false);
        jump = v.findViewById(R.id.jump);
        mRecyclerView = v.findViewById(R.id.rv);
        xRefreshView = v.findViewById(R.id.xrefreshview);
        mRefresh.setOnClickListener(this);
        jump.setOnClickListener(this);

        initPageInfo();

        initXRefreshView();

        //抓取初次信息
        switchOver(currentPage);

        return v;
    }

    /**
     * 对首页信息页数记录的初始化
     */
    public void initPageInfo(){
        LitePal.getDatabase();
        mPageInfo = DataSupport.find(PageInfo.class,1);
        if (mPageInfo == null){
            PageInfo info = new PageInfo();
            info.setLatestPageNum(CURRENT_NEWEST);
            currentPage = CURRENT_NEWEST;
            info.save();
        }else {
            PageInfo info = DataSupport.find(PageInfo.class,1);
            currentPage = info.getLatestPageNum();
        }
    }

    /**
     * 对XRefreshView的初始化
     */

    public void initXRefreshView(){
        xRefreshView.setSilenceLoadMore(true);
        xRefreshView.setPinnedTime(1000);
        xRefreshView.setMoveForHorizontal(true);
        xRefreshView.setPullLoadEnable(true);
        xRefreshView.setPullRefreshEnable(true);
        xRefreshView.enableReleaseToLoadMore(true);
        xRefreshView.enableRecyclerViewPullUp(true);
        xRefreshView.enablePullUpWhenLoadCompleted(true);



        xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {

            @Override
            public void onRefresh(boolean isPullDown) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //如果第一页满了，而且下一页有东西
                        if(judgeIfFirstPage()&&testGetNextPage()){
                            ++currentPage;
                            switchOver(currentPage);
                            xRefreshView.stopRefresh();
                        }else{
                            //没满，加载下一页
                            xRefreshView.stopRefresh();
                        }


                    }
                }, 2000);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        --currentPage;
                        switchOver(currentPage);
                        // 刷新完成必须调用此方法停止加载
                        xRefreshView.stopLoadMore();

                    }
                }, 2000);
            }
        });
    }

    /**
     * 测试下一页是否真的有东西，没有则返回false
     * @return
     */
    public boolean testGetNextPage(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                int page = currentPage+1;
                String url = url_first_half + page + url_second_half;
                Connection conn = Jsoup.connect(url);
                Document doc = null;
                try {
                    doc = conn.get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (doc == null){
                    //this is an invalid page
                    nextPageExist = false;
                }else{
                    //feel free to get next page
                    nextPageExist = true;
                }

            }
        }).start();
        return nextPageExist;

    }


    private void show() {
        if(list.isEmpty()) {
            TextView message = getActivity().findViewById(R.id.message);
            message.setText(R.string.message);

        }else{
            SimpleAdapter adapter = new SimpleAdapter(list);
            mRecyclerView.setAdapter(adapter);
        }
        dialog.dismiss();
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            Connection conn = Jsoup.connect(url);
            // 修改http包中的header,伪装成浏览器进行抓取
            conn.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/    20100101 Firefox/32.0");
            Document doc = null;
            try {
                doc = conn.get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 获取下一页的链接

            next_page_url = url_first_half + currentPage + url_second_half;

            // 获取tbody元素下的所有td元素
            Elements elements = doc.select("ol li");
            for(Element element : elements) {
                String userName = element.getElementsByClass("author").select("strong").text();
                String publishTime = element.getElementsByClass("author").select("small").select("a").text().substring(1);
                String content = element.getElementsByClass("text").select("p").text();
                String righttext = element.getElementsByClass("text").select("a").text();
                String[] vote = element.getElementsByClass("jandan-vote").select("span span").text().split(" ");
                String support = vote[0];
                String against = vote[1];
                Map<String, String> map = new HashMap<>();
                map.put("userName", userName);
                map.put("time", publishTime);
                map.put("content", content);
                map.put("number",righttext);
                map.put("support",support);
                map.put("against",against);
                list.add(map);


            }

            // 执行完毕后给handler发送一个空消息
            handler.sendEmptyMessage(0);
        }
    };


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 收到消息后执行handler
            switch (msg.what) {
                case 0://在Fragment中刷新Fragment
                    setTextStr(String.valueOf(currentPage));
                    break;
            }
            show();
        }
    };

    public void setTextStr(String str){
        mPageNum.setText("当前: "+str);
    }


    // 判断是否有可用的网络连接
    public boolean isNetworkAvailable(Activity activity)
    {
        Context context = activity.getApplicationContext();
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
            return false;
        else
        {   // 获取所有NetworkInfo对象
            NetworkInfo[] networkInfo = cm.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0)
            {
                for (int i = 0; i < networkInfo.length; i++)
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;  // 存在可用的网络连接
            }
        }
        return false;
    }


    // 重新抓取
    public void switchOver(final int page) {
        if(isNetworkAvailable(getActivity())) {
            // 显示“正在加载”窗口
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("正在抓取数据...");
            dialog.setCancelable(false);
            dialog.show();
            url = url_first_half + page + url_second_half;
            list.clear();
            new Thread(runnable).start();  // 子线程

        } else {
            // 弹出提示框
            new AlertDialog.Builder(getActivity())
                    .setTitle("提示")
                    .setMessage("当前没有网络连接！")
                    .setPositiveButton("重试",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switchOver(page);
                        }
                    }).setNegativeButton("退出",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);  // 退出程序
                }
            }).show();
        }
    }


    // 刷新
    public  void refresh() {
        if(isNetworkAvailable(getActivity())) {
            // 显示“正在刷新”窗口
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("正在刷新...");
            dialog.setCancelable(false);
            dialog.show();
            // 重新抓取
            list.clear();
            new Thread(runnable).start();  // 子线程
        } else {
            // 弹出提示框
            new AlertDialog.Builder(getActivity())
                    .setTitle("刷新")
                    .setMessage("当前没有网络连接！")
                    .setPositiveButton("重试",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            refresh();
                        }
                    }).setNegativeButton("退出",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);  // 退出程序
                }
            }).show();
        }
    }

    /**
     * 检查是否是第一页或者第一页的状态
     * @return
     */

    public boolean judgeIfFirstPage(){
        PageInfo info = DataSupport.find(PageInfo.class,1);
        if(list.size()<25) {
            Toast.makeText(getActivity(), "已经是第一页了", Toast.LENGTH_SHORT).show();
            info.setLatestPageNum(currentPage);
            info.save();
            return false;
        }
        else if(list.size()==25&&info.getLatestPageNum()==currentPage){
            Toast.makeText(getActivity(), "第一页满了,正获取下一页。。", Toast.LENGTH_SHORT).show();
            info.setLatestPageNum(currentPage);
            info.save();
            return true;
        }else{
            //未到第一页
            ++currentPage;
            switchOver(currentPage);
            return false;
        }


    }

    /**
     * jump to the specific page
     * @param pageNum
     */

    public void jumpToThePage(int pageNum){
        if (pageNum>=1&&pageNum<=currentPage){
            //valid page number;
            currentPage = pageNum;
            switchOver(pageNum);
        }else if (pageNum<0){
            Toast.makeText(getActivity(),"Invalid Page Num!",Toast.LENGTH_SHORT).show();
        }
    }



    // 上一页
    public void prePage() {
        if(isNetworkAvailable(getActivity())) {
            judgeIfFirstPage();
        } else {
            // 弹出提示框
            new AlertDialog.Builder(getActivity())
                    .setTitle("上一页")
                    .setMessage("当前没有网络连接！")
                    .setPositiveButton("重试",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            prePage();
                        }
                    }).setNegativeButton("退出",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);  // 退出程序
                }
            }).show();
        }
    }

    // 下一页
    public void nextPage() {
        if(isNetworkAvailable(getActivity())) {
            if(next_page_url.equals("#"))
                Toast.makeText(getActivity(), "已经是最后一页了", Toast.LENGTH_SHORT).show();
            else {
                --currentPage;
                switchOver(currentPage);
            }
        } else {
            // 弹出提示框
            new AlertDialog.Builder(getActivity())
                    .setTitle("下一页")
                    .setMessage("当前没有网络连接！")
                    .setPositiveButton("重试",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            nextPage();
                        }
                    }).setNegativeButton("退出",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);  // 退出程序
                }
            }).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.prev:
                prePage();
                break;
            case R.id.refresh:
                refresh();
                break;
            case R.id.next:
                nextPage();
                break;
            case R.id.jump:
                jumpToThePage(Integer.valueOf(jumpEdit.getText().toString()));
                break;
            default:
                break;
        }
    }
}
