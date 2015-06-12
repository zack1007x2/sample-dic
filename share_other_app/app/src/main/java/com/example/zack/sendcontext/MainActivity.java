package com.example.zack.sendcontext;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;


public class MainActivity extends Activity {
    private static int RESULT_LOAD_IMG = 1;
    private static int RESULT_LOAD_VIDEO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        switch (id) {
            case R.id.action_text:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "國父你好");
                sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "你好");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            case R.id.action_img:
                Intent galleryIntent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
//                Intent shareIntent = new Intent();
//                shareIntent.setAction(Intent.ACTION_SEND);
//                shareIntent.putExtra(Intent.EXTRA_STREAM,  (Serializable) dataList.get(position).imageList);
//                shareIntent.setType("image/jpeg");
//                startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.app_name)));
                break;
            case R.id.action_video:

                Intent vIntent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(vIntent, RESULT_LOAD_VIDEO);
                break;
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {

                Log.d("Zac", "onActivityResult");

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgPath = cursor.getString(columnIndex);
                cursor.close();
                File path = new File(imgPath);
                // Set the Image in ImageView
                Uri bmpUri = Uri.fromFile(path);


                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "分享我的相片");
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                shareIntent.setType("image/*");
                startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.app_name)));

            }
            else if(requestCode == RESULT_LOAD_VIDEO && resultCode == RESULT_OK
                    && null != data){
                Log.d("Zac", "VIDEO");

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgPath = cursor.getString(columnIndex);
                cursor.close();
                File path = new File(imgPath);
                // Set the Image in ImageView
                Uri bmpUri = Uri.fromFile(path);
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "分享我的影片");
                sharingIntent.putExtra(android.content.Intent.EXTRA_STREAM, bmpUri);
                sharingIntent.setType("video/*");
                startActivity(Intent.createChooser(sharingIntent, getResources().getText(R.string.app_name)));
            }
        } catch (Exception e) {
            Log.d("Zac", "Exception");
        }
    }
}
