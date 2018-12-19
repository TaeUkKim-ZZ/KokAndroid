package neolabs.kok;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class KokCommentActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView newnickname;
    TextView kokcomment;
    EditText commentview;
    Button sendbuttonview;
    String userauthid;
    Intent intent;
    String myselfauthid;
    String myselfnickname;

    RecyclerAdapter mAdapter;
    String[] commentsid= new String[99999];

    List<KokCommentItem> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kok_comment);

        intent = getIntent();

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        myselfauthid = pref.getString("userauthid",  null);
        myselfnickname = pref.getString("nickname", null);

        //무슨 콕인지 고유 번호를 받아서 불러온다.....
        newnickname = findViewById(R.id.textView2);
        kokcomment = findViewById(R.id.textView4);
        commentview = findViewById(R.id.editcommenttext);
        sendbuttonview = findViewById(R.id.commentsend);

        recyclerView = findViewById(R.id.commentrecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new RecyclerAdapter(items);
        recyclerView.setAdapter(mAdapter);

        newnickname.setText(intent.getStringExtra("username"));
        kokcomment.setText(intent.getStringExtra("kokcomment"));
        userauthid = intent.getStringExtra("userauthid");

        Retrofit client = new Retrofit.Builder().baseUrl("https://kok1.herokuapp.com/").addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitExService service = client.create(RetrofitExService.class);
        Call<KokData> call = service.getComment(userauthid);
        call.enqueue(new Callback<KokData>() {
            @Override
            public void onResponse(@NonNull Call<KokData> call, @NonNull retrofit2.Response<KokData> response) {
                switch (response.code()) {
                    case 200:
                        items.clear();
                        //mAdapter.notifyDataSetChanged();
                        List<Comment> comments = response.body().getComments();
                        for(int i = 0; i < comments.size(); i++) {
                            items.add(new KokCommentItem(comments.get(i).getContents(), comments.get(i).getAuthorusernickname()));
                            commentsid[i] = comments.get(i).getId();
                        }
                        mAdapter.notifyDataSetChanged();

                        //Toast.makeText(KokCommentActivity.this, "댓글이 정상적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                        break;
                    case 409:
                        Toast.makeText(KokCommentActivity.this, "에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Log.e("asdf", response.code() + "");
                        break;
                }
            }

            @Override
            public void onFailure(@NonNull Call<KokData> call, @NonNull Throwable t) {
                Log.d("checkonthe", "error");
            }
        });

        sendbuttonview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commentview.getText().toString().equals("")) return;
                Retrofit client = new Retrofit.Builder().baseUrl("https://kok1.herokuapp.com/").addConverterFactory(GsonConverterFactory.create()).build();
                RetrofitExService service = client.create(RetrofitExService.class);
                Call<KokData> call = service.addComment(userauthid, commentview.getText().toString(), myselfauthid, myselfnickname);
                call.enqueue(new Callback<KokData>() {
                    @Override
                    public void onResponse(@NonNull Call<KokData> call, @NonNull retrofit2.Response<KokData> response) {
                        switch (response.code()) {
                            case 200:
                                items.clear();
                                //mAdapter.notifyDataSetChanged();
                                List<Comment> comments = response.body().getComments();
                                for(int i = 0; i < comments.size(); i++) {
                                    Log.d("commentsize", comments.get(i).getContents());
                                    Log.d("commentsize", comments.get(i).getAuthorusernickname());
                                    commentsid[i] = comments.get(i).getId();
                                    items.add(new KokCommentItem(comments.get(i).getContents(), comments.get(i).getAuthorusernickname()));
                                }
                                mAdapter.notifyDataSetChanged();
                                commentview.setText("");
                                Toast.makeText(KokCommentActivity.this, "댓글이 정상적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                                break;
                            case 409:
                                Toast.makeText(KokCommentActivity.this, "에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Log.e("asdf", response.code() + "");
                                break;
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<KokData> call, @NonNull Throwable t) {
                        Log.d("checkonthe", "error");
                    }
                });
            }
        });
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<KokCommentItem> items;

        public RecyclerAdapter(List<KokCommentItem> items) {
            this.items = items;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.commentlist, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            myViewHolder.koktext.setText(items.get(position).koktext);
            myViewHolder.kokuser.setText(items.get(position).kokuser);
            myViewHolder.deletebuttona.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    items.remove(position);
                    mAdapter.notifyDataSetChanged();
                    Retrofit client = new Retrofit.Builder().baseUrl("https://kok1.herokuapp.com/").addConverterFactory(GsonConverterFactory.create()).build();
                    RetrofitExService service = client.create(RetrofitExService.class);
                    Call<KokData> call = service.deleteComment(userauthid, commentsid[position]);
                    call.enqueue(new Callback<KokData>() {
                        @Override
                        public void onResponse(@NonNull Call<KokData> call, @NonNull retrofit2.Response<KokData> response) {
                            switch (response.code()) {
                                case 200:
                                    Toast.makeText(KokCommentActivity.this, "댓글이 정상적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                    break;
                                case 409:
                                    Toast.makeText(KokCommentActivity.this, "에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Log.e("asdf", response.code() + "");
                                    break;
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<KokData> call, @NonNull Throwable t) {
                            Log.d("checkonthe", "error");
                        }
                    });
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView koktext;
        TextView kokuser;
        Button deletebuttona;

        MyViewHolder(View view) {
            super(view);
            koktext = view.findViewById(R.id.username3);
            kokuser = view.findViewById(R.id.introduce3);
            deletebuttona = view.findViewById(R.id.deletebutton);
        }
    }
}
