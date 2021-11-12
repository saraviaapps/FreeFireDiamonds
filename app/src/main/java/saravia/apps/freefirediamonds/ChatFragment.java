package saravia.apps.freefirediamonds;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

public class ChatFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseAuth auth;
    private RecyclerView recyclerView;
    private ImageView user_image;
    FirestoreRecyclerAdapter<FirebaseModel,NoteViewHolder> chat_Adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.chat_fragment,container,false);
        firebaseFirestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        recyclerView =view.findViewById(R.id.recycle_view);

        Query query=firebaseFirestore.collection("UserData").whereNotEqualTo("uid",auth.getUid());
        FirestoreRecyclerOptions<FirebaseModel> all_user_name=new FirestoreRecyclerOptions.Builder<FirebaseModel>().setQuery(query,FirebaseModel.class).build();

        chat_Adapter= new FirestoreRecyclerAdapter<FirebaseModel, NoteViewHolder>(all_user_name) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull FirebaseModel firebaseModel) {
                noteViewHolder.particular_user_name.setText(firebaseModel.getName());
                String uri=firebaseModel.getImage();
                Picasso.get().load(uri).into(user_image);
                noteViewHolder.view_status.setTextSize(20);
                if (firebaseModel.getStatus().equals("Online"))
                {
                    noteViewHolder.view_status.setText(firebaseModel.getStatus());
                    noteViewHolder.view_status.setTextColor(Color.GREEN);
                }else {
                    noteViewHolder.view_status.setText(firebaseModel.getStatus());
                    noteViewHolder.view_status.setTextColor(Color.parseColor("#454545"));
                }

                noteViewHolder.itemView.setOnClickListener(v->{
                    Intent intent =new Intent(getActivity(),PrivateChat.class);
                    intent.putExtra("name",firebaseModel.getName());
                    intent.putExtra("receiverUID",firebaseModel.getUid());
                    intent.putExtra("imageURL",firebaseModel.getImage());
                    intent.putExtra("status",firebaseModel.getStatus());
                    startActivity(intent);
                });
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_view_layout,parent,false);
                return new NoteViewHolder(v);
            }
        };

        recyclerView.setHasFixedSize(true);
        linearLayoutManager= new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chat_Adapter);


        return view;

    }
    public class NoteViewHolder extends RecyclerView.ViewHolder{

        private TextView particular_user_name;
        private TextView view_status;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            particular_user_name=itemView.findViewById(R.id.name_of_user);
            view_status=itemView.findViewById(R.id.status_of_user);
            user_image=itemView.findViewById(R.id.image_view_of_user);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        chat_Adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (chat_Adapter!=null)
        {
            chat_Adapter.stopListening();
        }
    }
}
