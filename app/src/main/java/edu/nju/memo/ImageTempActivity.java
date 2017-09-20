package edu.nju.memo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Set;

public class ImageTempActivity extends Activity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_temp);

        Uri image = getIntent().getExtras().getParcelable(Intent.EXTRA_STREAM);
        imageView = (ImageView) findViewById(R.id.image_view);

        grantUriPermission("edu.nju.memo", image, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            InputStream is = getContentResolver().openInputStream(image);
            imageView.setImageBitmap(BitmapFactory.decodeStream(is));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
