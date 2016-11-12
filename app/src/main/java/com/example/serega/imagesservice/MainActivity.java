package com.example.serega.imagesservice;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends Activity {

    private static final String URL1 = "http://www.zwani.com/graphics/hello_funny/images/56467.jpg";
    private static final String URL2 = "http://chudetstvo.ru/uploads/posts/2015-02/1423491175_zhenskaya-logika2.jpg";
    private static final String URL3 = "http://www.pozdravleniya.biz/images/fotoprikoli_online_00219.jpg";
    private static final String IMAGE1_NAME = "image1.jpg";
    private static final String IMAGE2_NAME = "image2.jpg";
    private static final String IMAGE3_NAME = "image3.jpg";

    public static final String BROADCAST_ACTION = "com.example.serega.imagesservice";
    public static final String URL = "url";
    public static final String BITMAP_URL = "Bitmap URL";
    public static final String IMAGE_NAME = "image name";

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private BroadcastReceiver broadcastReceiver;
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView1 = (ImageView) findViewById(R.id.imageView1);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        imageView3 = (ImageView) findViewById(R.id.imageView3);
        findViewById(R.id.btn1_download).setOnClickListener(onClickListener);
        findViewById(R.id.btn2_download).setOnClickListener(onClickListener);
        findViewById(R.id.btn3_download).setOnClickListener(onClickListener);

        registerBroadcastReceiver();
        verifyStoragePermissions(this);
    }

    private void registerBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String imageName = intent.getStringExtra(IMAGE_NAME);
                String path = intent.getStringExtra(BITMAP_URL);
                Bitmap bitmap = BitmapFactory.decodeFile(path);

                switch (imageName) {
                    case IMAGE1_NAME:
                        imageView1.setImageBitmap(bitmap);
                        break;
                    case IMAGE2_NAME:
                        imageView2.setImageBitmap(bitmap);
                        break;
                    case IMAGE3_NAME:
                        imageView3.setImageBitmap(bitmap);
                        break;

                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(MainActivity.this, DownloadService.class);

            switch (v.getId()) {
                case R.id.btn1_download:
                    intent.putExtra(URL, URL1);
                    intent.putExtra(IMAGE_NAME, IMAGE1_NAME);
                    break;
                case R.id.btn2_download:
                    intent.putExtra(URL, URL2);
                    intent.putExtra(IMAGE_NAME, IMAGE2_NAME);
                    break;
                case R.id.btn3_download:
                    intent.putExtra(URL, URL3);
                    intent.putExtra(IMAGE_NAME, IMAGE3_NAME);
                    break;
            }
            startService(intent);
        }
    };

    public static void verifyStoragePermissions(Activity activity) {

        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}
