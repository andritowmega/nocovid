package pe.smarttech.covid19;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class WebViewActivity extends AppCompatActivity {
    WebView web;
    String data = "";
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        web = findViewById(R.id.web);
        try
        {
            BufferedReader fin =
                    new BufferedReader(
                            new InputStreamReader(
                                    openFileInput("ct.txt")));
            data = fin.readLine();
            fin.close();
        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), "ERROR AL LEER data" + ex.toString(), Toast.LENGTH_SHORT).show();
        }
        try
        {
            BufferedReader fin =
                    new BufferedReader(
                            new InputStreamReader(
                                    openFileInput("ctoken.txt")));
            String token = fin.readLine();
            Log.d("Token",token);
            fin.close();
        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), "ERROR AL LEER data" + ex.toString(), Toast.LENGTH_SHORT).show();
        }
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        //mAdView = findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder().build();
        //mAdView.loadAd(adRequest);
        web.setWebViewClient(new WebViewClient());
        web.getSettings().setJavaScriptEnabled(true);
        web.loadUrl(data);

    }
}
