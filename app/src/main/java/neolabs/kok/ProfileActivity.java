package neolabs.kok;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    Button logout;
    String usernickname;
    String userintroduce;
    TextView putusername;
    TextView putintroduce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        putusername = findViewById(R.id.textView);
        putintroduce = findViewById(R.id.textView3);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        usernickname = pref.getString("nickname", "");
        userintroduce = pref.getString("introduce", "");

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
            }
        });
    }
}
