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

public class CommentsActivity extends AppCompatActivity {

    private String url;
    private List<Map<String, Object>> list = new ArrayList<>();
    private ListView hotCommentList;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent i = getIntent();
        url = i.getStringExtra("url");

        hotCommentList = (ListView) findViewById(R.id.hot_comments_list);

        switchOver();

    }

    public static void actionStart(Context context,String url){
        Intent i = new Intent(context,CommentsActivity.class);
        i.putExtra("url",url);
        context.startActivity(i);
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

            Elements elements = doc.select("ol li");

            for(Element element : elements){

                String userName = element.getElementsByClass("author").select("strong").text();
                String time = element.getElementsByClass("author").select("small").text();
                String content = element.getElementsByClass("text").select("p").text();
                String[] vote = element.getElementsByClass("jandan-vote").select("span span").text().split(" ");
                String support = vote[0];
                String against = vote[1];
                System.out.println(userName + "\n" + time +"\n"+content+"\n"+"----------");
                Map<String, Object> map = new HashMap<>();
                map.put("userName", userName);
                map.put("time", time);
                map.put("content", content);
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
            show();


        }
    };


    // 将数据填充到ListView中
    private void show() {
        if(list.isEmpty()) {
            TextView message = (TextView) findViewById(R.id.message);
            message.setText(R.string.message);

        } else {
            SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.comments_double_list_item,
                    new String[]{"userName", "time","content","support","against"},
                    new int[]{R.id.user_name, R.id.publish_time, R.id.content,R.id.support,R.id.against});
            hotCommentList.setAdapter(adapter);
        }
        dialog.dismiss();  // 关闭窗口
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
