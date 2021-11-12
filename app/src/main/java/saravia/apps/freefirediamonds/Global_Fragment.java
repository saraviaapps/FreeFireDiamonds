package saravia.apps.freefirediamonds;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.PluralsRes;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class Global_Fragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private RecyclerView recyclerView;
    private EditText get_message;
    private ImageButton send_message;
    private HashMap<String,Object> messages;
    private ArrayList<HashMap<String,Object>> message_db;
    private FirebaseFirestore firebaseFirestore;
    private String nameg;
    private FirebaseModel model;
    private ChildEventListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.global_fragment,container,false);
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        recyclerView=view.findViewById(R.id.view_messages_on_global);
        get_message=view.findViewById(R.id.get_g_message);
        send_message=view.findViewById(R.id.send_message_global);
        model=new FirebaseModel();
        reference=database.getReference("MessagesGlobal");
        firebaseFirestore=FirebaseFirestore.getInstance();


        firebaseFirestore.collection("UserData").document(auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                nameg=documentSnapshot.get("name").toString();
            }
        });

        send_message.setOnClickListener(v ->{
            String message_to_send;
            message_to_send=get_message.getText().toString();
            if (message_to_send.isEmpty()){
                Toast.makeText(getContext(), "Please Entry Message", Toast.LENGTH_SHORT).show();
            }else {
                SendMessage(nameg,message_to_send,auth.getCurrentUser().getEmail());
                get_message.setText(null);
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                message_db=new ArrayList<>();
                try {
                    GenericTypeIndicator<HashMap<String,Object>> _ind= new GenericTypeIndicator<HashMap<String, Object>>() {};
                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        HashMap<String,Object> _map=snapshot1.getValue(_ind);
                        message_db.add(_map);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                recyclerView.setAdapter(new MessagesAdapter(message_db));
                LinearLayoutManager manager= new LinearLayoutManager(getContext());
                manager.setStackFromEnd(true);
                recyclerView.setLayoutManager(manager);
                recyclerView.setItemViewCacheSize(30);
                recyclerView.setDrawingCacheEnabled(true);
                recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Updater();

        return view;
    }

    private void SendMessage(String username,String message,String mail){
        messages= new HashMap<>();
        messages.put("username",username);
        messages.put("message",message);
        messages.put("mail",mail);
        messages.put("uid",auth.getUid());
        reference.push().updateChildren(messages);
        messages.clear();


    }

    public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder>
    {

        ArrayList<HashMap<String,Object>> _data;
        public MessagesAdapter(ArrayList<HashMap<String,Object>>_arr)
        {
            _data=_arr;
        }

        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){
            LayoutInflater inflater=(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v=inflater.inflate(R.layout.view_message_recycle,null);
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(params);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder,final int position)
        {
            View view = holder.itemView;
            TextView username=view.findViewById(R.id.view_username_g);
            TextView message_View=view.findViewById(R.id.view_message_g);
            username.setText(message_db.get(position).get("username").toString());
            message_View.setText(message_db.get(position).get("message").toString());

        }

        @Override
        public int getItemCount(){
            return  _data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public ViewHolder(View v){
                super(v);
            }
        }

    }
    private void Updater(){
        listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                GenericTypeIndicator<HashMap<String,Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                String _childKey =snapshot.getKey();
                HashMap<String,Object> _childValue = snapshot.getValue(_ind);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        message_db = new ArrayList<>();
                        try {
                            GenericTypeIndicator<HashMap<String,Object>> _ind= new GenericTypeIndicator<HashMap<String, Object>>() {};
                            for (DataSnapshot _data:snapshot.getChildren()){
                                HashMap<String,Object> _map=_data.getValue(_ind);
                                message_db.add(_map);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        recyclerView.setAdapter(new MessagesAdapter(message_db));
                        LinearLayoutManager manager = new LinearLayoutManager(getContext());
                        manager.setStackFromEnd(true);
                        recyclerView.setLayoutManager(manager);
                        recyclerView.setItemViewCacheSize(30);
                        recyclerView.setDrawingCacheEnabled(true);
                        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                GenericTypeIndicator<HashMap<String,Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                String _childKey =snapshot.getKey();
                HashMap<String,Object> _childValue = snapshot.getValue(_ind);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        message_db = new ArrayList<>();
                        try {
                            GenericTypeIndicator<HashMap<String,Object>> _ind= new GenericTypeIndicator<HashMap<String, Object>>() {};
                            for (DataSnapshot _data:snapshot.getChildren()){
                                HashMap<String,Object> _map=_data.getValue(_ind);
                                message_db.add(_map);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        recyclerView.setAdapter(new MessagesAdapter(message_db));
                        LinearLayoutManager manager = new LinearLayoutManager(getContext());
                        manager.setStackFromEnd(true);
                        recyclerView.setLayoutManager(manager);
                        recyclerView.setItemViewCacheSize(30);
                        recyclerView.setDrawingCacheEnabled(true);
                        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<HashMap<String,Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                String _childKey =snapshot.getKey();
                HashMap<String,Object> _childValue = snapshot.getValue(_ind);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        message_db = new ArrayList<>();
                        try {
                            GenericTypeIndicator<HashMap<String,Object>> _ind= new GenericTypeIndicator<HashMap<String, Object>>() {};
                            for (DataSnapshot _data:snapshot.getChildren()){
                                HashMap<String,Object> _map=_data.getValue(_ind);
                                message_db.add(_map);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        recyclerView.setAdapter(new MessagesAdapter(message_db));
                        LinearLayoutManager manager = new LinearLayoutManager(getContext());
                        manager.setStackFromEnd(true);
                        recyclerView.setLayoutManager(manager);
                        recyclerView.setItemViewCacheSize(30);
                        recyclerView.setDrawingCacheEnabled(true);
                        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                GenericTypeIndicator<HashMap<String,Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                String _childKey =snapshot.getKey();
                HashMap<String,Object> _childValue = snapshot.getValue(_ind);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        message_db = new ArrayList<>();
                        try {
                            GenericTypeIndicator<HashMap<String,Object>> _ind= new GenericTypeIndicator<HashMap<String, Object>>() {};
                            for (DataSnapshot _data:snapshot.getChildren()){
                                HashMap<String,Object> _map=_data.getValue(_ind);
                                message_db.add(_map);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        recyclerView.setAdapter(new MessagesAdapter(message_db));
                        LinearLayoutManager manager = new LinearLayoutManager(getContext());
                        manager.setStackFromEnd(true);
                        recyclerView.setLayoutManager(manager);
                        recyclerView.setItemViewCacheSize(30);
                        recyclerView.setDrawingCacheEnabled(true);
                        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

}
