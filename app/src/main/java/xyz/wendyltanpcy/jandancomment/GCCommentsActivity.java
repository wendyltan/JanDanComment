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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xyz.wendyltanpcy.jandancomment.adapter.DuanziCommentsAdapter;
import xyz.wendyltanpcy.jandancomment.helper.SerializableMap;

/**
 * Created by Wendy on 2017/10/26.
 */

public class GCCommentsActivity extends AppCompatActivity {

    //需要点击段子之后段子传过来的一切参数。然后抓取相应的评论区！
    private SerializableMap mMap;
    private List<Map<String, String>> list = new ArrayList<>();
    private RecyclerView duanziCommentList;
    private ProgressDialog dialog;
    private TextView userName,number,time,content,against,support,tucao;
    private OkHttpClient okHttpClient;

    //得到评论的id
    private String commentNum;
    //获得请求此id返回的json初始内容
    private String responseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gc_comments);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Duanzi Comments");
        }

        userName = (TextView) findViewById(R.id.user_name);
        number = (TextView) findViewById(R.id.publish_number);
        time  = (TextView) findViewById(R.id.publish_time);
        content = (TextView) findViewById(R.id.content);
        against = (TextView) findViewById(R.id.against);
        support = (TextView) findViewById(R.id.support);
        tucao = (TextView) findViewById(R.id.tucao);



        Intent i = getIntent();

        mMap = (SerializableMap) i.getSerializableExtra("map");
        Map<String,String> map = mMap.getMap();

        userName.setText(map.get("userName"));
        number.setText(map.get("number"));
        time.setText(map.get("time"));
        content.setText(map.get("content"));
        against.setText(map.get("against"));
        support.setText(map.get("support"));
        tucao.setText(map.get("tucao"));


        commentNum = map.get("number");

        duanziCommentList = (RecyclerView) findViewById(R.id.gc_comments_list);


        switchOver();
    }


    public static void actionStart(Context context, SerializableMap map){
        Intent i = new Intent(context,GCCommentsActivity.class);
        i.putExtra("map",map);
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

            okHttpClient=new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://jandan.net/tucao/" + commentNum)
                    .addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/    20100101 Firefox/32.0")
                    .build();
            try {
                Response response = okHttpClient.newCall(request).execute();
                responseData = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //获得评论。因为评论是js动态加载，不能使用jsoup。但是这里我直接通过发送网页请求换来json数据读取即可

            JSONObject jsoninit;
            try {
                jsoninit = new JSONObject(responseData);
                JSONArray content = (JSONArray) jsoninit.get("tucao");
                for (int i=0;i<content.length();i++){
                    JSONObject tucao = content.getJSONObject(i);
                    Map<String,String> map = new HashMap<>();

                    String tucao_author = tucao.getString("comment_author");
                    String regex = "<[^>]*>";
                    String tucao_content = tucao.getString("comment_content").replaceAll(regex,"");
                    String tucao_date = tucao.getString("comment_date");
                    String tucao_support = tucao.getString("vote_positive");
                    String tucao_against = tucao.getString("vote_negative");

                    map.put("tucaoAuthor",tucao_author);
                    map.put("tucaoDate",tucao_date);
                    map.put("tucaoContent",tucao_content);
                    map.put("tucaoSupport",tucao_support);
                    map.put("tucaoAgainst",tucao_against);
                    list.add(map);


                }
            } catch (JSONException e) {
                e.printStackTrace();
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

        }else{
            DuanziCommentsAdapter adapter = new DuanziCommentsAdapter(list);
            duanziCommentList.setAdapter(adapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            duanziCommentList.setLayoutManager(layoutManager);
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
