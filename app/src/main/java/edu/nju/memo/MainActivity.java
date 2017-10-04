package edu.nju.memo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Button;

import edu.nju.memo.activities.MemoDetailActivity;
import edu.nju.memo.dao.CachedMemoDao;
import edu.nju.memo.domain.Memo;
import edu.nju.memo.service.StartFloatBallService;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

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
    }
}
