package neolabs.kok;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    Button login;
    Button signup;
    EditText inputemail;
    EditText inputpassword;
    String emailstring;
    String passwordstring;
    String encryptedstring;
    LockClass getsha512 = new LockClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.email_login_button);
        signup = findViewById(R.id.email_signup_button);
        inputemail = findViewById(R.id.email_edittext);
        inputpassword = findViewById(R.id.password_edittext);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailstring = inputemail.getText().toString();
                passwordstring = inputpassword.getText().toString();

                //SHA-512암호화 해야지....
                encryptedstring = getsha512.getSHA512(passwordstring);
                passwordstring = ""; //일이 끝난 이후에는 원문을 메모리에서도 없애버리자.

                //결과를 받아온다.
                logintoserver(emailstring, encryptedstring);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void logintoserver(String email, String password) {
        Retrofit client = new Retrofit.Builder().baseUrl("https://kok1.herokuapp.com/").addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitExService service = client.create(RetrofitExService.class);
        Call<Data> call = service.signinUserInfo(email, password);
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, retrofit2.Response<Data> response) {
                switch (response.code()) {
                    case 200:
                        Data body = response.body();

                        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("useremail", body.getEmail());
                        editor.putString("userauthid", body.getId());
                        editor.putString("gender", body.getGender());
                        editor.putString("nickname", body.getNickname());
                        editor.putString("introduce", body.getIntroduce());
                        editor.apply();

                        Toast.makeText(LoginActivity.this, "로그인 완료", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case 409:
                        Toast.makeText(LoginActivity.this, "존재하지 않은 계정입니다.", Toast.LENGTH_SHORT).show();
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
