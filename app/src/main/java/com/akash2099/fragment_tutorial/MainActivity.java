package com.akash2099.fragment_tutorial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    // Frame layout and Fragments
    Button b1,b2;
//    Fragment_1 first_frag;
//    Fragment_2 second_frag;
//    Fragment_2 third_frag;
//    Fragment_2 forth_frag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        b1=(Button)findViewById(R.id.button1);
//        b2=(Button)findViewById(R.id.button2);

//        first_frag=new Fragment_1();
//        second_frag=new Fragment_2();
//        third_frag=new Fragment_3();
//        forth_frag=new Fragment_4();

        // setting the first frame

        getSupportFragmentManager().beginTransaction().replace(R.id.myFrameLayout,Fragment_1.createFor("By Default Home",1)).commit();

        // connecting with the BottomNavigationView
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener); // to do the code outside oncreate

//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                return false;
//            }
//        });
    }

    // accessing the BottomNavigationView outside oncreate
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment=null;
                    switch (menuItem.getItemId()){
                        case R.id.nav_home:
                            // For sending data to fragments from activity
                            selectedFragment=Fragment_1.createFor("Send Data from activity to fragment",1);
//                            selectedFragment=new Fragment_1();
                            break;
                        case R.id.nav_favourites:
                            selectedFragment=new Fragment_2();
                            break;
                        case R.id.nav_search:
                            selectedFragment=new Fragment_3();
                            break;
                        case R.id.nav_history:
                            selectedFragment=new Fragment_4();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.myFrameLayout,selectedFragment).commit();
                    return true;
                }
            };

//    public void button1_onclick(View view) {
//        getSupportFragmentManager().beginTransaction().replace(R.id.myFrameLayout,first_frag).commit();
//    }
//
//    public void button2_onclick(View view) {
//        getSupportFragmentManager().beginTransaction().replace(R.id.myFrameLayout,second_frag).commit();
//    }
}
