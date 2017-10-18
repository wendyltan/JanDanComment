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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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

import xyz.wendyltanpcy.jandancomment.model.PageInfo;


/**
 * A simple {@link Fragment} subclass.
 */
public class Tab4Fragment extends Fragment implements View.OnClickListener{


    public Tab4Fragment() {
        // Required empty public constructor
    }

    private ListView infoListView;
    private List<Map<String, Object>> list = new ArrayList<>();
    private String url_first_half = "http://jandan.net/pic/page-";
    private String url_second_half = "#comments";
    private String next_page_url = "";
    private int currentPage;
    private static int CURRENT_NEWEST = 164;
    private String url;
    private ProgressDialog dialog;
    private PageInfo mPageInfo;
    private TextView mPrev, mRefresh, mNext, mPageNum;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab4, container, false);
        mPrev = v.findViewById(R.id.prev);
        mRefresh = v.findViewById(R.id.refresh);
        mNext = v.findViewById(R.id.next);
        mPageNum = v.findViewById(R.id.pageNumber);

        mPrev.setOnClickListener(this);
        mRefresh.setOnClickListener(this);
        mNext.setOnClickListener(this);

        LitePal.getDatabase();
        mPageInfo = DataSupport.find(PageInfo.class,3);
        if (mPageInfo == null) {
            PageInfo info = new PageInfo();
            info.setLatestPageNum(CURRENT_NEWEST);
            currentPage = CURRENT_NEWEST;
            info.save();
        } else {
            currentPage = mPageInfo.getLatestPageNum();
        }

        infoListView = v.findViewById(R.id.info_list_view);
        switchOver(currentPage);


        return v;
    }


    // 将数据填充到ListView中
    private void show() {
        if (list.isEmpty()) {
            TextView message = getActivity().findViewById(R.id.message);
            message.setText(R.string.message);

        } else {
            SimpleAdapter adapter = new SimpleAdapter(getActivity(), list, R.layout.boring_list_item,
                    new String[]{"userName", "time", "number","boring", "support", "against"},
                    new int[]{R.id.user_name, R.id.publish_time, R.id.publish_number,R.id.boring_content, R.id.support, R.id.against});
            adapter.setViewBinder(new Tab4Fragment.MyViewBinder());
            infoListView.setAdapter(adapter);
            infoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ListView v = (ListView) parent;
                    HashMap<String, String> map = (HashMap<String, String>) v.getItemAtPosition(position);
                    String url = map.get("boring");
                    PictureHandle.actionStart(getContext(),url);
                }
            });
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

            // 获取下一页的链接

            next_page_url = url_first_half + currentPage + url_second_half;

            // 获取tbody元素下的所有td元素
            Elements elements = doc.select("ol li");
            for (Element element : elements) {
                String userName = element.getElementsByClass("author").select("strong").text();
                String publishTime = element.getElementsByClass("author").select("small").select("a").text();
                String content = element.getElementsByClass("text").select("p a").attr("href");
                String righttext = element.getElementsByClass("text").select("a").text();
                String[] vote = element.getElementsByClass("jandan-vote").select("span span").text().split(" ");
                String support = vote[0];
                //这里有一个奇妙的bug
                String against = vote[0];
                Map<String, Object> map = new HashMap<>();
                map.put("userName", userName);
                map.put("time", publishTime);
                map.put("boring","http:"+content);
                map.put("number", righttext);
                map.put("support", support);
                map.put("against", against);
                list.add(map);

            }

            // 执行完毕后给handler发送一个空消息
            handler.sendEmptyMessage(0);
        }
    };

    class MyViewBinder implements SimpleAdapter.ViewBinder
    {
        @Override
        public boolean setViewValue(View view, Object data,
                                    String textRepresentation) {
            if((view instanceof ImageView)&(data instanceof String))
            {
                ImageView iv = (ImageView)view;

                //通过url动态加载图片
                Picasso.with(getContext())
                        .load((String) data)
                        .placeholder(R.mipmap.icon)
                        .into(iv);
                return true;
            }
            return false;
        }


    }



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
            if(list.size()<25) {
                Toast.makeText(getActivity(), "已经是第一页了", Toast.LENGTH_SHORT).show();
                mPageInfo.setLatestPageNum(currentPage);
                mPageInfo.save();
            }
            else if(list.size()==25&&mPageInfo.getLatestPageNum()==currentPage){
                Toast.makeText(getActivity(), "第一页满了", Toast.LENGTH_SHORT).show();
                ++currentPage;
                switchOver(currentPage);
            }
            else {
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

