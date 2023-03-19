package app.personal.fury.UI.Drawer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import app.personal.MVVM.Entity.userEntity;
import app.personal.MVVM.Viewmodel.LoggedInUserViewModel;
import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.Utls.Commons;
import app.personal.Utls.Constants;
import app.personal.fury.R;

public class Settings_Activity extends AppCompatActivity {

    private LoggedInUserViewModel userVM;
    private mainViewModel mainVM;
    private ImageButton uploadPic;
    private ImageView profilePic;
    private TextView profileName;
    private EditText profileNameEdit;
    private Button save, discard;
    private Button contact;
    private Button faq;
    private Button feedback;
    private Button privacy;
    private Button terms;
    private Button about;
    private userEntity userData = new userEntity();
    // request code
    private final int PICK_IMAGE_REQUEST = 22;
    private InterstitialAd interstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity_settings);
        userVM = new ViewModelProvider(this).get(LoggedInUserViewModel.class);
        mainVM = new ViewModelProvider(this).get(mainViewModel.class);
        init();
    }

    private void init() {
        findView();
        getUserData();
        OnClick();
        if (Commons.isConnectedToInternet(this)){
            initAd();
        }
    }

    private void initAd() {
        MobileAds.initialize(this);
        String TestAdId = "ca-app-pub-8620335196955785/4549061972";
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                this,
                TestAdId,
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        interstitial = interstitialAd;
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        interstitial = null;
                                        finish();
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        interstitial = null;
                                        finish();
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        interstitial = null;
                    }
                });
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (interstitial != null) {
            interstitial.show(this);
        }
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
        Button clearData = findViewById(R.id.u_clearData);

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
        clearData.setOnClickListener(v -> callPopupWindow());
    }

    private void OnClick() {
        uploadPic.setOnClickListener(v -> callPopupWindow(uploadPic));

        contact.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            String UriText = "mailto:" + Uri.encode("shoptourhome@gmail.com") + "?subject=" +
                    Uri.encode("Write to us: ") + Uri.encode("");
            Uri uri = Uri.parse(UriText);
            intent.setData(uri);
            startActivity(Intent.createChooser(intent, "send email"));
//            Toast.makeText(this, "Thanks for the,feedback!", Toast.LENGTH_SHORT).show();
        });

        faq.setOnClickListener(v -> {
//            Intent i = new Intent(this, WebViewActivity.class);
//            i.putExtra(Constants.WEB_VIEW_ACTIVITY_TITLE, "FAQ");
//            i.putExtra(Constants.WEB_VIEW_ACTIVITY_URL,  "URL here");
//            startActivity(i);
        });

        feedback.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_SENDTO);
            String UriText = "mailto:" + Uri.encode("shoptourhome@gmail.com") + "?subject=" +
                    Uri.encode("Write to us: ") + Uri.encode("");
            Uri uri = Uri.parse(UriText);
            intent.setData(uri);
            startActivity(Intent.createChooser(intent, "send email"));
//            Toast.makeText(this, "Thanks for the,feedback!", Toast.LENGTH_SHORT).show();

        });

        privacy.setOnClickListener(v -> {
            Intent i = new Intent(this, WebViewActivity.class);
            i.putExtra(Constants.WEB_VIEW_ACTIVITY_TITLE, "Privacy Policy");
            i.putExtra(Constants.WEB_VIEW_ACTIVITY_URL, "file:///android_asset/web_resources/privacy_policy.html");
            startActivity(i);
        });

        terms.setOnClickListener(v -> {
            Intent i = new Intent(this, WebViewActivity.class);
            i.putExtra(Constants.WEB_VIEW_ACTIVITY_TITLE, "Terms and Conditions");
            i.putExtra(Constants.WEB_VIEW_ACTIVITY_URL, "file:///android_asset/web_resources/t_c.html");
            startActivity(i);
        });

        about.setOnClickListener(v -> {
            Intent i = new Intent(this, WebViewActivity.class);
            i.putExtra(Constants.WEB_VIEW_ACTIVITY_TITLE, "About us");
            i.putExtra(Constants.WEB_VIEW_ACTIVITY_URL, "file:///android_asset/web_resources/about_app.html");
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

    private void fakeLoadingScreen() {
        new CountDownTimer(2000, 1000) {
            final PopupWindow popupWindow = new PopupWindow(Settings_Activity.this);

            @Override
            public void onTick(long millisUntilFinished) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                assert inflater != null;
                View view = inflater.inflate(R.layout.popup_budget_fake_loading, null);
                TextView t = view.findViewById(R.id.loadingText);
                String s = "Clearing your data, please wait..";
                t.setText(s);
                popupWindow.setContentView(view);
                popupWindow.setFocusable(true);
                popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
                popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
                popupWindow.setBackgroundDrawable(null);
                popupWindow.setElevation(6);
                popupWindow.showAsDropDown(about);
            }

            @Override
            public void onFinish() {
                popupWindow.dismiss();
                Commons.clearData(mainVM);
            }
        }.start();

    }

    private void callPopupWindow() {
        PopupWindow popupWindow = new PopupWindow(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.popup_action_balanceclear, null);
        popupWindow.setContentView(view);
        popupWindow.setFocusable(true);

        TextView warning = view.findViewById(R.id.dlttxt);
        Button yes = view.findViewById(R.id.dyes_btn);
        Button no = view.findViewById(R.id.dno_btn);

        String s = "This will clear all your data.";
        warning.setText(s);
        no.setOnClickListener(v -> popupWindow.dismiss());
        yes.setOnClickListener(v -> {
            fakeLoadingScreen();
            showInterstitial();
            popupWindow.dismiss();
        });

        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.setElevation(6);
        popupWindow.setOverlapAnchor(true);
        popupWindow.showAsDropDown(uploadPic);
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