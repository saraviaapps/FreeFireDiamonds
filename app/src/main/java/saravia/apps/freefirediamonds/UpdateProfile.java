package saravia.apps.freefirediamonds;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfile extends AppCompatActivity {

    private CardView cardView;
    private ImageView imageView;
    private EditText get_name;
    private ImageButton back_button;
    private android.widget.Button save_data;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private androidx.appcompat.widget.Toolbar toolbar;
    private String ImageAccessToken;
    private static int PICK_IMAGE= 983;
    private String new_name;
    private Intent intent;
    private Uri image_path;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        cardView=findViewById(R.id.get_user_image);
        imageView=findViewById(R.id.get_new_user_image_in_image_view);
        get_name=findViewById(R.id.get_new_username);
        back_button=findViewById(R.id.back_button);
        save_data=findViewById(R.id.save_data);
        progressBar=findViewById(R.id.progress_bar_of_update);
        toolbar=findViewById(R.id.toolbar_of_update_profile);
        intent=getIntent();
        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        setSupportActionBar(toolbar);

        back_button.setOnClickListener(v ->{
            finish();
        });

        get_name.setText(intent.getStringExtra("name_user"));

        DatabaseReference databaseReference=firebaseDatabase.getReference(auth.getUid());
        save_data.setOnClickListener(v ->{
            new_name=get_name.getText().toString();
            if (new_name.isEmpty()){
                Toast.makeText(getApplicationContext(), "Please insert name", Toast.LENGTH_SHORT).show();
            }
            else if (image_path!=null){
                progressBar.setVisibility(View.VISIBLE);
                UserProfile userProfile= new UserProfile(new_name,auth.getUid());
                databaseReference.setValue(userProfile);

                UpdateImage();
                Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                Intent intent=new Intent(UpdateProfile.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                progressBar.setVisibility(View.VISIBLE);
                UserProfile userProfile= new UserProfile(new_name,auth.getUid());
                databaseReference.setValue(userProfile);
                UpdateNameOnCloudFirestore();
                Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(UpdateProfile.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        imageView.setOnClickListener(v ->{
            Intent intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(intent,PICK_IMAGE);
        });

        storageReference=firebaseStorage.getReference();
        storageReference.child("Images").child(auth.getUid()).child("Profile Pick").getDownloadUrl().addOnSuccessListener(uri -> {
            ImageAccessToken=uri.toString();
            Picasso.get().load(uri).into(imageView);
        });


    }

    private void UpdateNameOnCloudFirestore() {
        DocumentReference reference=firebaseFirestore.collection("UserData").document(auth.getUid());
        Map<String,Object> userdata= new HashMap<>();
        userdata.put("name",new_name);
        userdata.put("image",ImageAccessToken);
        userdata.put("uid",auth.getUid());
        userdata.put("status","Online");
        reference.set(userdata).addOnSuccessListener(command -> {
            Toast.makeText(getApplicationContext(), "Data updated success", Toast.LENGTH_SHORT).show();
        });

    }

    private void UpdateImage(){
        StorageReference firebaseStorage=storageReference.child("Images").child(auth.getUid()).child("Profile Pick");
        Bitmap bitmap = null;
        try {
            bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),image_path);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP,25,outputStream);
        byte[] data = outputStream.toByteArray();

        UploadTask task = firebaseStorage.putBytes(data);
        task.addOnSuccessListener(taskSnapshot -> {
            firebaseStorage.getDownloadUrl().addOnSuccessListener(uri -> {
                ImageAccessToken=uri.toString();
                Toast.makeText(getApplicationContext(), "URI get success", Toast.LENGTH_SHORT).show();
                UpdateNameOnCloudFirestore();
            }).addOnFailureListener(e -> {
                Toast.makeText(getApplicationContext(), "URI get failed", Toast.LENGTH_SHORT).show();
            });
            Toast.makeText(getApplicationContext(), "Image si updated", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(getApplicationContext(), "Image not updated", Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK)
        {
            image_path=data.getData();
            imageView.setImageURI(image_path);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}