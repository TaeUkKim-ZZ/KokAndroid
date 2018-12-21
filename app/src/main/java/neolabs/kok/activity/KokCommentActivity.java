package neolabs.kok.activity;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import neolabs.kok.R;
import neolabs.kok.data.Comment;
import neolabs.kok.data.Data;
import neolabs.kok.data.KokData;
import neolabs.kok.item.KokCommentItem;
import neolabs.kok.retrofit.RetrofitExService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class KokCommentActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView newnickname;
    TextView kokcomment;
    EditText commentview;
    Button sendbuttonview;
    String kokid;
    Intent intent;
    String myselfauthid;
    String myselfnickname;

    ImageView profileImage;
    String profileImagelink;

    RecyclerAdapter mAdapter;
    String[] commentsid= new String[99999];

    List<KokCommentItem> items = new ArrayList<>();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

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
        profileImage = findViewById(R.id.profile_image3);

        recyclerView = findViewById(R.id.commentrecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new RecyclerAdapter(items);

        getuserInfo(intent.getStringExtra("userauthid"));
        kokcomment.setText(intent.getStringExtra("kokcomment"));

        /*newnickname.setText(intent.getStringExtra("username"));
        profileImagelink = intent.getStringExtra("profileImage");*/

        kokid = intent.getStringExtra("kokidarray");

        Retrofit client = new Retrofit.Builder().baseUrl(RetrofitExService.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitExService service = client.create(RetrofitExService.class);
        Call<KokData> call = service.getComment(kokid);
        call.enqueue(new Callback<KokData>() {
            @Override
            public void onResponse(@NonNull Call<KokData> call, @NonNull Response<KokData> response) {
                switch (response.code()) {
                    case 200:
                        items.clear();
                        //mAdapter.notifyDataSetChanged();
                        final List<Comment> comments = response.body().getComments();
                        for(int i = 0; i < comments.size(); i++) {
                            final int low = i;
                            if (comments.get(low).getAuthorauthid().equals(myselfauthid)) {
                                items.add(new KokCommentItem(comments.get(low).getContents(), comments.get(low).getAuthorauthid(),  true));
                            } else {
                                items.add(new KokCommentItem(comments.get(low).getContents(), comments.get(low).getAuthorauthid(), false));
                            }
                            /*getCommentUserInfo(comments.get(i).getAuthorauthid(), userinfo -> {
                                List<KokCommentItem> items2 = new ArrayList<>();
                                //Log.d("kokuserprofile", userinfo[0] + userinfo[1]);

                                if (comments.get(low).getAuthorauthid().equals(myselfauthid)) {
                                        items.add(new KokCommentItem(comments.get(low).getContents(), userinfo[0], userinfo[1], comments.get(low).getCommentDate(), true));
                                } else {
                                        items.add(new KokCommentItem(comments.get(low).getContents(), userinfo[0], userinfo[1], comments.get(low).getCommentDate(), false));
                                }



                                commentsid[low] = comments.get(low).getId();
                            });*/
                        }

                        recyclerView.setAdapter(mAdapter);

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
                Retrofit client = new Retrofit.Builder().baseUrl(RetrofitExService.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
                RetrofitExService service = client.create(RetrofitExService.class);
                Call<KokData> call = service.addComment(kokid, commentview.getText().toString(), myselfauthid, myselfnickname);
                call.enqueue(new Callback<KokData>() {
                    @Override
                    public void onResponse(@NonNull Call<KokData> call, @NonNull retrofit2.Response<KokData> response) {
                        switch (response.code()) {
                            case 200:
                                items.clear();
                                final List<Comment> comments = response.body().getComments();
                                for(int i = 0; i < comments.size(); i++) {
                                    final int low = i;
                                    if (comments.get(low).getAuthorauthid().equals(myselfauthid)) {
                                        items.add(new KokCommentItem(comments.get(low).getContents(), comments.get(low).getAuthorauthid(), true));
                                    } else {
                                        items.add(new KokCommentItem(comments.get(low).getContents(), comments.get(low).getAuthorauthid(), false));
                                    }
                                    /*getCommentUserInfo(comments.get(i).getAuthorauthid(), userinfo -> {
                                        Log.d("kokuserprofile", comments.get(low).getCommentDate());
                                        if (comments.get(low).getAuthorauthid().equals(myselfauthid)) {
                                            items.add(new KokCommentItem(comments.get(low).getContents(), userinfo[0], userinfo[1], comments.get(low).getCommentDate(), true));
                                        } else {
                                            items.add(new KokCommentItem(comments.get(low).getContents(), userinfo[0], userinfo[1], comments.get(low).getCommentDate(), false));
                                        }
                                        commentsid[low] = comments.get(low).getId();
                                    });*/
                                    //Log.d("userinfo", userinfo[0] + userinfo[1]);

                                    //Log.d("commentsize", comments.get(i).getContents());
                                    //Log.d("commentsize", comments.get(i).getAuthorusernickname());
                                    //Log.d("commentsize", comments.get(i).getAuthorauthid());

                                }

                                //Log.d("insideitems", items.toString());

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

    private interface getCommentUserCallback {
        void setUserInfo(String[] userinfo);
    }

    public void getCommentUserInfo(String userauth, final getCommentUserCallback callback) {
        final String[] userinfo = new String[2];
        Retrofit client = new Retrofit.Builder().baseUrl(RetrofitExService.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitExService service = client.create(RetrofitExService.class);
        Call<Data> call = service.getuserInfo(userauth);
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(@NonNull Call<Data> call, @NonNull retrofit2.Response<Data> response) {
                switch (response.code()) {
                    case 200:
                        userinfo[0] = response.body().getNickname();
                        userinfo[1] = response.body().getProfileimage();
                        callback.setUserInfo(userinfo);
                        break;
                    case 409:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(@NonNull Call<Data> call, @NonNull Throwable t) {
                Log.d("checkonthe", "error");
            }
        });
    }

    public void getuserInfo(String userauthid) {
        Retrofit client = new Retrofit.Builder().baseUrl(RetrofitExService.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitExService service = client.create(RetrofitExService.class);
        Call<Data> call = service.getuserInfo(userauthid);
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(@NonNull Call<Data> call, @NonNull retrofit2.Response<Data> response) {
                switch (response.code()) {
                    case 200:
                        newnickname.setText(response.body().getNickname());
                        profileImagelink = response.body().getProfileimage();

                        if(profileImagelink.equals("default")) {
                            profileImage.setImageResource(R.mipmap.ic_launcher_round);
                        } else {
                            Glide.with(KokCommentActivity.this)
                                    .load(RetrofitExService.BASE_URL + "images/" + profileImagelink)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(profileImage);
                        }
                        //Toast.makeText(KokCommentActivity.this, "댓글이 정상적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                        break;
                    case 409:
                        Toast.makeText(KokCommentActivity.this, "에러2가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Log.e("asdf", response.code() + "");
                        break;
                }
            }

            @Override
            public void onFailure(@NonNull Call<Data> call, @NonNull Throwable t) {
                Log.d("checkonthe", "error");
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
            getCommentUserInfo(items.get(position).kokuserauthid, userinfo -> {
                Glide.with(KokCommentActivity.this)
                        .load(RetrofitExService.BASE_URL + "images/" + userinfo[1])
                        .apply(RequestOptions.circleCropTransform())
                        .into(myViewHolder.profileImage);
                myViewHolder.kokuser.setText(userinfo[0]);
            });
            /*Glide.with(KokCommentActivity.this)
                    .load(RetrofitExService.BASE_URL + "images/" + items.get(position).kokuserprofile)
                    .apply(RequestOptions.circleCropTransform())
                    .into(myViewHolder.profileImage);*/


            //Log.d("kokuserprofile2", RetrofitExService.BASE_URL + "images/" + items.get(position).kokuserprofile);
            /*if(!items.get(position).kokuserprofile.equals("default")) {
                Glide.with(KokCommentActivity.this)
                        .asBitmap()
                        .load(RetrofitExService.BASE_URL + "images/" + items.get(position).kokuserprofile)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                                myViewHolder.profileImage.setImageBitmap(resource);
                            }
                        });
            }*/

            //myViewHolder.profileImage.setImageResource(R.drawable.custom_callout_balloon);
            myViewHolder.koktext.setText(items.get(position).koktext);
            //myViewHolder.kokuser.setText(items.get(position).kokuser);
            myViewHolder.deletebuttona.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    items.remove(position);
                    mAdapter.notifyDataSetChanged();
                    Retrofit client = new Retrofit.Builder().baseUrl(RetrofitExService.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
                    RetrofitExService service = client.create(RetrofitExService.class);
                    Call<KokData> call = service.deleteComment(kokid, commentsid[position]);
                    call.enqueue(new Callback<KokData>() {
                        @Override
                        public void onResponse(@NonNull Call<KokData> call, @NonNull Response<KokData> response) {
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
            if(!items.get(position).ismycomment) {
                myViewHolder.deletebuttona.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView koktext;
        TextView kokuser;
        Button deletebuttona;

        MyViewHolder(View view) {
            super(view);
            profileImage = view.findViewById(R.id.feelimage2);
            koktext = view.findViewById(R.id.username3);
            kokuser = view.findViewById(R.id.introduce3);
            deletebuttona = view.findViewById(R.id.deletebutton);
        }
    }
}
