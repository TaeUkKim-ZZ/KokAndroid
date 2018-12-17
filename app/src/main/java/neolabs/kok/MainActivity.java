package neolabs.kok;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    FloatingActionButton gotoprofile;
    FloatingActionButton addkok;

    RecyclerView recyclerview;
    RecyclerAdapter mAdapter;
    List<KokItem> items = new ArrayList<>();

    SwipeRefreshLayout mSwipeRefreshLayout;

    String[] userauthidarray = new String[99999];

    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission = false;

    private GPSInfo gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        mSwipeRefreshLayout.setRefreshing(false);

        gotoprofile = (FloatingActionButton) findViewById(R.id.myprofile);
        addkok = (FloatingActionButton) findViewById(R.id.addkok);

        recyclerview = findViewById(R.id.mainrecyclerView);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));

        getgpsdata();

        //여기에 아이템 추가.... 위치 얻어서....?

        //items.add(new KokItem("test"));

        mAdapter = new RecyclerAdapter(items);
        recyclerview.setAdapter(mAdapter);

        gotoprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        addkok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddKokActivity.class);
                startActivity(intent);
            }
        });

        recyclerview.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recyclerview, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(getApplicationContext(),position+"번 째 아이템 클릭",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Toast.makeText(getApplicationContext(),position+"번 째 아이템 롱 클릭",Toast.LENGTH_SHORT).show();
                        //삭제 여부를 물어본후 삭제한다.
                    }
                }));
    }

    public void getgpsdata() {
        if(!isPermission){
            callPermission();
        }

        gps = new GPSInfo(MainActivity.this);

        // GPS 사용유무 가져오기
        if (gps.isGetLocation()) {
            //GPSInfo를 통해 알아낸 위도값과 경도값
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            Log.d("latitude", String.format("%f", latitude));
            Log.d("longitude", String.format("%f", longitude));

            getkokfromserver(String.format("%f", latitude), String.format("%f", longitude));
        } else {
            // GPS 를 사용할수 없으므로
            gps.showSettingsAlert();
        }
    }

    public void getkokfromserver (String latitude, String longitude) {
        Retrofit client = new Retrofit.Builder().baseUrl("https://kok1.herokuapp.com/").addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitExService service = client.create(RetrofitExService.class);
        Call<List<KokData>> call = service.getPick(latitude, longitude);
        call.enqueue(new Callback<List<KokData>>() {
            @Override
            public void onResponse(@NonNull Call<List<KokData>> call, @NonNull retrofit2.Response<List<KokData>> response) {
                Log.d("softtag", "isitworkingcheck");
                switch (response.code()) {
                    case 200:
                        //Log.d("softtag", Integer.toString(response.body().size()));
                        for(int i = 0; i < response.body().size(); i++) {
                            items.add(new KokItem(response.body().get(i).getMessage()));
                            userauthidarray[i] = response.body().get(i).getUserauthid();
                            //Log.d("softtag", response.body().get(i).getMessage());
                        }
                        mAdapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                        //출처: http://jekalmin.tistory.com/entry/Gson을-이용한-json을-객체에-담기 [jekalmin의 블로그]
                        //Log.d("softtag", body.toString());
                        break;
                    case 409:
                        Toast.makeText(MainActivity.this, "에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                    default:
                        Log.e("asdf", response.code() + "");
                        break;
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<KokData>> call, @NonNull Throwable t) {
                Log.d("checkonthe", "error");
            }
        });
    }

    @Override
    public void onRefresh() {
        items.clear();
        mAdapter.notifyDataSetChanged();
        getgpsdata();
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<KokItem> items;

        public RecyclerAdapter(List<KokItem> items) {
            this.items = items;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listlayout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            myViewHolder.koktext.setText(items.get(position).koktext);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView koktext;

        MyViewHolder(View view) {
            super(view);
            koktext = view.findViewById(R.id.title);
        }
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
                && ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_ACCESS_FINE_LOCATION);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {
            isPermission = true;
        }
    }
}
