package saravia.apps.freefirediamonds;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class PrivateChat extends AppCompatActivity {

    private ImageButton back;
    private androidx.appcompat.widget.Toolbar toolbar;
    private RecyclerView recyclerView;
    private EditText get_message;
    private ImageView user_image,send_message;
    private TextView view_name;
    private Intent intent;

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

    }
}