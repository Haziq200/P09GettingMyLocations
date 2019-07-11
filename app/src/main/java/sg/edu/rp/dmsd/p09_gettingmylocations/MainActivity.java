package sg.edu.rp.dmsd.p09_gettingmylocations;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class MainActivity extends AppCompatActivity {

    TextView tvLng, tvLat;
    Button btnStart, btnStop, btnCheck;
    FusedLocationProviderClient client;
    String folderLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tvLat = findViewById(R.id.tvLatitude);
        tvLng = findViewById(R.id.tvLongitude);
        btnStart = findViewById(R.id.btnStartDet);
        btnStop = findViewById(R.id.btnStopDet);
        btnCheck = findViewById(R.id.btnCheckRecords);


        client = LocationServices.getFusedLocationProviderClient(this);

        int permissionCheck = PermissionChecker.checkSelfPermission
                (MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);


        if (permissionCheck != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            return;
        } else {
            Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lng = location.getLongitude();
                        tvLat.setText("Latitude: " + lat);
                        tvLng.setText("Longtitude: " + lng);
                    } else {
                        String msg = "No Last Location Found";
                        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MyService.class);
                startService(i);

            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MyService.class);
                stopService(i);
            }
        });

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyLocation";
                                 File targetFile = new File(folderLocation, "loc.txt");
                                 if (targetFile.exists() == true) {
                                         String data = "";
                                        try {
                                                 FileReader reader = new FileReader(targetFile);
                                                 BufferedReader br = new BufferedReader(reader);
                                                 String line = br.readLine();
                                                 while (line != null) {
                                                         data += line + "\n";
                                                         line = br.readLine();
                                                    }
                                                br.close();
                                                 reader.close();
                                             } catch (Exception e) {
                                                 Toast.makeText(getBaseContext(), "Failed!", Toast.LENGTH_LONG).show();
                                                 e.printStackTrace();
                                             }
                                         Log.d("content", data);
                                         Toast.makeText(getBaseContext(), data, Toast.LENGTH_LONG).show();
                                    }


            }
        });

    }

    private boolean checkPermission() {
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);


        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

}
