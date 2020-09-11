package com.example.dianote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private Button recordDataButton, statisticsButton;

    DatabaseReference databaseReference;
    private DatabaseReference userRef;
    private StorageReference userPicRef;
    private FirebaseAuth mAuth;
    String currentUserId, downloadImageUrl;
    ProgressDialog progress;

    private ListView listView;
    private List<DataDetails> dataDetailsList;
    private CustomAdapter customAdapter;
    private ProgressBar progressBar;
    private ImageView home, post, profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        //currentUserId = mAuth.getCurrentUser().getUid(); // this line cause null object reference on first time installation
        //databaseReference = FirebaseDatabase.getInstance().getReference().child("UserData").child(currentUserId);

        toolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        dataDetailsList = new ArrayList<>();
        customAdapter= new CustomAdapter(MainActivity.this, dataDetailsList);

        navigationView = (NavigationView) findViewById(R.id.navigationViewID);  // declaration
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayoutId);
        progressBar = (ProgressBar) findViewById(R.id.progressBarId3);
        listView = (ListView) findViewById(R.id.listViewId);
        recordDataButton = (Button) findViewById(R.id.recordDataId);
        statisticsButton = (Button) findViewById(R.id.statisticsId);

        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close); // enable navigation toggle
        actionBarDrawerToggle.syncState();
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        recordDataButton.setOnClickListener(this);  // button listener
        statisticsButton.setOnClickListener(this);

        progressBar.setVisibility(View.VISIBLE); // visible progress bar

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                UserMenuSelect(menuItem);
                return false;
            }
        });
    }

    @Override  // this method is for to listen click on toggle icon
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onClick(View view) {
        if(view.getId()== R.id.recordDataId)
        {
            Intent intent = new Intent(MainActivity.this, RecordData.class);
            startActivity(intent);
        }
        else if(view.getId()==R.id.statisticsId)
        {
            Intent intent = new Intent(MainActivity.this, Statistics.class);
            startActivity(intent);
        }
    }

    protected void onStart() {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null)   // if user not signed in
        {
            sendUserToLoginActivity();
        }
        else
        {
            currentUserId = mAuth.getCurrentUser().getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("UserData").child(currentUserId);
            databaseReference.addValueEventListener(new ValueEventListener() {  // fetching data from firebase and add to listView
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    dataDetailsList.clear();   // clear array list
                    Log.e("Count " ,""+snapshot.getChildrenCount());  // get all sub children from children
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        DataDetails dataDetails = dataSnapshot.getValue(DataDetails.class);
                        dataDetailsList.add(dataDetails);
                    }
                    Collections.reverse(dataDetailsList);
                    listView.setAdapter(customAdapter);
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        super.onStart();
    }

    public void sendUserToLoginActivity()
    {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);   // go to login activity to sign in
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    public void UserMenuSelect(MenuItem item)
    {
        if(item.getItemId() == R.id.logoutItemId)  // logout option
        {
            mAuth.signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}