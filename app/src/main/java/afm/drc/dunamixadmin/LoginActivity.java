package afm.drc.dunamixadmin;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    EditText etPassword, etEmail;
    Button bLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etPassword = (EditText) findViewById(R.id.etPassword);
        etEmail = (EditText) findViewById(R.id.etEmail);
        bLogin = (Button) findViewById(R.id.bLogin);

        Login();
    }

    public void Login(){
        bLogin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String password = etPassword.getText().toString();
                        String identifier = etEmail.getText().toString();

                        String pass = "2021";
                        String email = "media@dunamix.drc";


                        if(password.equalsIgnoreCase(pass) && identifier.equalsIgnoreCase(email)){

                            Toast.makeText(LoginActivity.this, "Welcome user: Dunamix Admin" , Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Username and Password do not match" , Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }
}