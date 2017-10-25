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
import android.widget.TextView;
import android.widget.Toast;

import com.lqr.recyclerview.LQRRecyclerView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.wendyltanpcy.jandancomment.adapter.GirlListAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class GirlsFragment extends Fragment implements View.OnClickListener {

    private LQRRecyclerView mRecyclerView;
    private List<Map<String, String>> list = new ArrayList<>();
    private String url_first_half = "http://jandan.net/ooxx/page-";
    private String url_second_half = "#comments";
    private String next_page_url = "";
    private int currentPage;
    private String url;
    private ProgressDialog dialog;
    private TextView mPrev, mRefresh, mNext, mPageNum;
    private  boolean isNextPageExists = false;
    private String current;


    public GirlsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab2, container, false);
        mPrev = v.findViewById(R.id.prev);
        mRefresh = v.findViewById(R.id.refresh);
        mNext = v.findViewById(R.id.next);
        mPageNum = v.findViewById(R.id.pageNumber);


        mPrev.setOnClickListener(this);
        mRefresh.setOnClickListener(this);
        mNext.setOnClickListener(this);



        mRecyclerView = v.findViewById(R.id.rv);
        currentPage = 0;

        switchOver(currentPage);

        return v;
    }



    /**
     * 检查是否是第一页或者第一页的状态
     * @return
     */

    public boolean judgeIfFirstPage(){
        if(list.size()<25&&currentPage>0) {
            Toast.makeText(getActivity(), "已经是第一页了", Toast.LENGTH_SHORT).show();

            return false;
        }
        else if(list.size()==25){
            Toast.makeText(getActivity(), "第一页满了", Toast.LENGTH_SHORT).show();
            //下一页没东西了，才是第一页

            return true;
        }else{
            //未到第一页
            ++currentPage;
            switchOver(currentPage);
            return false;
        }


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
                    isNextPageExists = false;
                }else{
                    //feel free to get next page
                    isNextPageExists  = true;
                }

            }
        }).start();
        return isNextPageExists;
    }



    // 将数据填充到ListView中
    private void show() {
        if (list.isEmpty()) {
            TextView message = getActivity().findViewById(R.id.message);
            message.setText(R.string.message);

        } else{
            GirlListAdapter adapter = new GirlListAdapter(list);
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

            //transform currentpage num from 0 to specific number
            if (currentPage==0){
                Elements t = doc.select("div div span");
                String[] hi = new String[1];
                for (Element e : t){
                    if (e.getElementsByClass("current-comment-page")!=null){
                        current = e.getElementsByClass("current-comment-page").text();
                        String [] hello = current.split(" ");
                        for (String str:hello){
                            if (!str.isEmpty()){
                                hi[0] = str;
                            }
                        }
                    }
                };
                currentPage = Integer.parseInt(hi[0].replace("[","").replace("]",""));
            }

            Elements elements = doc.select("ol li");
            for (Element element : elements) {
                String userName = element.getElementsByClass("author").select("strong").text();
                String publishTime = element.getElementsByClass("author").select("small").select("a").text().substring(1);
                String content = element.getElementsByClass("text").select("p a").attr("href");
                String righttext = element.getElementsByClass("text").select("a").text().replace("[查看原图]","");
                String[] vote = element.getElementsByClass("jandan-vote").select("span span").text().split(" ");
                String support = vote[0];
                String against = vote[1];
                Map<String, String> map = new HashMap<>();
                map.put("userName", userName);
                map.put("time", publishTime);
                map.put("girls","http:"+content);
                map.put("number", righttext);
                map.put("support", support);
                map.put("against", against);
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

    public void setTextStr(String str) {
        mPageNum.setText("当前: " + str);
    }


    // 判断是否有可用的网络连接
    public boolean isNetworkAvailable(Activity activity) {
        Context context = activity.getApplicationContext();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
            return false;
        else {   // 获取所有NetworkInfo对象
            NetworkInfo[] networkInfo = cm.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++)
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;  // 存在可用的网络连接
            }
        }
        return false;
    }


    // 重新抓取
    public void switchOver(final int page) {
        if (isNetworkAvailable(getActivity())) {
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
                    .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switchOver(page);
                        }
                    }).setNegativeButton("退出", new DialogInterface.OnClickListener() {
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



    // 上一页
    public void prePage() {
        if(isNetworkAvailable(getActivity())) {
            if (testGetNextPage()&&judgeIfFirstPage()){
                ++currentPage;
                switchOver(currentPage);
            }
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
        switch (view.getId()) {
            case R.id.prev:
                prePage();
                break;
            case R.id.refresh:
                refresh();
                break;
            case R.id.next:
                nextPage();
                break;
            default:
                break;
        }
    }
}