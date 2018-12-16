package neolabs.kok;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddKokActivity extends AppCompatActivity {

    EditText inputmessage;
    Button sendmessage;
    String gomessage;
    String userauthid;

    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission = false;

    private GPSInfo gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_kok);

        sendmessage = findViewById(R.id.sendbutton2);
        inputmessage = findViewById(R.id.editText2);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        userauthid = pref.getString("userauthid", null);

        sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gomessage = inputmessage.getText().toString();

                if(!isPermission){
                    callPermission();
                    return;
                }

                gps = new GPSInfo(AddKokActivity.this);

                // GPS 사용유무 가져오기
                if (gps.isGetLocation()) {
                    //GPSInfo를 통해 알아낸 위도값과 경도값
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    Log.d("latitude", String.format("%f", latitude));
                    Log.d("longitude", String.format("%f", longitude));

                    sendreqeust(String.format("%f", latitude), String.format("%f", longitude), userauthid, gomessage);
                } else {
                    // GPS 를 사용할수 없으므로
                    gps.showSettingsAlert();
                }
            }
        });

        callPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            isAccessFineLocation = true;
        } else if (requestCode == PERMISSIONS_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            isAccessCoarseLocation = true;
        }

        if (isAccessFineLocation && isAccessCoarseLocation) {
            isPermission = true;
        }
    }


    // 전화번호 권한 요청
    private void callPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(AddKokActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_ACCESS_FINE_LOCATION);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(AddKokActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {
            isPermission = true;
        }
    }

    public void sendreqeust(String latitude, String longitude, String userauthid, String message) {
        //리퀘스트를 보낸다아아아아아아아
        Retrofit client = new Retrofit.Builder().baseUrl("https://kok1.herokuapp.com/").addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitExService service = client.create(RetrofitExService.class);
        Call<Data> call = service.addPick(latitude, longitude, userauthid, message);
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, retrofit2.Response<Data> response) {
                switch (response.code()) {
                    case 200:
                        Data body = response.body();
                        Log.d("responsebody", body.toString());

                        Toast.makeText(AddKokActivity.this, "콕 저장 완료", Toast.LENGTH_SHORT).show();
                        break;
                    case 409:
                        Toast.makeText(AddKokActivity.this, "문제가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    default:
                        Log.e("asdf", response.code() + "");
                        break;
                }
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                Log.d("checkonthe", "error");
            }
        });
    }
}
