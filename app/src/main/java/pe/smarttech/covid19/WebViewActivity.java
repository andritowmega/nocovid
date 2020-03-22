package pe.smarttech.covid19;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class WebViewActivity extends AppCompatActivity {
    WebView web;
    String data = "";
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
        web.setWebViewClient(new WebViewClient());
        web.getSettings().setJavaScriptEnabled(true);
        web.loadUrl(data);

    }
}
