package edu.nju.memo;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import edu.nju.memo.manager.ViewManager;

public class MemoPreview extends AppCompatActivity {

    public static final String FROM = "from";

    public static final String CONTENT = "content";

    private DrawerLayout drawerLayout;

    private EditText editText;

    private TextView textView;

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_preview);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        editText = (EditText) findViewById(R.id.memo_title);
        textView = (TextView) findViewById(R.id.clipboard_data);
        imageView = (ImageView) findViewById(R.id.screen_shot);

        initNavigationView();
        initMemoContent(getIntent());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.backup:
                Toast.makeText(this, "You clicked BackUp", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.done:
                Toast.makeText(this, "You clicked Done", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
    }


    /**
     * 初始化边栏
     */
    private void initNavigationView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        // TODO 之后会替换成真实的标题项
        navigationView.getMenu().add("head_1");
        navigationView.getMenu().add("head_2");
        navigationView.getMenu().add("head_3");

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
                editText.setText(item.getTitle());
                return true;
            }
        });
    }

    /**
     * 初始化Memo的内容，如果是复制文字则初始化TextView，如果是截屏，则初始化ImageView
     * @param intent
     */
    private void initMemoContent(Intent intent) {
        String source = intent.getStringExtra(FROM);
        if (source != null && source.equals(ViewManager.TAG)) {
            imageView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            textView.setText(intent.getCharSequenceExtra(CONTENT));
        }
        else {
            textView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            Uri image = getIntent().getExtras().getParcelable(Intent.EXTRA_STREAM);
            Glide.with(this).load(image).into(imageView);
        }
    }
}
