package com.audsnap.workingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.audsnap.R;
import com.edmodo.cropper.CropImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView mUsername;
    private Uri imagePath;
    private ProgressDialog progressDialog;
    private String startActivityImagePath;
    private final int PICK_IMAGE_CODE=100;
    private StorageReference storageRef,imageRef;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsernameReference;
    private File profileImg;
    private String uid,pathOfProfilePic;
    private Bitmap croppedImage,profileImage;
    boolean isClicked =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");
        imageView=(ImageView) findViewById(R.id.profileimage);
        mUsername=(TextView) findViewById(R.id.t_username);

        //set the username in SQLite database

        uid=FirebaseAuth.getInstance().getCurrentUser().getUid();

        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mUsernameReference=mFirebaseDatabase.getReference("/AudSnap/UserNames/"+uid);


        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://project-527458073545831192.appspot.com/");
        imageRef=storageRef.child("AudSnap/ProfilePictures/"+ uid+".jpg");
        getImagePathAndSetImageView();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* Bundle b = new Bundle();
                b.putByteArray("picture",profileImage.getNinePatchChunk());
                Intent intent= new Intent(ProfileActivity.this,FullImageviewActivity.class);
                intent.putExtras(b);
                startActivity(intent);*/

            }
        });

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButtonClickDialog();
            }
        });



    }

    private void imageButtonClickDialog()
    {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.imagebuttonclicklayout,(ViewGroup) findViewById(R.id.root_layout));
        ImageButton changeImage=(ImageButton) v.findViewById(R.id.changeImage);
        ImageButton removeImage=(ImageButton) v.findViewById(R.id.removeImage);
        alertDialog.setView(v);
        final AlertDialog alertDialogBox = alertDialog.create();

        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Choose Picture"),PICK_IMAGE_CODE);
                alertDialogBox.cancel();
            }
        });
        removeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File setProfileImgOnStart = new File(Environment.getExternalStorageDirectory() +
                        "/AudSnap/ProfilePicture/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+".jpg");
                boolean isDeleted=setProfileImgOnStart.delete();
                if(isDeleted)
                {
                    getImagePathAndSetImageView();
                }
                else
                {
                    Toast.makeText(getBaseContext(),R.string.error_removing_picture,Toast.LENGTH_SHORT).show();
                }
                alertDialogBox.cancel();

            }
        });

        alertDialogBox.show();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_CODE) {

                imagePath = data.getData();
                Log.e("PICTUREPATH", getImagePathFromUri(imagePath));
                croppedImage(getImagePathFromUri(imagePath));

            }

        }
    }

    private String getImagePathFromUri(Uri uri){
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }


    private void getImagePathAndSetImageView()
    {
       /* File setProfileImgOnStart = new File(Environment.getExternalStorageDirectory() +
                "/AudSnap/ProfilePicture/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+".jpg");
        if(setProfileImgOnStart.exists())
        {
            startActivityImagePath=setProfileImgOnStart.getAbsolutePath();
            Bitmap bitmap = BitmapFactory.decodeFile(startActivityImagePath);
            imageView.setImageBitmap(bitmap);
        }
        else
        {
            startActivityImagePath="NO IMAGE";
            imageView.setImageResource(R.drawable.default_avatar);
        }*/
        showLoading("Loading...");
        mUsernameReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                stopLoading();
                mUsername.setText("Hello "+dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                stopLoading();
                Toast.makeText(getBaseContext(),"Error retriving username",Toast.LENGTH_SHORT).show();
            }
        });

        imageRef.getDownloadUrl().addOnSuccessListener(ProfileActivity.this, new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(final Uri uri) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*final RequestCreator requestCreator = */
                        Picasso.with(ProfileActivity.this).load(uri).into(imageView);
                        profileImage=imageView.getDrawingCache();
                     /*   requestCreator.into(imageView);
                        try {
                            profileImage=requestCreator.get();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/
                    }
                });
            }
        }).addOnFailureListener(ProfileActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                stopLoading();
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.default_avatar));
                //Toast.makeText(ProfileActivity.this, "", Toast.LENGTH_LONG).show();
            }
        });

    }


    void showLoading()
    {
        progressDialog=new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Updating...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

    }

    void showLoading(String message)
    {
        progressDialog=new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

    }
    void stopLoading()
    {
        progressDialog.dismiss();
    }

    private void croppedImage(String path)
    {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater layoutInflater = getLayoutInflater();

        View v = layoutInflater.inflate(R.layout.content_crop_image,(ViewGroup) findViewById(R.id.root_layout));

        final CropImageView cropImageView = (CropImageView) v.findViewById(R.id.CropImageView);
        ImageButton imageButton =(ImageButton) v.findViewById(R.id.imageButton);

        cropImageView.setImageBitmap(BitmapFactory.decodeFile(path));

        cropImageView.setAspectRatio(1,1);
        cropImageView.setFixedAspectRatio(true);
        builder.setView(v);
        final AlertDialog alertDialog = builder.create();

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                croppedImage=cropImageView.getCroppedImage();
                isClicked=true;
                imageView.setImageBitmap(croppedImage);
                uploadImage();
                alertDialog.dismiss();

            }
        });
        alertDialog.show();
    }

    private void uploadImage()
    {
        showLoading();
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap=imageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] dataimage = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(dataimage);
        uploadTask.addOnSuccessListener(ProfileActivity.this,
                new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       /* profileImg = new File(Environment.getExternalStorageDirectory() + "/AudSnap/ProfilePicture/");
                        if(!profileImg.exists())
                        {
                            boolean b = profileImg.mkdir();
                        }
                        try {
                            File saveProfileImg = new File(profileImg, uid + ".jpg");
                            FileOutputStream fos = new FileOutputStream(saveProfileImg);
                            Bitmap bit = imageView.getDrawingCache();
                            bit.compress(Bitmap.CompressFormat.JPEG, 50, fos);
                            fos.flush();
                            fos.close();
                            getImagePathAndSetImageView();
                            stopLoading();
                            Toast.makeText(ProfileActivity.this, R.string.msg_profile_picture_changed, Toast.LENGTH_LONG).show();
                        }catch (Exception e)
                        {
                            File saveProfileImg = new File(profileImg, uid + ".jpg");
                            Uri uri=Uri.fromFile(saveProfileImg);
                            Toast.makeText(getBaseContext(),"Failed to change profile picture",Toast.LENGTH_SHORT).show();
                            imageView.setImageURI(uri);
                        }*/
                        stopLoading();
                        imageRef.getDownloadUrl().addOnSuccessListener(ProfileActivity.this, new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.with(ProfileActivity.this).load(uri).into(imageView);
                                Toast.makeText(ProfileActivity.this, R.string.msg_profile_picture_changed, Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }).addOnFailureListener(ProfileActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                stopLoading();
            }
        });


    }
}