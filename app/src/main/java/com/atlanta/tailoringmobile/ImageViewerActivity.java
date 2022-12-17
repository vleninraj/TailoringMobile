package com.atlanta.tailoringmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class ImageViewerActivity extends AppCompatActivity {

    ImageView imgmain;
    Button btnselectimage,btncamara,btnclear,btnokimage,btncancelimage;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mroot=firebaseDatabase.getReference();
    DatabaseReference rootReference;
    String sOrderNo;
    String sCurrentUserID;
    String sCurrentDate;
    String sDtlID;
    int SELECT_PICTURE = 200;
    String sImage="";
    Uri sfilePath;
    FirebaseStorage storage;
    StorageReference storageReference;
    private static final int CAMERA_REQUEST_CODE = 1 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        imgmain=findViewById(R.id.imgmain);
        btncamara=findViewById(R.id.btncamara);
        btnclear=findViewById(R.id.btnclear);
        btnselectimage=findViewById(R.id.btnselectimage);
        btnokimage=findViewById(R.id.btnokimage);
        btncancelimage=findViewById(R.id.btncancelimage);
        final SharedPreferences shApi =getApplicationContext().getSharedPreferences("Settings",MODE_PRIVATE);
        Common.sConnectionID=shApi.getString("ConnectionID","");
        final Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        sCurrentUserID =bd.getString("LoginUserID","");
        if(sCurrentUserID.equals(""))
        {
            Toast.makeText(getApplicationContext(),"Login user can't be blank!",Toast.LENGTH_LONG).show();
            finish();
        }
        sOrderNo= bd.getString("OrderNo");
        rootReference=mroot.child("Tailoring").child(Common.sConnectionID);
        sCurrentDate=bd.getString("CurrentDate");
        sDtlID=bd.getString("DTLID");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        if(sDtlID!="")
        {

            storageReference.child("FabricImages/" + sOrderNo + "/" + sDtlID + "/Image1").getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    // Use the bytes to display the image

                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imgmain.setImageBitmap(Bitmap.createScaledBitmap(bmp, imgmain.getWidth(), imgmain.getHeight(), false));

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });

        }

        btnselectimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });
        btncamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageFromCamara();
            }
        });

        btnokimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(imgmain.getDrawable()==null)
                {
                    Toast.makeText(getApplicationContext(),"Image must be selected!",Toast.LENGTH_LONG).show();
                    return;
                }
                BitmapDrawable drawable = (BitmapDrawable) imgmain.getDrawable();
                Bitmap bmap = drawable.getBitmap();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bmap.compress(Bitmap.CompressFormat.PNG,100,bos);
                byte[] bb = bos.toByteArray();
                sImage = Base64.getEncoder().encodeToString(bb);

                if(sDtlID!="")
                {
                    uploadImage();
                    Toast.makeText(getApplicationContext(), "Image Saved!",Toast.LENGTH_LONG).show();

                }

            }
        });
        btncancelimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgmain.setImageDrawable(null);

            }
        });



    }
    private void uploadImage()
    {
        if (sfilePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child("FabricImages/" + sOrderNo + "/" + sDtlID + "/Image1");

            // adding listeners on upload
            // or failure of image
            ref.putFile(sfilePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    Uri downloadUri = taskSnapshot.getUploadSessionUri();

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(ImageViewerActivity.this,
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(ImageViewerActivity.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });
        }
    }
   public void imageChooser()
   {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
         startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }
    public  void imageFromCamara()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE || requestCode==CAMERA_REQUEST_CODE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                sfilePath=selectedImageUri;
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    imgmain.setImageURI(selectedImageUri);
                }
                else
                {
                    if (data != null && data.getExtras() != null) {
                        Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                        imgmain.setImageBitmap(imageBitmap);
                    }
                }
            }

        }
    }
}