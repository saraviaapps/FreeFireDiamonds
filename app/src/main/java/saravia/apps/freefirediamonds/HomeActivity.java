package saravia.apps.freefirediamonds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TableLayout;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    private com.google.android.material.tabs.TabLayout tableLayout;
    private TabItem GLOBAL,CHAT;
    private ViewPager viewPager;
    private PageAdapter adapter;
    androidx.appcompat.widget.Toolbar toolbar;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar=findViewById(R.id.toolbar);
        viewPager=findViewById(R.id.fragment_container);
        tableLayout=findViewById(R.id.included);
        GLOBAL=findViewById(R.id.global_chat);
        CHAT=findViewById(R.id.personal_chat);
        firebaseFirestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        setSupportActionBar(toolbar);
        adapter=new PageAdapter(getSupportFragmentManager(),tableLayout.getTabCount());
        viewPager.setAdapter(adapter);
        tableLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition()==0 || tab.getPosition()==1){
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tableLayout));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.menu_app,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.profile:
                Intent intent=new Intent(HomeActivity.this,ProfileActivity.class);
                startActivity(intent);
                break;
        }
        return true;
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