package xyz.wendyltanpcy.jandancomment.TabFragment;


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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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

import xyz.wendyltanpcy.jandancomment.NewsActivity;
import xyz.wendyltanpcy.jandancomment.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment implements View.OnClickListener {


    private ListView infoListView;
    private List<Map<String, Object>> list = new ArrayList<>();
    private String url = "http://jandan.net/page/1";
    private ProgressDialog dialog;
    private TextView mRefresh;

    public NewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab3, container, false);
        mRefresh = v.findViewById(R.id.refresh);

        mRefresh.setOnClickListener(this);

        infoListView = v.findViewById(R.id.info_list_view);



        infoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView v = (ListView) parent;
                HashMap<String, String> map = (HashMap<String, String>) v.getItemAtPosition(position);
                String url = map.get("url");
                NewsActivity.actionStart(getContext(),url);

            }
        });

        switchOver();

        return v;
    }


    // 将数据填充到ListView中
    private void show() {
        if (list.isEmpty()) {
            TextView message = getActivity().findViewById(R.id.message);
            message.setText(R.string.message);

        } else {
            SimpleAdapter adapter = new SimpleAdapter(getActivity(), list, R.layout.news_list_item,
                    new String[]{"title", "agree", "author", "content","url"},
                    new int[]{R.id.news_title, R.id.agree_num, R.id.author, R.id.content,R.id.url});
            infoListView.setAdapter(adapter);
        }
        dialog.dismiss();  // 关闭窗口
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


            // 获取tbody元素下的所有td元素
            Elements elements = doc.select("#body #content .list-post");
            for (Element element : elements) {
                String title = element.getElementsByClass("indexs").select("h2").text();
                String comment_num = element.getElementsByClass("comment-link").text();
                String author = element.getElementsByClass("time_s").select("a").text().replace(" "," · ");
                String agreeString = element.getElementsByClass("indexs").select("a span").text();
                String agree = "赞同: "+ agreeString.substring(1);
                String content = element.getElementsByClass("indexs").text();
                String url = element.getElementsByTag("a").attr("href");

                /*
                deal with content text
                 */
                content = content.replace(title," ");
                content = content.replace(comment_num,"");
                content = content.replace(" "+author," ");
                content = content.replace(agreeString," ");
                content = content.replace("⊙"," ");


                Map<String, Object> map = new HashMap<>();
                map.put("title",title);
                map.put("content",content);
                map.put("author",author);
                map.put("agree",agree);
                map.put("url",url);
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
            show();
        }
    };



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
    public void switchOver() {
        if (isNetworkAvailable(getActivity())) {
            // 显示“正在加载”窗口
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("正在抓取数据...");
            dialog.setCancelable(false);
            dialog.show();
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
                            switchOver();
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
    public void refresh() {
        if (isNetworkAvailable(getActivity())) {
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
                    .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            refresh();
                        }
                    }).setNegativeButton("退出", new DialogInterface.OnClickListener() {
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
            case R.id.refresh:
                refresh();
                break;
            default:
                break;
        }
    }
}
