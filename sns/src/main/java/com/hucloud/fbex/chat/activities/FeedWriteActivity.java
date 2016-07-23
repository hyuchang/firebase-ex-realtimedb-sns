package com.hucloud.fbex.chat.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hucloud.fbex.chat.R;
import com.hucloud.fbex.chat.firebase.FirebaseWrapp;
import com.hucloud.fbex.chat.model.Feed;

import java.util.ArrayList;
import java.util.Date;

/**
 * 해당 파일은 소유권은 신휴창에게 있습니다.
 * 현재 오픈 소스로 공개중인 버전은 AGPL을 따르는 오픈 소스 프로젝트이며,
 * 소스 코드를 수정하셔서 사용하는 경우에는 반드시 동일한 라이센스로 소스 코드를 공개하여야 합니다.
 * 만약 HUCLOUD를 상업적으로 이용하실 경우에는 라이센스를 구매하여 사용하셔야 합니다.
 * email : huttchang@gmail.com
 * 프로젝트명    : fbex
 * 작성 및 소유자 : hucloud
 * 최초 생성일   : 2016. 7. 20.
 */
public class FeedWriteActivity extends AppCompatActivity {

    private LinearLayout topLayout;

    private EditText text;

    private HorizontalScrollView photoArea;

    private Toast mToast;
    private FirebaseDatabase dbInstance;
    private DatabaseReference mDbRef;
    private StorageReference mStorageRef;
    private FirebaseStorage storageInstance;


    private ArrayList<String> previewPhotos = new ArrayList<String>();
    private ArrayList<String> uploadedPhotoUrls = new ArrayList<String>();
    private ArrayList<Uri> uploadPhotoUris = new ArrayList<Uri>();

    private int photoIndex = 0;

    private ProgressDialog mProgressdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_write);
        setView();
        dbInstance = FirebaseWrapp.getDbInstance();
        mDbRef = dbInstance.getReference("feeds");
        storageInstance = FirebaseWrapp.getStorageInstance();
        mStorageRef = storageInstance.getReference("feeds");
    }

    private void setView(){
        topLayout = (LinearLayout) findViewById(R.id.toplayout);
        text = (EditText) findViewById(R.id.text);
        photoArea = (HorizontalScrollView) findViewById(R.id.photo_area);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feed_write, menu);
        return true;
    }
    public void confirm(Context context, String message, DialogInterface.OnClickListener ok){
        new AlertDialog.Builder(context)
            .setMessage(message)
            .setPositiveButton("확인", ok)
            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            })
            .create()
            .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ( item.getItemId() != R.id.createFeed )
            return false;
        final Feed feed = new Feed();
        if ( text.getText().toString().trim().isEmpty() && previewPhotos.isEmpty() ) {
            Toast.makeText(this, "사진이나 글을 등록해주세요.", Toast.LENGTH_LONG).show();
            return false;
        }
        feed.setText(text.getText().toString());
        feed.setPhotos(uploadedPhotoUrls);
        feed.setRegdate(new Date());
        feed.setUserName(FirebaseWrapp.getAuthInstance().getCurrentUser().getEmail().split("@")[0]);
        feed.setUserId(FirebaseWrapp.getAuthInstance().getCurrentUser().getUid());
        if ( FirebaseWrapp.getAuthInstance().getCurrentUser().getPhotoUrl() != null ) {
            feed.setUserPhoto(FirebaseWrapp.getAuthInstance().getCurrentUser().getPhotoUrl().toString());
        }

        confirm(this, "피드를 등록하시겠습니까?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createFeed(feed);
            }
        });
        return super.onOptionsItemSelected(item);
    }

    protected void toast(String txt) {
        if (mToast != null)
            mToast.cancel();
        mToast = Toast.makeText(this, txt, Toast.LENGTH_SHORT);
        mToast.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(1, 1000, 1, "카메라");
        menu.add(1, 2000, 2, "사진");
        menu.add(1, 3000, 3, "취소");
    }

    public void createFeed(Feed feed){
        // todo 등록 로직 기술 ~~!
        showProgress("피드를 등록하는 중입니다.");
        DatabaseReference ref = mDbRef.push();
        feed.setFeedId(ref.getKey());
        ref.setPriority(System.currentTimeMillis());
        ref.setValue(feed).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    toast("피드등록이 완료되었습니다.");
                    finish();
                } else {
                    toast("피드 등록에 실패하였습니다. 다시 등록해주세요.");
                }
                dismissProgress();
            }
        });
    }

    private void photoUpload(){
        showProgress("이미지를 업로드 하는 중입니다.");
        StorageReference ref;
        for ( Uri photoUri : uploadPhotoUris) {
            ref = mStorageRef.child(photoUri.getLastPathSegment());
            ref.putFile(photoUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    uploadedPhotoUrls.add(taskSnapshot.getDownloadUrl().toString());
                    System.out.println( "photo Url : " + taskSnapshot.getDownloadUrl().toString() );
                    dismissProgress();
                }
            });
        }

    }

    public void attachBtnClick(View v) {
        Intent attachIntent = new Intent();
        int limit = previewPhotos.size();

        if ( limit > 4 ) {
            toast("첨부 파일 및 이미지는 5개 까지 첨부할 수 있습니다.");
            return;
        }

        if ( v.getId() == R.id.camerabutton ) {
            attachIntent.setAction(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(attachIntent, 1000);

        } else if ( v.getId() == R.id.photo ) {
            attachIntent.setType("image/*");
            attachIntent.setAction(Intent.ACTION_GET_CONTENT);
            attachIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(attachIntent, "사진선택"), 2000);
        }
    }

    public Bitmap getThumbnailBitmap(String path) {
        Cursor cursor =
                this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.MediaColumns._ID }, MediaStore.MediaColumns.DATA + "=?",new String[] { path }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return MediaStore.Images.Thumbnails.getThumbnail(this.getContentResolver(), id, MediaStore.Images.Thumbnails.MINI_KIND, null);
        }
        cursor.close();
        return null;
    }

    private void previewMiniPhoto(String path) {
        int [] imageViewIndexes = {
                R.id.iv1,R.id.iv2,R.id.iv3,
                R.id.iv4,R.id.iv5
        };
        try{
            ImageView previewimageView = (ImageView) findViewById(imageViewIndexes[photoIndex]);
            previewimageView.setTag(photoIndex);
            Drawable miniphoto = new BitmapDrawable( getResources() ,getThumbnailBitmap(path));
            previewimageView.setImageDrawable(miniphoto);
            photoIndex++;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        Uri selectedFile = data.getData();
        switch (requestCode) {
            case 2000: {
                try {
                    ClipData clipData = data.getClipData();
                    int selectedItemCount = clipData.getItemCount();
                    for (int i = 0; i < selectedItemCount; i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        addPhoto(item.getUri(), 2000);
                    }
                } catch (Exception e) {
                    addPhoto(selectedFile, 2000);
                }
                break;
            }
            case 1000: {
                addPhoto(getLstCaptureImageUri(), 1000);
                break;
            }
        }
        photoUpload();
    }

    public Uri getLstCaptureImageUri() {
        Uri uri = null;
        String[] IMAGE_PROJECTION = {MediaStore.Images.ImageColumns.DATA};
        Cursor cursor = this.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                null, null, null);
        if (cursor != null && cursor.moveToLast()) {
            uri = Uri.parse(cursor.getString(0));
            mUploadFilePath = cursor.getString(0);
            cursor.close();
        }
        return uri;
    }

    String mUploadFilePath;
    protected void addPhoto(Uri selectedFile,int callType) {
        if ( callType != 1000) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedFile,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            mUploadFilePath = cursor.getString(columnIndex);
            cursor.close();
        }
        uploadPhotoUris.add(selectedFile);
        previewPhotos.add(mUploadFilePath);
        if ( previewPhotos.size() > 0 ) {
            photoArea.setVisibility(View.VISIBLE);
        } else {
            photoArea.setVisibility(View.GONE);
        }
        previewMiniPhoto(mUploadFilePath);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if ( savedInstanceState == null )
            return;
        mUploadFilePath = savedInstanceState.getString("image");
        addPhoto(null, 1000);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if ( outState == null )
            return;
        outState.putString("image", mUploadFilePath);
        super.onSaveInstanceState(outState);
    }

    /**
     * 첨부된 파일 또는 이미지 클릭 시
     * @param view
     */
    public void imageClick(final View view) {
        if ( view.getTag() == null ) {
            attachBtnClick(view);
        } else {
            Snackbar.make(topLayout, "이미지를 삭제하시겠습니까?", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void showProgress(String loadingTxt){
        if ( mProgressdialog != null
                && mProgressdialog.isShowing() ) {
                mProgressdialog.dismiss();
        }
        mProgressdialog = new ProgressDialog(FeedWriteActivity.this);
        mProgressdialog.setCancelable(false);
        mProgressdialog.setCanceledOnTouchOutside(false);
        mProgressdialog.setMessage(loadingTxt);
        mProgressdialog.show();
    }
    private void dismissProgress(){
        if ( mProgressdialog != null ) {
            mProgressdialog.dismiss();
        }
    }

}
