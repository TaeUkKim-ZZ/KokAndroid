package neolabs.kok.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import neolabs.kok.LockClass;
import neolabs.kok.R;
import neolabs.kok.data.Data;
import neolabs.kok.retrofit.RetrofitExService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupActivity extends AppCompatActivity {

    Button signupcomplete;
    EditText inputemail;
    EditText inputpassword;
    EditText inputnickname;
    EditText inputintroduce;
    RadioButton male;
    RadioButton female;
    String emailstring;
    String passwordstring;
    String nicknamestring;
    String genderstring;
    String introducestring;
    String encryptedstring;
    LockClass getsha512 = new LockClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        setView();

        signupcomplete = findViewById(R.id.email_signup_button2);
        signupcomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //정보들을 뷰로 부터 얻어와서 최종적으로 가입함수로 보낸다.
                emailstring = inputemail.getText().toString();
                passwordstring = inputpassword.getText().toString();
                nicknamestring = inputnickname.getText().toString();
                if(male.isChecked()) {
                    genderstring = "Male";
                } else if(female.isChecked()) {
                    genderstring = "Female";
                }
                introducestring = inputintroduce.getText().toString();
                encryptedstring = getsha512.getSHA512(passwordstring);
                passwordstring = "";

                signintoserver(emailstring, encryptedstring, genderstring, introducestring, nicknamestring);
            }
        });
    }

    public void setView() {
        inputemail = findViewById(R.id.email_edittext2);
        inputpassword = findViewById(R.id.password_edittext2);
        inputnickname = findViewById(R.id.getname_edittext3);
        inputintroduce = findViewById(R.id.getname_edittext5);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
    }

    //서버에 사용자를 가입시켜준다.
    public void signintoserver(String email, String password, String gender, String introduce, String nickname) {
        Retrofit client = new Retrofit.Builder().baseUrl(RetrofitExService.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitExService service = client.create(RetrofitExService.class);
        Call<Data> call = service.signupUserInfo(email, password, gender, nickname, introduce);
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, retrofit2.Response<Data> response) {
                switch (response.code()) {
                    case 200:
                        Data body = response.body();

                        //Retrofit에서 받아온 데이터를 Sharedpreferences에 저장해준다.
                        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("useremail", body.getEmail());
                        editor.putString("userauthid", body.getId());
                        editor.putString("gender", body.getGender());
                        editor.putString("nickname", body.getNickname());
                        editor.putString("introduce", body.getIntroduce());
                        editor.apply();

                        Toast.makeText(SignupActivity.this, "가입 완료", Toast.LENGTH_SHORT).show();

                        //가입이 끝난 이후에 메인 엑티비티로 간다.
                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case 409:
                        Toast.makeText(SignupActivity.this, "이미 존재하는 이메일입니다. 다른 이메일을 입력하여 주십시오.", Toast.LENGTH_SHORT).show();
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

        //출처: http://falinrush.tistory.com/5 [형필 개발일지]
    }
}
