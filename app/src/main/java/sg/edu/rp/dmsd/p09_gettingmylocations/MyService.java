package sg.edu.rp.dmsd.p09_gettingmylocations;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileWriter;

public class MyService extends Service {
    public MyService() {
    }

    boolean started;
    private FusedLocationProviderClient client;
    private LocationCallback mLocationCallback;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Have not yet implemented");
    }

    @Override
    public void onCreate() {
        client = LocationServices.getFusedLocationProviderClient(this);

        String folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mylocation";

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location data = locationResult.getLastLocation();
                    double lat = data.getLatitude();
                    double lng = data.getLongitude();
                    String msg = lat + "," + lng;
                    if (checkPermission() == true) {
                        try {
                            String folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyLocationr";
                            File targetFile = new File(folderLocation, "loc.txt");
                            FileWriter writer = new FileWriter(targetFile, true);
                            writer.write(msg + "\n");
                            writer.flush();
                            writer.close();
                        } catch (Exception e) {
                            Toast.makeText(MyService.this, "Failed!", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                }
            }

            ;
        };
        File folder = new File(folderLocation);
        if (folder.exists() == false) {
            boolean result = folder.mkdir();
            if (result == true) {
                Log.d("File is Write and Read", "Folder created");
            }
        }
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (started == false) {
            started = true;
            if (checkPermission() == true) {
                LocationRequest mLocationRequest = LocationRequest.create();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setInterval(10000);
                mLocationRequest.setFastestInterval(5000);
                mLocationRequest.setSmallestDisplacement(100);

                client.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            }
        } else {
            Toast.makeText(getBaseContext(), "Service still running", Toast.LENGTH_LONG).show();
        }


        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        client.removeLocationUpdates(mLocationCallback);
        super.onDestroy();
    }


    private boolean checkPermission() {
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MyService.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MyService.this, Manifest.permission.READ_EXTERNAL_STORAGE);


        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


}
