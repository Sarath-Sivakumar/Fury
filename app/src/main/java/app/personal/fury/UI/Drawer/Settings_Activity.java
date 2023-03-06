package app.personal.fury.UI.Drawer;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

import app.personal.MVVM.Entity.userEntity;
import app.personal.MVVM.Viewmodel.LoggedInUserViewModel;
import app.personal.Utls.Commons;
import app.personal.Utls.Constants;
import app.personal.fury.R;

public class Settings_Activity extends AppCompatActivity {

    private LoggedInUserViewModel userVM;
    private ImageButton uploadPic;
    private ImageView profilePic;
    private TextView profileName;
    private EditText profileNameEdit;
    private Button save, discard;
    private Button contact, faq, feedback, privacy, terms, about;
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
                if (!userEntity.getImgUrl().equals(Constants.DEFAULT_DP)) {
                    Glide.with(this)
                            .load(userData.getImgUrl())
                            .circleCrop()
                            .into(profilePic);
                } else {
                    profilePic.setImageResource(R.drawable.nav_icon_account);
                }
            }
        });
    }

    private void findView() {
        ImageButton back = findViewById(R.id.nBack);
        back.setOnClickListener(v -> finish());
        contact = findViewById(R.id.Contact_us);
        faq = findViewById(R.id.faq);
        feedback = findViewById(R.id.feedback);
        privacy = findViewById(R.id.p_policies);
        terms = findViewById(R.id.t_c);
        about = findViewById(R.id.app_info);

        uploadPic = findViewById(R.id.uploadPic);
        profilePic = findViewById(R.id.profilePic);
        profileName = findViewById(R.id.profileName);
        profileNameEdit = findViewById(R.id.profileNameEdit);
        save = findViewById(R.id.save);
        discard = findViewById(R.id.cancel);
        profileName.setOnClickListener(v -> {
            profileNameEdit.setVisibility(View.VISIBLE);
            save.setVisibility(View.VISIBLE);
            discard.setVisibility(View.VISIBLE);
            profileNameEdit.setText(profileName.getText().toString());
            profileName.setVisibility(View.GONE);
        });
    }

    private void OnClick() {
        uploadPic.setOnClickListener(v -> callPopupWindow(uploadPic));

        contact.setOnClickListener(v -> {
//            Intent i = new Intent(this, WebViewActivity.class);
//            i.putExtra(Constants.WEB_VIEW_ACTIVITY_TITLE, "Contact Us");
//            i.putExtra(Constants.WEB_VIEW_ACTIVITY_URL, "URL here");
//            startActivity(i);
        });

        faq.setOnClickListener(v -> {
            Intent i = new Intent(this, WebViewActivity.class);
            i.putExtra(Constants.WEB_VIEW_ACTIVITY_TITLE, "FAQ");
            i.putExtra(Constants.WEB_VIEW_ACTIVITY_URL,  "URL here");
            startActivity(i);
        });

        feedback.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_SENDTO);
            String UriText = "mailto:" +      Uri.encode("shoptourhome@gmail.com") +"?subject="+
                    Uri.encode("Write to us: ") + Uri.encode("");
            Uri uri = Uri.parse(UriText);
            intent.setData(uri);
            startActivity(Intent.createChooser(intent,"send email"));
//            Toast.makeText(this, "Thanks for the,feedback!", Toast.LENGTH_SHORT).show();

        });

        privacy.setOnClickListener(v -> {
            Intent i = new Intent(this, WebViewActivity.class);
            i.putExtra(Constants.WEB_VIEW_ACTIVITY_TITLE, "Privacy Policy");
            i.putExtra(Constants.WEB_VIEW_ACTIVITY_URL,  "file:///android_asset/web_resources/privacy_policy.html");
            startActivity(i);
        });

        terms.setOnClickListener(v -> {
            Intent i = new Intent(this, WebViewActivity.class);
            i.putExtra(Constants.WEB_VIEW_ACTIVITY_TITLE, "Terms and Conditions");
            i.putExtra(Constants.WEB_VIEW_ACTIVITY_URL,  "file:///android_asset/web_resources/t_c.html");
            startActivity(i);
        });

        about.setOnClickListener(v -> {
            Intent i = new Intent(this, WebViewActivity.class);
            i.putExtra(Constants.WEB_VIEW_ACTIVITY_TITLE, "About us");
            i.putExtra(Constants.WEB_VIEW_ACTIVITY_URL,  "file:///android_asset/web_resources/about_app.html");
            startActivity(i);
        });

        save.setOnClickListener(v -> {
            String uname = profileNameEdit.getText().toString();
            if (!uname.trim().isEmpty()) {
                if (!uname.equals(profileName.getText().toString())) {
                    userEntity entity = userData;
                    entity.setName(uname);
                    userVM.UpdateUserData(entity);
                    save.setVisibility(View.GONE);
                    discard.setVisibility(View.GONE);
                    profileNameEdit.setVisibility(View.GONE);
                    profileName.setVisibility(View.VISIBLE);
                } else {
                    Commons.SnackBar(save, "That's the same name -_-");
                }
            } else {
                Commons.SnackBar(save, "Hello NoName human, field cannot be empty. Try again!");
            }
        });
        discard.setOnClickListener(v -> {
            profileNameEdit.setVisibility(View.GONE);
            profileName.setVisibility(View.VISIBLE);
            save.setVisibility(View.GONE);
            discard.setVisibility(View.GONE);
        });
    }

    private void callPopupWindow(ImageButton uploadPic) {
        PopupWindow popupWindow = new PopupWindow(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.popup_action_profileoptions, null);
        popupWindow.setContentView(view);
        popupWindow.setFocusable(true);

        ImageButton Camera = view.findViewById(R.id.camera);
        ImageButton Files = view.findViewById(R.id.gallery);
        View background1 = view.findViewById(R.id.bgView1);
        View background2 = view.findViewById(R.id.bgView2);
        background1.setOnClickListener(v -> popupWindow.dismiss());
        background2.setOnClickListener(v -> popupWindow.dismiss());
        Camera.setOnClickListener(v -> {
//                todo
        });
        Files.setOnClickListener(v -> {
            SelectImage();
            popupWindow.dismiss();
        });

        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.setElevation(6);
        popupWindow.setOverlapAnchor(true);
        popupWindow.showAsDropDown(uploadPic);
    }

    private void SelectImage() {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
                Bitmap bitmap = MediaStore.Images.Media
                        .getBitmap(getContentResolver(), filePath);
                profilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }
}