package pe.smarttech.covid19;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuActivity extends AppCompatActivity {
    Button mapa;
    Button noticias;
    Button estadisticas;
    Button sintomas;
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mapa = findViewById(R.id.mapa);
        noticias = findViewById(R.id.noticias);
        estadisticas = findViewById(R.id.estadisticas);
        sintomas = findViewById(R.id.sintomas);
        checkPermissions();
        mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this,HereActivity.class);
                startActivity(intent);
            }
        });
        noticias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = "ct.txt";
                String string= "http://nocovid.org.pe/webviews/noticias.php";
                FileOutputStream outputStream;
                try{
                    outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                    outputStream.write(string.getBytes());
                    outputStream.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
                Intent intent = new Intent(MenuActivity.this,WebViewActivity.class);
                startActivity(intent);
            }
        });

        estadisticas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = "ct.txt";
                String string= "http://nocovid.org.pe/webviews/estadisticas.php";
                FileOutputStream outputStream;
                try{
                    outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                    outputStream.write(string.getBytes());
                    outputStream.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
                Intent intent = new Intent(MenuActivity.this,WebViewActivity.class);
                startActivity(intent);
            }
        });
        sintomas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = "ct.txt";
                String string= "http://nocovid.org.pe/webviews/sintomas-recomendaciones.php";
                FileOutputStream outputStream;
                try{
                    outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                    outputStream.write(string.getBytes());
                    outputStream.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
                Intent intent = new Intent(MenuActivity.this,WebViewActivity.class);
                startActivity(intent);
            }
        });
    }
    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

}
