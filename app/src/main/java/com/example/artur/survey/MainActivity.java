package com.example.artur.survey;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.codemybrainsout.ratingdialog.RatingDialog;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences ratingPref;
    private String androidID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.setWebChromeClient(new WebChromeClient());
        androidID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.i("InstanceID", androidID);
        myWebView.loadUrl("http://free-ig-video-views.com/app/?appid="+androidID);
        ratingPref = getPreferences(Context.MODE_PRIVATE);
        float defaultValue = 0;
        final float rating = ratingPref.getFloat(getString(R.string.saved_rating), defaultValue);
        if (!(rating > 0)) {
            final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                    .playstoreUrl("https://play.google.com/store/apps/details?id=" + getPackageName())
                    .onRatingChanged(new RatingDialog.Builder.RatingDialogListener() {
                        @Override
                        public void onRatingSelected(float rating, boolean thresholdCleared) {
                            Log.i("Rating", String.valueOf(rating));
                            SharedPreferences.Editor editor = ratingPref.edit();
                            editor.putFloat(getString(R.string.saved_rating), rating);
                            editor.apply();
                            if (rating > 4) {
                                AsyncTask<String, Void, String> sendRatingTask = new HttpPostRequest()
                                        .execute("http://free-ig-videoviews.com/app/api/",
                                                androidID, String.valueOf(rating), HttpPostRequest.RATE_ACTION);
                                try {
                                    Log.i("sendRatingTask result", sendRatingTask.get());
                                } catch (InterruptedException | ExecutionException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).build();

            ratingDialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.share_menu, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.action_share);
        // Fetch and store ShareActionProvider

        // Return true to display menu
        return true;
    }

    private Intent getShareIntent() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        String shareBody = "\nLet me recommend you this application\n\n";
        shareBody = shareBody + "https://play.google.com/store/apps/details?id=" + getPackageName() + "\n\n";
        i.putExtra(Intent.EXTRA_TEXT, shareBody);
        return i;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                startActivityForResult(Intent.createChooser(getShareIntent(), "Share this app with friends!"),
                        3);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("onActivityResult", "Result code: " + String.valueOf(resultCode));
        if (resultCode == RESULT_OK) {
            AsyncTask<String, Void, String> sendShareTask = new HttpPostRequest()
                    .execute("http://free-ig-videoviews.com/app/api/",
                            androidID, "0", HttpPostRequest.SHARE_ACTION);
            try {
                Log.i("sendShareTask result", sendShareTask.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
