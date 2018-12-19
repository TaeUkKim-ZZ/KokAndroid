package neolabs.kok;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditProfileActivity extends AppCompatActivity {

    EditText inputpassword;
    EditText inputnickname;
    EditText inputintroduce;
    RadioButton editmale;
    RadioButton editfemale;
    Button senddata;
    String passwordstring;
    String encryptedstring;
    String nicknamestring;
    String genderstring;
    String introducestring;
    String findemail;
    LockClass getsha512 = new LockClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        inputpassword = findViewById(R.id.password_edittext3);
        inputnickname = findViewById(R.id.getname_edittext4);
        inputintroduce = findViewById(R.id.getname_edittext6);
        editmale = findViewById(R.id.male2);
        editfemale = findViewById(R.id.female2);

        senddata = findViewById(R.id.email_signup_button3);
        senddata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                String findemail = pref.getString("useremail",  "");

                passwordstring = inputpassword.getText().toString();
                if(!passwordstring.equals("")) encryptedstring = getsha512.getSHA512(passwordstring);
                else encryptedstring = "";
                passwordstring = "";
                nicknamestring = inputnickname.getText().toString();
                if(editmale.isChecked()) {
                    genderstring = "Male";
                } else if(editfemale.isChecked()) {
                    genderstring = "Female";
                } else {
                    genderstring = "";
                }
                introducestring = inputintroduce.getText().toString();

                Retrofit client = new Retrofit.Builder().baseUrl("https://kok1.herokuapp.com/").addConverterFactory(GsonConverterFactory.create()).build();
                RetrofitExService service = client.create(RetrofitExService.class);
                Call<Data> call = service.EditUserInfo(findemail, encryptedstring, genderstring, nicknamestring, introducestring);
                call.enqueue(new Callback<Data>() {
                    @Override
                    public void onResponse(Call<Data> call, retrofit2.Response<Data> response) {
                        switch (response.code()) {
                            case 200:
                                Data body = response.body();

                                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("gender", body.getGender());
                                editor.putString("nickname", body.getNickname());
                                editor.putString("introduce", body.getIntroduce());
                                editor.apply();

                                Toast.makeText(EditProfileActivity.this, "수정 완료", Toast.LENGTH_SHORT).show();
                                finish();
                                break;
                            case 409:
                                Toast.makeText(EditProfileActivity.this, "문제가 발생했습니다.", Toast.LENGTH_SHORT).show();
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
        });
    }
}
