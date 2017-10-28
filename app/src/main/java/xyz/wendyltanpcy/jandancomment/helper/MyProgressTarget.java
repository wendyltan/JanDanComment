package xyz.wendyltanpcy.jandancomment.helper;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.request.target.Target;

/**
 * Created by Wendy on 2017/10/28.
 */

public class MyProgressTarget<Z> extends ProgressTarget<String, Z> {
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
