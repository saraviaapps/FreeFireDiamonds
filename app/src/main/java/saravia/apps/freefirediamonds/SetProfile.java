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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.common.collect.BiMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SetProfile extends AppCompatActivity {

    private CardView cardView;
    private ImageView imageView;
    private static int PICK_IMAGE=983;
    private Uri image_path;
    private EditText get_username;
    private android.widget.Button save_button;
    private FirebaseAuth auth;
    private String name;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String image_uri_access_token;
    private FirebaseFirestore firebaseFirestore;
    private ProgressBar progressBar_set;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);
        cardView=findViewById(R.id.get_user_image);
        imageView=findViewById(R.id.get_user_image_in_image_view);
        get_username=findViewById(R.id.get_username);
        save_button=findViewById(R.id.save_profile_button);
        progressBar_set=findViewById(R.id.progress_of_save_profile);
        auth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();

        cardView.setOnClickListener(v ->{
            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(intent,PICK_IMAGE);
        });

        save_button.setOnClickListener(V ->{
            name = get_username.getText().toString();
            if(name.isEmpty()){
                Toast.makeText(getApplicationContext(), "Insert a name", Toast.LENGTH_SHORT).show();
            }
            else if(image_path==null){
                Toast.makeText(getApplicationContext(), "Select a image", Toast.LENGTH_SHORT).show();
            }
            else
            {
                progressBar_set.setVisibility(View.VISIBLE);
                SendDataForNewUser();
                progressBar_set.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(SetProfile.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void SendDataForNewUser() {
        SendDataForRealtimeDatabase();
    }

    private void SendDataForRealtimeDatabase(){
        name =get_username.getText().toString().trim();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference(auth.getUid());
        UserProfile profile = new UserProfile(name,auth.getUid());
        reference.setValue(profile);
        Toast.makeText(getApplicationContext(), "User Profile added successfully", Toast.LENGTH_SHORT).show();
        SendImageToStorage();
    }

    private void SendImageToStorage() {
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
               image_uri_access_token=uri.toString();
               Toast.makeText(getApplicationContext(), "URI get success", Toast.LENGTH_SHORT).show();
               SendDataToFirestore();
           }).addOnFailureListener(e -> {
               Toast.makeText(getApplicationContext(), "URI get failed", Toast.LENGTH_SHORT).show();
           });
            Toast.makeText(getApplicationContext(), "Image si uploaded", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(getApplicationContext(), "Image not uploaded", Toast.LENGTH_SHORT).show();
        });

    }

    private void SendDataToFirestore() {
        DocumentReference reference=firebaseFirestore.collection("UserData").document(auth.getUid());
        Map<String,Object> userdata= new HashMap<>();
        userdata.put("name",name);
        userdata.put("image",image_uri_access_token);
        userdata.put("uid",auth.getUid());
        userdata.put("status","Online");
        reference.set(userdata).addOnSuccessListener(command -> {
            Toast.makeText(getApplicationContext(), "Data send success", Toast.LENGTH_SHORT).show();
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