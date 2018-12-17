package neolabs.kok;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    Button logout;
    String usernickname;
    String userintroduce;
    String userauthid;
    TextView putusername;
    TextView putintroduce;
    RecyclerView recyclerView;

    RecyclerAdapter2 mAdapter;
    List<KokItem> items = new ArrayList<>();

    SwipeRefreshLayout mSwipeRefreshLayout;
    String[] userauthidarray = new String[99999];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout2);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        mSwipeRefreshLayout.setRefreshing(false);

        putusername = findViewById(R.id.textView);
        putintroduce = findViewById(R.id.textView3);
        logout = findViewById(R.id.logoutbutton);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        usernickname = pref.getString("nickname", "");
        userintroduce = pref.getString("introduce", "");
        userauthid = pref.getString("userauthid", "");

        recyclerView = findViewById(R.id.myrecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //여기에 아이템 추가.... 위치 얻어서....?

        //items.add(new KokItem("test"));

        mAdapter = new RecyclerAdapter2(items);
        recyclerView.setAdapter(mAdapter);

        putusername.setText(usernickname);
        putintroduce.setText(userintroduce);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.apply();

                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
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
        getkokfromserver();
    }

    public void getkokfromserver () {
        Retrofit client = new Retrofit.Builder().baseUrl("https://kok1.herokuapp.com/").addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitExService service = client.create(RetrofitExService.class);
        Call<List<KokData>> call = service.getmyPick(userauthid);
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
                        Toast.makeText(ProfileActivity.this, "에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
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
        getkokfromserver();
    }

    public class RecyclerAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<KokItem> items;

        public RecyclerAdapter2(List<KokItem> items) {
            this.items = items;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mylist, parent, false);
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
            koktext = view.findViewById(R.id.title2);
        }
    }
}
