package saravia.apps.freefirediamonds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    ImageButton back_button;
    EditText view_name;
    ImageView view_image_in_image_view;
    CardView view_image;
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    TextView update_profile;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;
    private String Image_URI_Access_Token;
    androidx.appcompat.widget.Toolbar toolbar;
    FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        view_image_in_image_view=findViewById(R.id.view_user_image_in_image_view);
        view_name=findViewById(R.id.view_username);
        back_button=findViewById(R.id.back_button);
        auth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        update_profile=findViewById(R.id.update_profile);
        firebaseStorage=FirebaseStorage.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        back_button.setOnClickListener(v ->{
            finish();
        });

        storageReference=firebaseStorage.getReference();
        storageReference.child("Images").child(auth.getUid()).child("Profile Pick").getDownloadUrl().addOnSuccessListener(uri -> {
            Image_URI_Access_Token=uri.toString();
            Picasso.get().load(uri).into(view_image_in_image_view);
        });
        DatabaseReference databaseReference=firebaseDatabase.getReference(auth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserProfile profile = snapshot.getValue(UserProfile.class);
                view_name.setText(profile.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed to fetch", Toast.LENGTH_SHORT).show();
            }
        });
        update_profile.setOnClickListener(v ->{
            Intent intent = new Intent(ProfileActivity.this,UpdateProfile.class);
            intent.putExtra("name_user",view_name.getText().toString());

            startActivity(intent);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        DocumentReference documentReference=firebaseFirestore.collection("UserData").document(auth.getUid());
        documentReference.update("status","Online").addOnSuccessListener(command -> {

        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        DocumentReference documentReference=firebaseFirestore.collection("UserData").document(auth.getUid());
        documentReference.update("status","Offline").addOnSuccessListener(command -> {

        });
    }
}