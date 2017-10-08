package edu.nju.memo;

import android.content.ClipData;
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

import com.android.internal.util.Predicate;
import com.bumptech.glide.Glide;

import java.util.List;

import edu.nju.memo.core.MemoItemFactory;
import edu.nju.memo.core.parser.MemoItemFactoryImpl;
import edu.nju.memo.dao.CachedMemoDao;
import edu.nju.memo.dao.MemoDao;
import edu.nju.memo.domain.Attachment;
import edu.nju.memo.domain.Memo;
import edu.nju.memo.manager.ViewManager;

public class MemoPreview extends AppCompatActivity {

    public static final String FROM = "from";

    public static final String CONTENT = "content";

    public static final String CLIPDATA = "clipData";

    private DrawerLayout drawerLayout;

    private EditText editText;

    private TextView textView;

    private ImageView imageView;

    private Intent intent;

    private MemoDao memoDao;

    private MemoItemFactory memoItemFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_preview);

        intent = getIntent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        editText = (EditText) findViewById(R.id.memo_title);
        editText.setText("新建备忘录");
        textView = (TextView) findViewById(R.id.clipboard_data);
        imageView = (ImageView) findViewById(R.id.screen_shot);

        initNavigationView();
        initMemoContent();

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
                final String title = editText.getText().toString();
                List<Memo> memos = memoDao.select(new Predicate<Memo>() {
                    @Override
                    public boolean apply(Memo memo) {
                        return title.equals(memo.getMTitle());
                    }
                });
                // 存在此标题
                if (memos.size() > 0) {
                    Memo memo = memos.get(0);
                    String source = intent.getStringExtra(FROM);
                    // 复制
                    if (source != null && source.equals(ViewManager.TAG)) {
                        ClipData clipData = intent.getParcelableExtra(CLIPDATA);
                        List<Attachment> attachments = memoItemFactory.getAttachments(clipData);
                        memo.getMAttachments().addAll(attachments);
                        memoDao.update(memo);
                    }
                    // 截屏
                    else {
                        List<Attachment> attachments = memoItemFactory.getAttachments(intent);
                        memo.getMAttachments().addAll(attachments);
                        memoDao.update(memo);
                    }
                }
                // 不存在此标题
                else {
                    String source = intent.getStringExtra(FROM);
                    // 复制
                    if (source != null && source.equals(ViewManager.TAG)) {
                        ClipData clipData = intent.getParcelableExtra(CLIPDATA);
                        Memo memo = memoItemFactory.getMemoItem(clipData);
                        memo.setMTitle(title);
                        memoDao.insert(memo);
                    }
                    // 截屏
                    else {
                        Memo memo = memoItemFactory.getMemoItem(intent);
                        memo.setMTitle(title);
                        memoDao.insert(memo);
                    }
                }
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                finish();
                break;
            default:
        }
        return true;
    }


    /**
     * 初始化边栏
     */
    private void initNavigationView() {
        memoDao = CachedMemoDao.INSTANCE;
        memoItemFactory = MemoItemFactoryImpl.INSTANCE;
        List<String> titles = memoDao.selectAllTitles();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        for (String title : titles) {
            navigationView.getMenu().add(title);
        }

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
     */
    private void initMemoContent() {
        String source = intent.getStringExtra(FROM);
        if (source != null && source.equals(ViewManager.TAG)) {
            imageView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            textView.setText(intent.getCharSequenceExtra(CONTENT));
        }
        else {
            textView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            Uri image = intent.getExtras().getParcelable(Intent.EXTRA_STREAM);
            Glide.with(this).load(image).into(imageView);
        }
    }
}
