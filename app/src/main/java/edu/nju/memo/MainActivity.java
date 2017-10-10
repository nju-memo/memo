package edu.nju.memo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import edu.nju.memo.activities.fragment.RecyclerViewFragment;



public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.main_layout, new RecyclerViewFragment()).commit();
    }

}
