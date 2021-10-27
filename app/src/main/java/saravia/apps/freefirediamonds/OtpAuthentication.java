package saravia.apps.freefirediamonds;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OtpAuthentication extends AppCompatActivity {

    private EditText get_otp;
    private TextView change_number;
    private android.widget.Button verify_code;
    private String code_entered;
    private FirebaseAuth auth;
    private ProgressBar progressBar_of_auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_authentication);

        get_otp=findViewById(R.id.get_opt_code);
        change_number=findViewById(R.id.change_number);
        verify_code=findViewById(R.id.verify_opt_code);
        progressBar_of_auth=findViewById(R.id.progress_of_otp_auth);
        auth=FirebaseAuth.getInstance();

        change_number.setOnClickListener(v ->{
            Intent intent = new Intent(OtpAuthentication.this,MainActivity.class);
            startActivity(intent);
        });

        verify_code.setOnClickListener(v ->{
            code_entered=get_otp.getText().toString();
            if (code_entered.isEmpty()){
                Toast.makeText(getApplicationContext(), "Enter your code", Toast.LENGTH_SHORT).show();
            }else {
                progressBar_of_auth.setVisibility(View.VISIBLE);
                 String code_receiver=getIntent().getStringExtra("code");
                PhoneAuthCredential credential= PhoneAuthProvider.getCredential(code_receiver,code_entered);
                SignWithCredential(credential);
            }
        });

    }

    private void SignWithCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                progressBar_of_auth.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OtpAuthentication.this,SetProfile.class);
                startActivity(intent);
            }else {
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                    progressBar_of_auth.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}