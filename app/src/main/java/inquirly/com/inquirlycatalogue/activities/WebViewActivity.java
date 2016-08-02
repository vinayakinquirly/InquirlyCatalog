package inquirly.com.inquirlycatalogue.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import inquirly.com.inquirlycatalogue.R;
import inquirly.com.inquirlycatalogue.utils.ApiConstants;

public class WebViewActivity extends AppCompatActivity {

    private WebView webview;
    private static final String TAG = WebViewActivity.class.getSimpleName();
    public class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        public void onPageFinished(WebView view, String url) {
            Log.i(TAG, "Finished loading URL: " + url);

        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.e(TAG, "Error: " + description);
            Toast.makeText(WebViewActivity.this, "Page Load Error" + description, Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Bundle extras = getIntent().getExtras();
        String feedbackUrl = extras.getString(ApiConstants.CAMPAIGN_FEEDBACK_LINK);

        webview = (WebView)findViewById(R.id.webview);

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setAllowContentAccess(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.getSettings().setAllowContentAccess(true);
        ///webview.getSettings().setMixedContentMode(true);
        webview.setWebViewClient(new MyWebViewClient());

        webview.loadUrl(feedbackUrl);

    }

}
