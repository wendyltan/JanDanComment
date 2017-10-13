package xyz.wendyltanpcy.jandancomment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Wendy on 2017/10/13.
 */

public class NewsActivity extends AppCompatActivity {

    private String url,titleText,contentText,timeAndAuth;
    private TextView newsTitle,newsTimeAuth,newsContent;
    private List<Map<String, Object>> list = new ArrayList<>();
    private ProgressDialog dialog;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("JanDanNews");
        }
        newsTitle = (TextView) findViewById(R.id.news_title_text);
        newsTimeAuth = (TextView) findViewById(R.id.news_time_and_author);
        newsContent = (TextView) findViewById(R.id.news_content_text);
        Intent i = getIntent();
        url = i.getStringExtra("url");


        switchOver();

    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            dialog.dismiss();

            Connection conn = Jsoup.connect(url);
            // 修改http包中的header,伪装成浏览器进行抓取
            conn.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/    20100101 Firefox/32.0");
            Document doc = null;
            try {
                doc = conn.get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Elements elements = doc.getElementsByClass("post");

            for(Element element : elements){
                titleText = element.getElementsByTag("h1").text();
                timeAndAuth = element.getElementsByClass("time_s").text();
                List<String> test = element.getElementsByTag("p").eachText();
                /*
                去掉头尾
                 */
                test.remove(0);
                test.remove(test.size()-1);
                StringBuilder builder = new StringBuilder();
                for(String item : test){
                    builder.append("      "+item+"\n");

                }
                contentText = builder.toString();
                if (timeAndAuth!=null&&contentText!=null&&titleText!=null)
                    break;
            }
//            System.out.println(titleText + "\n"+contentText +"\n"+ timeAndAuth);




            // 执行完毕后给handler发送一个空消息
            handler.sendEmptyMessage(0);
        }
    };



    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 收到消息后执行handler
            newsTimeAuth.setText(timeAndAuth);
            newsContent.setText(contentText);
            newsTitle.setText(titleText);

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
        if (isNetworkAvailable(this)) {
            // 显示“正在加载”窗口
            dialog = new ProgressDialog(this);
            dialog.setMessage("正在抓取数据...");
            dialog.setCancelable(false);
            dialog.show();
            new Thread(runnable).start();  // 子线程


        } else {
            // 弹出提示框
            new AlertDialog.Builder(this)
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


    public static void actionStart(Context context,String url){
        Intent i = new Intent(context,NewsActivity.class);
        i.putExtra("url",url);
        context.startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return false;
    }
}
