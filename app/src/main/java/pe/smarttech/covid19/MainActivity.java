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
import android.widget.Toast;

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
    String tokendata="";
    String URLbase = "http://nocovid.org.pe";
    String response = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pbloading = findViewById(R.id.progress);
        title = findViewById(R.id.title);
        try
        {
            BufferedReader fin =
                    new BufferedReader(
                            new InputStreamReader(
                                    openFileInput("ctoken.txt")));
            tokendata = fin.readLine();
            fin.close();
        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), "ERROR AL LEER data" + ex.toString(), Toast.LENGTH_SHORT).show();
        }
        new CheckInfo().execute();
    }
    private class CheckInfo extends AsyncTask<Void, Integer, Boolean> {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Boolean doInBackground(Void... params) {

            publishProgress(50);
            HiloTask();
                if(tokendata==null || tokendata.equals("")){
                    Log.i("Token", "vacio");
                    return false;
                }
                else{
                    Log.i("Token", "no es null");
                }
            String urlParameters  = "token="+tokendata;
            Log.i("Parametros", urlParameters);
            byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
            int    postDataLength = postData.length;
            URL url;
            HttpURLConnection urlConnection = null;
            try{
                url = new URL(URLbase+"/api/login.php");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput( true );
                urlConnection.setInstanceFollowRedirects( false );
                urlConnection.setRequestMethod( "POST" );
                urlConnection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty( "charset", "utf-8");
                //urlConnection.setRequestProperty( "x-access-token", data.getString("token"));
                urlConnection.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
                urlConnection.setUseCaches( false );
                DataOutputStream wr = new DataOutputStream( urlConnection.getOutputStream());
                wr.write( postData );
                int responseCode = urlConnection.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK){
                    publishProgress(100);
                    response = readStream(urlConnection.getInputStream());
                    return true;
                }
                else{
                    Log.d("ERROR ","nO HAY RESPUESTA "+responseCode);
                    return false;
                }
            } catch (MalformedURLException e){
                    Log.d("ERROR ",e.toString());
                    return false;
                } catch (IOException e){
                    Log.d("ERROR ",e.toString());
                    return false;
                }


        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            int progreso = values[0].intValue();
            pbloading.setProgress(progreso);
        }
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected void onPostExecute(Boolean result) {

            Log.d("Response ",response);
            if(result){
                if(tokendata==null ||tokendata.equals("")){
                    Toast toast = Toast.makeText(getApplicationContext(),"Inicie Sesión",Toast.LENGTH_LONG);
                    toast.show();
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
                else if(response.equals("vacio")){
                    Toast toast = Toast.makeText(getApplicationContext(),"Error de Token",Toast.LENGTH_LONG);
                    toast.show();
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
                else if(response.equals("error")){
                    Toast toast = Toast.makeText(getApplicationContext(),"Estamos en Mantenimiento",Toast.LENGTH_LONG);
                    toast.show();
                }
                else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Acceso Permitido, Bienvenido de Nuevo",Toast.LENGTH_LONG);
                    toast.show();
                    Intent intent = new Intent(MainActivity.this,MenuActivity.class);
                    startActivity(intent);

                }
            }
            else{
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        }
        @Override
        protected void onCancelled() {

        }
    }
    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
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
    private class CheckToken extends AsyncTask<Void, Integer, Boolean> {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Boolean doInBackground(Void... params) {
            if(tokendata != null || !tokendata.equals("")){
                JSONObject data = null;
                String urlParameters  = "token="+tokendata;
                Log.i("Parametros", urlParameters);
                byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
                int    postDataLength = postData.length;
                URL url;
                HttpURLConnection urlConnection = null;
                try{
                    url = new URL(URLbase+"/api/loginbytoken.php");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoOutput( true );
                    urlConnection.setInstanceFollowRedirects( false );
                    urlConnection.setRequestMethod( "POST" );
                    urlConnection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
                    urlConnection.setRequestProperty( "charset", "utf-8");
                    //urlConnection.setRequestProperty( "x-access-token", data.getString("token"));
                    urlConnection.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
                    urlConnection.setUseCaches( false );
                    DataOutputStream wr = new DataOutputStream( urlConnection.getOutputStream());
                    wr.write( postData );
                    int responseCode = urlConnection.getResponseCode();
                    if(responseCode == HttpURLConnection.HTTP_OK){
                        publishProgress(100);
                        response = readStream(urlConnection.getInputStream());
                        return true;
                    }
                    else{
                        Log.d("ERROR ","nO HAY RESPUESTA "+responseCode);
                        return false;
                    }
                } catch (MalformedURLException e){
                    Log.d("ERROR ",e.toString());
                    return false;
                } catch (IOException e){
                    Log.d("ERROR ",e.toString());
                    return false;
                }
            }
            else return true;

        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            int progreso = values[0].intValue();
            if(progreso==100){ }
        }
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected void onPostExecute(Boolean result) {
            Log.d("Response ",response);
            if(result){

            }
            else{

            }
        }
        @Override
        protected void onCancelled() {

        }
    }
}
