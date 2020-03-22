package pe.smarttech.covid19;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    ProgressBar pbloading;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pbloading = findViewById(R.id.progress);
        title = findViewById(R.id.title);
        new CheckInfo().execute();
    }
    private class CheckInfo extends AsyncTask<Void, Integer, Boolean> {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Boolean doInBackground(Void... params) {
            HiloTask();
            publishProgress(50);
            try {
                Log.d("INTERNET ", "conectando");
                if(conectadoAInternet()){
                    HiloTask();
                    Log.d("INTERNET ", "OK");
                    publishProgress(100);
                    return true;
                }
                return false;
            } catch (InterruptedException e) {
                publishProgress(100);
                return false;
            } catch (IOException e) {
                publishProgress(100);
                return false;
            }
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            int progreso = values[0].intValue();
            if(progreso==100){
                title.setText("OK ...");
            }
            pbloading.setProgress(progreso);
        }
        @Override
        protected void onPreExecute() {
            pbloading.setMax(100);
            pbloading.setProgress(0);
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if(result){
                Log.d("INTERNET ", "lanzando activity");
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
            else{
                title.setText("No tienes conexión a internet");
            }
        }
        @Override
        protected void onCancelled() {

        }
    }
    public boolean conectadoAInternet()  throws InterruptedException, IOException
    {
        System.out.println("executeCommand");
        Runtime runtime = Runtime.getRuntime();
        try
        {
            Process  mIpAddrProcess = runtime.exec("/system/bin/ping -c google.com");
            int mExitValue = mIpAddrProcess.waitFor();
            System.out.println(" mExitValue "+mExitValue);
            if(mExitValue==0){
                return true;
            }else{
                return true;
            }
        }
        catch (InterruptedException ignore)
        {
            ignore.printStackTrace();
            System.out.println(" Exception:"+ignore);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println(" Exception:"+e);
        }
        return false;
    }
    private void HiloTask()
    {
        try {
            Thread.sleep(2000);
        } catch(InterruptedException e) {}
    }
}
