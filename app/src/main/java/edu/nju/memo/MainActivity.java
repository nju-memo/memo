package edu.nju.memo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import edu.nju.memo.activities.MemoDetailActivity;
import edu.nju.memo.activities.fragment.RecyclerViewFragment;
import edu.nju.memo.dao.CachedMemoDao;
import edu.nju.memo.service.StartFloatBallService;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button_on);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, StartFloatBallService.class);
                startService(intent);
                finish();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MemoDetailActivity.class);
                intent.putExtra("memo", CachedMemoDao.INSTANCE.select(1));
                startActivity(intent);
            }
        });

        fragmentManager = this.getSupportFragmentManager();
        Button btn_list = (Button) findViewById(R.id.btn_list);
        btn_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().add(R.id.main_layout, new RecyclerViewFragment()).commit();
            }
        });
    }
}
