package neolabs.kok;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    FloatingActionButton gotoprofile;
    FloatingActionButton addkok;

    RecyclerView recyclerview;
    RecyclerAdapter mAdapter;
    List<KokItem> items = new ArrayList<>();

    SwipeRefreshLayout mSwipeRefreshLayout;

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

        gotoprofile = findViewById(R.id.myprofile);
        addkok = findViewById(R.id.addkok);

        recyclerview = findViewById(R.id.mainrecyclerView);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));

        //여기에 아이템 추가.... 위치 얻어서....?
        items.add(new KokItem("testtext"));
        items.add(new KokItem("testtext"));
        items.add(new KokItem("testtext"));
        items.add(new KokItem("testtext"));
        items.add(new KokItem("testtext"));
        items.add(new KokItem("testtext"));
        items.add(new KokItem("testtext"));
        items.add(new KokItem("testtext"));
        items.add(new KokItem("testtext"));
        items.add(new KokItem("testtext"));
        items.add(new KokItem("testtext"));
        items.add(new KokItem("testtext"));

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
    }

    @Override
    public void onRefresh() {

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
}
