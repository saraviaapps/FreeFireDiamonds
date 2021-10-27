package saravia.apps.freefirediamonds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private CountryCodePicker codePicker;
    private EditText get_number;
    private android.widget.Button send_code;
    private ProgressBar progressBar_of_Main;
    private FirebaseAuth auth;
    private String country_code;
    private String phone_number;
    private String code_send;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        codePicker=findViewById(R.id.country_picker);
        get_number=findViewById(R.id.get_phone_number);
        send_code=findViewById(R.id.send_otp);
        progressBar_of_Main=findViewById(R.id.progress_of_main);
        auth=FirebaseAuth.getInstance();

        country_code=codePicker.getSelectedCountryCodeWithPlus();
        codePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                country_code=codePicker.getSelectedCountryCodeWithPlus();
            }
        });

        send_code.setOnClickListener(v ->{
            String number;
            number=get_number.getText().toString();
            if (number.isEmpty()){
                Toast.makeText(getApplicationContext(), "Please Insert Your Number!!", Toast.LENGTH_SHORT).show();
            }
            else if (number.length()<8)
            {
                Toast.makeText(getApplicationContext(), "Please Enter Correct Number", Toast.LENGTH_SHORT).show();
            }
            else {
                progressBar_of_Main.setVisibility(View.VISIBLE);
                phone_number=country_code+number;
                PhoneAuthOptions authOptions = PhoneAuthOptions.newBuilder(auth).setPhoneNumber(phone_number).setTimeout(60L, TimeUnit.SECONDS).setActivity(MainActivity.this).setCallbacks(callbacks).build();
                PhoneAuthProvider.verifyPhoneNumber(authOptions);
            }
        });

        callbacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(getApplicationContext(), "Code as send", Toast.LENGTH_SHORT).show();
                progressBar_of_Main.setVisibility(View.INVISIBLE);
                code_send=s;
                Intent intent =  new Intent(MainActivity.this,OtpAuthentication.class);
                intent.putExtra("code",code_send);
                startActivity(intent);
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser()!=null){
            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}