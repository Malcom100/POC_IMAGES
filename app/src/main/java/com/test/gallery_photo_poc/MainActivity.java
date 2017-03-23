package com.test.gallery_photo_poc;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Locale;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private Button btn_picture;
    private String selectedPath;
    private int SELECT_PICTURE = 5;
    private int REQUEST_IMAGE_CAPTURE = 7;
    private Dialog dialog;
    private Bitmap imageBitmap;
    private Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        btn_picture = (Button) findViewById(R.id.btn);
        btn_picture.setOnClickListener(this);
                       /*
                // Create intent to Open Image applications like Gallery, Google Photos
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
                */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private void createDialog(){
        dialog = new Dialog(this);
        //with no title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_alert_dialog);

        LinearLayout layoutTakePhoto = (LinearLayout)dialog.findViewById(R.id.layout_take_photo);
        LinearLayout layoutPickFile = (LinearLayout)dialog.findViewById(R.id.layout_pick_file);

        layoutPickFile.setOnClickListener(this);
        layoutTakePhoto.setOnClickListener(this);


        dialog.show();
    }

    private void takePhoto(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn:
                createDialog();
                break;
            case R.id.layout_pick_file:
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
                break;
            case R.id.layout_take_photo:
                dialog.dismiss();
                takePhoto();
                break;
        }
    }

    private String getPath(Uri uri){
        // just some safety built in
        if( uri == null ) {
            Log.i("Test","there is not Uri "+uri);
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        // this is our fallback here
        return uri.getPath();
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException{
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == SELECT_PICTURE){
                Uri selectedImageUri = data.getData();
                selectedPath = getPath(selectedImageUri);
                try {
                    Log.i("Adneom", "Bitmap is " + getBitmapFromUri(selectedImageUri));
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("E","error is "+e.getMessage());
                }
            }
        }

        if(resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE){
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            Log.i("Adneom", "bitmap picture is " + imageBitmap);
        }
        if(imageBitmap != null || image != null){
            transformInFile();
        }
        manageLanguage();
    }


    private void transformInFile(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if(imageBitmap != null){
            imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        }else{
            image.compress(Bitmap.CompressFormat.JPEG,100,baos);
        }
        byte[] b = baos.toByteArray();
        Log.i("Adneom",b.length+" ");

        //String encodeImage = Base64.encodeToString(b,Base64.DEFAULT);
    }


    private void manageLanguage(){
        String languageToLoad = "en";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration conf = new Configuration();
        conf.locale = locale;
        getBaseContext().getResources().updateConfiguration(conf,getBaseContext().getResources().getDisplayMetrics());
    }

}
