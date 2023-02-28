package app.personal.fury.UI.Drawer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;

import app.personal.MVVM.Entity.userEntity;
import app.personal.MVVM.Viewmodel.LoggedInUserViewModel;
import app.personal.fury.R;

public class Settings_Activity extends AppCompatActivity {

    private LoggedInUserViewModel userVM;
    private ImageButton uploadPic;
    private ImageView profilePic;
    private TextView profileName;
    private EditText profileNameEdit;

    private userEntity userData = new userEntity();
    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity_settings);
        userVM = new ViewModelProvider(this).get(LoggedInUserViewModel.class);
        init();
    }

    private void init() {
        findView();
        getUserData();
        OnClick();
    }

    private void getUserData() {
        userVM.getUserData().observe(this, userEntity -> {
            if (userEntity != null) {
                userData = userEntity;
                profileName.setText(userData.getName());
                Glide.with(this)
                        .load(userData.getImgUrl())
                        .circleCrop()
                        .into(profilePic);
            }
        });
    }

    private void findView() {
        ImageButton back = findViewById(R.id.nBack);
        back.setOnClickListener(v -> finish());
        uploadPic = findViewById(R.id.uploadPic);
        profilePic = findViewById(R.id.profilePic);
        profileName = findViewById(R.id.profileName);
        profileNameEdit = findViewById(R.id.profileNameEdit);
        profileName.setOnClickListener(v -> {
            profileNameEdit.setVisibility(View.VISIBLE);
            profileNameEdit.setText(profileName.getText().toString());
            profileName.setVisibility(View.GONE);
        });
    }

    private void OnClick() {
        uploadPic.setOnClickListener(v ->SelectImage());
    }

    private void SelectImage() {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            // Uri indicates, where the image will be picked from
            Uri filePath = data.getData();
            userVM.InsertProfilePic(filePath, userData);
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                profilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }
}