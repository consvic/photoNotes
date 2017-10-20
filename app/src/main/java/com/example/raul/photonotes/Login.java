package com.example.raul.photonotes;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Login extends FragmentActivity{

    private String info = "";
    private LoginButton loginButton;
    private ImageView ivLogin;
    private CallbackManager callbackManager;
    private AccessTokenTracker tracker;
    private static String id;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        ivLogin = (ImageView)findViewById(R.id.ivLogin);

        Glide.with(getApplicationContext()).load(R.drawable.notes).into(ivLogin);


        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //AccessToken accessToken = loginResult.getAccessToken();
                Log.d("on Success","True");
                info = ("User ID: " +
                        loginResult.getAccessToken().getUserId() + "\n" + "Auth Token: " + loginResult.getAccessToken().getToken());
                ///id=loginResult.getAccessToken().getUserId().toString();
                /*Intent intent = new Intent(Login.this,MainActivity.class);
                intent.putExtra("UserID",id);
                startActivity(intent);*/
            }

            @Override
            public void onCancel() {
                info = ("Login attempt canceled.");
                Log.d("on CANCEL","True");
            }

            @Override
            public void onError(FacebookException e) {
                info = ("Login attempt failed.");
                Log.d("on ERROR","True");
            }
        });
        System.out.println(info);
        tracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            }
        };
        tracker.startTracking();
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isLoggedIn()){
            Intent intent = new Intent(Login.this,MainActivity.class);
            intent.putExtra("UserID",id);
            startActivity(intent);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        tracker.stopTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Log.d("on Success","ACTIVITY RESULT");
            Intent intent = new Intent(Login.this,MainActivity.class);
            intent.putExtra("UserID",id);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Unable to login please check your internet connection",Toast.LENGTH_LONG).show();
        }
    }
}

