package xyz.wendyltanpcy.jandancomment.helper;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import xyz.wendyltanpcy.jandancomment.R;

/**
 * Created by Wendy on 2017/10/18.
 */

public class PictureHandle extends AppCompatActivity{

    private String picUrl;
    private PinchImageView mImageView;
    private FloatingActionButton download;
    public static final int PERMISSION_REQUEST = 11;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_handle);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setTitle("保存图片");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mImageView = (PinchImageView) findViewById(R.id.handle_pic);
        download = (FloatingActionButton) findViewById(R.id.downloadButton);
        Intent i = getIntent();
        picUrl = i.getStringExtra("url");

        //只能用毕加索搭配pinchimage，glide不兼容
        Picasso.with(this)
                .load(picUrl)
                .placeholder(R.mipmap.icon)
                .into(mImageView);

        //点击保存照片
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }
        });




    }

    public void checkPermission(){

        if (ContextCompat.checkSelfPermission(PictureHandle.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(PictureHandle.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Toast.makeText(this,"Couldn't store pic until u get permission!",Toast.LENGTH_SHORT).show();

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(PictureHandle.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                       PERMISSION_REQUEST);

            }
        }else{
           savePicToDir();
        }
    }

    private void savePicToDir(){
        Drawable drawable = mImageView.getDrawable();
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        saveImageToGallery (PictureHandle.this,bitmap);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    savePicToDir();

                } else {
                    Toast.makeText(this,"Sorry you don't get the permission!",Toast.LENGTH_SHORT).show();

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public static void saveImageToGallery(Context context, Bitmap bitmap) {

            String  sdCardDir = Environment.getExternalStorageDirectory()+ "/JanDanImage/";
            File dirFile  = new File(sdCardDir);  //目录转化成文件夹
            if (!dirFile.exists()) {              //如果不存在，那就建立这个文件夹
                dirFile.mkdirs();
            }                          //文件夹有啦，就可以保存图片啦
            String fileName = System.currentTimeMillis()+".jpg";
            File file = new File(sdCardDir, fileName);// 在SDcard的目录下创建图片文,以当前时间为其命名
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90,out);
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                }
            // 其次把文件插入到系统图库
            try {
                MediaStore.Images.Media.insertImage(context.getContentResolver(),
                        file.getAbsolutePath(), fileName, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //最后通知图库更新
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);//扫描单个文件
            intent.setData(Uri.fromFile(file));//给图片的绝对路径
            context.sendBroadcast(intent);

            System.out.println(Environment.getExternalStorageDirectory()+"/JanDanImage/"+" 目录文件夹下");
            Toast.makeText(context,"保存已经至"+Environment.getExternalStorageDirectory()+"/JanDanImage/"+" 目录文件夹下", Toast.LENGTH_SHORT).show();

    }




    public static void actionStart(Context context,String url){
        Intent i = new Intent(context,PictureHandle.class);
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
