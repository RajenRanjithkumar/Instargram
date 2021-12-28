package com.restapi.insta;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    private ImageView close, image_added;
    private TextView post;
    SocialAutoCompleteTextView description;

    private Uri imageUri;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        close = findViewById(R.id.closebt);
        image_added = findViewById(R.id.addImage);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PostActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //open Image cropper
        CropImage.activity().start(PostActivity.this);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                upload();

            }
        });


    }

    private void upload() {

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading image...");
        pd.show();

        if (imageUri != null){

            StorageReference filePath = FirebaseStorage.getInstance().getReference("Posts").child(System.currentTimeMillis()+"."+getFileExtension(imageUri));

            StorageTask uploadtask = filePath.putFile(imageUri);
            uploadtask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }

                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    Uri downloadUri = task.getResult();
                    imageUrl = downloadUri.toString();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                    String postId = ref.push().getKey();

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("postid", postId);
                    map.put("ImageUrl", imageUrl);
                    map.put("description", description.getText().toString());
                    map.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

                    ref.child(postId).setValue(map);

                    DatabaseReference mHashtagRef = FirebaseDatabase.getInstance().getReference().child("HashTags");
                    List<String> hasTags = description.getHashtags();
                    if (!hasTags.isEmpty()){

                        for (String tag: hasTags){
                            map.clear();
                            map.put("tag", tag.toLowerCase());
                            map.put("postid", postId);
                            mHashtagRef.child(tag.toLowerCase()).child(postId).setValue(map);
                        }

                    }
                    pd.dismiss();
                    Intent intent = new Intent(PostActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(PostActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, "Post Error:"+ e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });

        }else {

            Toast.makeText(PostActivity.this, "No image was selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {

        String extension;
        ContentResolver cR = getContentResolver();
        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(cR.getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            image_added.setImageURI(imageUri);

        }else {
            Toast.makeText(PostActivity.this, "Try again!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(PostActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        }
    }
}