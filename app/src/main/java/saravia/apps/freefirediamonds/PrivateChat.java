package saravia.apps.freefirediamonds;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PrivateChat extends AppCompatActivity {

    private ImageButton back;
    private androidx.appcompat.widget.Toolbar toolbar;
    private RecyclerView recyclerView;
    private EditText get_message;
    private ImageView user_image,send_message;
    private TextView view_name;
    private Intent intent;
    private CardView cardView;

    private String entered_message;
    private String receiver_name,sender_name,receiver_uid,sender_uid;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private String sender_rom,receiver_rom;
    private ChildEventListener listener1;
    String current_time;
    Calendar calendar;
    SimpleDateFormat format;
    private MessagesAdapter messagesAdapter;
    private ArrayList<Messages> messagesArrayList;
    //private

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_chat);

        back=findViewById(R.id.back_button);
        toolbar=findViewById(R.id.toolbar_private);
        recyclerView=findViewById(R.id.recycle_view_private);
        get_message=findViewById(R.id.get_message);
        user_image=findViewById(R.id.view_image_private);
        send_message=findViewById(R.id.send_message);
        view_name=findViewById(R.id.name_private);
        intent=getIntent();
        cardView=findViewById(R.id.card_view_send_message);
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        setSupportActionBar(toolbar);
        cardView=findViewById(R.id.card_view_private);
        messagesArrayList= new ArrayList<>();
        LinearLayoutManager manager= new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);
        messagesAdapter= new MessagesAdapter(PrivateChat.this,messagesArrayList);
        recyclerView.setAdapter(messagesAdapter);

        toolbar.setOnClickListener(v ->{
            Toast.makeText(getApplicationContext(), "Toolbar is clicked", Toast.LENGTH_SHORT).show();
        });
        
        cardView.setOnClickListener(v ->{
            Toast.makeText(getApplicationContext(), "Image View", Toast.LENGTH_SHORT).show();
            ShowImage(intent.getStringExtra("imageURL"));
        });

        calendar=Calendar.getInstance();
        format=new SimpleDateFormat("hh:mm a");
        sender_uid=auth.getUid();
        receiver_uid=getIntent().getStringExtra("receiverUID");
        receiver_name=getIntent().getStringExtra("name");


        sender_rom=sender_uid+receiver_uid;
        receiver_rom=receiver_uid+sender_uid;


        DatabaseRefresh();

        DatabaseReference reference=database.getReference().child("chats").child(sender_rom).child("messages");
        messagesAdapter=new MessagesAdapter(PrivateChat.this,messagesArrayList);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for(DataSnapshot snapshot1 :snapshot.getChildren())
                {
                    Messages messages=snapshot1.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Handler refresh= new Handler();
        refresh.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("data","data is activate");
                DatabaseReference reference=database.getReference().child("chats").child(sender_rom).child("messages");
                messagesAdapter=new MessagesAdapter(PrivateChat.this,messagesArrayList);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messagesArrayList.clear();
                        for(DataSnapshot snapshot1 :snapshot.getChildren())
                        {
                            Messages messages=snapshot1.getValue(Messages.class);
                            messagesArrayList.add(messages);
                        }
                        messagesAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        },5000);


        back.setOnClickListener(v ->{
            finish();
        });

        view_name.setText(receiver_name);
        String uri=getIntent().getStringExtra("imageURL");
        /*if (uri.isEmpty()){
            Toast.makeText(getApplicationContext(), "Not image received", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Picasso.get().load(uri).into(user_image);
        }*/

        send_message.setOnClickListener(v ->{
            entered_message=get_message.getText().toString();
            if (entered_message.isEmpty())
            {
                Toast.makeText(getApplicationContext(), "Entered message please", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Date date=new Date();
                current_time=format.format(calendar.getTime());
                Messages messages = new Messages(entered_message,auth.getUid(),date.getTime(),current_time);
                database.getReference().child("chats").child(sender_rom)
                        .child("messages")
                        .push().setValue(messages).addOnCompleteListener(task -> {
                            database.getReference().child("chats")
                                    .child(receiver_rom).child("messages")
                                    .push().setValue(messages).addOnCompleteListener(task1 ->{

                            });
                });

                get_message.setText(null);
            }
        });




    }

    private void DatabaseRefresh() {
        listener1= new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                DatabaseReference reference=database.getReference().child("chats").child(sender_rom).child("messages");
                messagesAdapter=new MessagesAdapter(PrivateChat.this,messagesArrayList);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messagesArrayList.clear();
                        for(DataSnapshot snapshot1 :snapshot.getChildren())
                        {
                            Messages messages=snapshot1.getValue(Messages.class);
                            messagesArrayList.add(messages);
                        }
                        messagesAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(messagesAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                DatabaseReference reference=database.getReference().child("chats").child(sender_rom).child("messages");
                messagesAdapter=new MessagesAdapter(PrivateChat.this,messagesArrayList);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messagesArrayList.clear();
                        for(DataSnapshot snapshot1 :snapshot.getChildren())
                        {
                            Messages messages=snapshot1.getValue(Messages.class);
                            messagesArrayList.add(messages);
                        }
                        messagesAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(messagesAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                DatabaseReference reference=database.getReference().child("chats").child(sender_rom).child("messages");
                messagesAdapter=new MessagesAdapter(PrivateChat.this,messagesArrayList);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messagesArrayList.clear();
                        for(DataSnapshot snapshot1 :snapshot.getChildren())
                        {
                            Messages messages=snapshot1.getValue(Messages.class);
                            messagesArrayList.add(messages);

                        }
                        messagesAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(messagesAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };


        messagesAdapter=new MessagesAdapter(PrivateChat.this,messagesArrayList);
        DatabaseReference reference=database.getReference().child("chats").child(sender_rom).child("messages");
        reference.addChildEventListener(listener1);
        //messagesAdapter.notifyDataSetChanged();

    }

    private void ShowImage(String uri)
    {
        AlertDialog.Builder builder= new AlertDialog.Builder(PrivateChat.this);
        ViewGroup viewGroup=findViewById(android.R.id.content);
        View dialogView= LayoutInflater.from(getApplicationContext()).inflate(R.layout.view_user_image,viewGroup,false);
        ImageView user_image=dialogView.findViewById(R.id.view_image_on_dialog);
        Picasso.get().load(uri).into(user_image);
        builder.setView(dialogView);
        AlertDialog alertDialog= builder.create();
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        messagesAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (messagesAdapter != null)
        {
            messagesAdapter.notifyDataSetChanged();
        }

    }
}