package creativemind.think.thinkcreativemind;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import creativemind.think.thinkcreativemind.filebrowser.AndroidPermissionHelper;
import creativemind.think.thinkcreativemind.filebrowser.FileexplorerActivity;
import creativemind.think.thinkcreativemind.util.AppSettings;
import creativemind.think.thinkcreativemind.util.CreativeUtil;
import creativemind.think.thinkcreativemind.util.ImageRecogApp;
import creativemind.think.thinkcreativemind.util.PathUtil;


public class TextFromImage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    AndroidPermissionHelper mPermissionHelper;
    ImageView image_selected;
    TextView textView_imageScanText;
    Button start_Scan;
    FloatingActionButton floatingActionButton;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppSettings.setAppPathInPutOutput("");
        ImageRecogApp.getInstance().setFolderBrowse(false);
        mPermissionHelper = new AndroidPermissionHelper(this);
        mPermissionHelper.checkReadWriteExternalPermission();
        Toolbar toolbar = findViewById(R.id.toolbar);
        image_selected = findViewById(R.id.imageView_select);
        start_Scan = findViewById(R.id.button_startSan);
        start_Scan.setOnClickListener((View.OnClickListener) this);
        textView_imageScanText = findViewById(R.id.textView_imageScanText);
        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(this);
        textView_imageScanText.setMovementMethod(new ScrollingMovementMethod());
        setImageFromSelectedPath();
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            cameraIntent();
        } else if (id == R.id.nav_gallery) {
            galleryIntent();

         /*
            Intent intent1 = new Intent(this, FileexplorerActivity.class);
            startActivity(intent1);*/

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

            if (textView_imageScanText.getText().toString().trim().length() > 0) {
                chooseIntent();
            } else {
                Toast.makeText(this, "No image scan text to send!!!", Toast.LENGTH_LONG).show();
            }


        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setImageFromSelectedPath() {
        try {

            if (AppSettings.getInputPath().trim().length() > 0) {
                image_selected.setImageBitmap(CreativeUtil.getBitMapFromFile(AppSettings.getInputPath()));
            }
            //startScanImage();
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public void startScanImage() {
        textView_imageScanText.setText("");

        StringBuilder sb = new StringBuilder();
        String outPut = CreativeUtil.startScanImageForExtractText(CreativeUtil.getBitMapFromFile(AppSettings.getInputPath()));
        sb.append(outPut);
        textView_imageScanText.setText(sb.toString());
    }

    public Bitmap getBitmapFromAssets(String fileName) {
        AssetManager assetManager = getAssets();

        InputStream istr = null;
        try {
            istr = assetManager.open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(istr);

        return bitmap;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setImageFromSelectedPath();
    }

    @Override
    protected void onPause() {
        super.onPause();
        setImageFromSelectedPath();
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.button_startSan:
                if(AppSettings.getInputPath().trim().length()>0) {
                    startScanImage();
                }else
                {
                    Toast.makeText(this, "No image selected for scan text!!!", Toast.LENGTH_LONG).show();

                }



                break;

            case R.id.fab:
                // Action

                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

        }

    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
            else if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo = null;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fo != null) {
                try {
                    fo.flush();
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        AppSettings.setInputPath(destination.getAbsolutePath().toString());
        image_selected.setImageBitmap(CreativeUtil.getBitMapFromFile(AppSettings.getInputPath()));

    }


    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        File imageFile = null;
        Uri url;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                url = (Uri) data.getData();
                imageFile = new File(PathUtil.getPath(this, url));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        AppSettings.setInputPath(imageFile.getAbsolutePath().toString());
        image_selected.setImageBitmap(CreativeUtil.getBitMapFromFile(AppSettings.getInputPath()));
        image_selected.setImageBitmap(bm);
    }

    public void chooseIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        // intent.setType( "message/rfc822");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, textView_imageScanText.getText().toString());
        intent.putExtra(Intent.EXTRA_SUBJECT, "Scan Image Data");

        startActivity(Intent.createChooser(intent, "Send mail..."));
    }
}
