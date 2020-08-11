package com.tranthibich.chatapp;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class UserLoginActivity extends AppCompatActivity {

    FirebaseUser currentUser;
    Button loginButton;
    EditText userEmail, userPassword;
    TextView needaccountlink, forgetpasswordlink;

    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        AnhXa();

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUser = mAuth.getCurrentUser();

        needaccountlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowUserToLogin();
            }
        });

    }

    private void AllowUserToLogin() {
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this,
                    "Email không được trống!", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,
                    "Mật khẩu không được trống!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Đang đăng nhập");
            loadingBar.setMessage("Xin hãy đợi");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        String currentUserID = mAuth.getCurrentUser().getUid();
                        String deviceToken = FirebaseInstanceId.getInstance()
                                .getToken();

                        userRef.child(currentUserID).child("device_token")
                                .setValue(deviceToken)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            SendUserToMainActivity();
                                            Toast.makeText(UserLoginActivity.this,
                                                    "Đăng nhập thành công",
                                                    Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                        }
                                    }
                                });
                    }
                    else
                    {
                        ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                        if ( conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.DISCONNECTED
                                || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.DISCONNECTED) {

                            Toast.makeText(UserLoginActivity.this,
                                    "Kiểm tra kết nối internet của bạn.", Toast.LENGTH_SHORT).show();
                        }
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    private void AnhXa() {
        loginButton = (Button) findViewById(R.id.login_button);
        userEmail = (EditText) findViewById(R.id.login_email);
        userPassword = (EditText) findViewById(R.id.login_password);
        needaccountlink = (TextView) findViewById(R.id.alread_have_account_link);
        forgetpasswordlink = (TextView) findViewById(R.id.alread_have_account_link);

        loadingBar = new ProgressDialog(this);
    }

    protected void onStart()
    {
        super.onStart();

        if(currentUser != null)
        {
            SendUserToMainActivity();
        }
    }

    private void SendUserToMainActivity()
    {
        Intent main = new Intent(UserLoginActivity.this, MainActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(main);
        finish();
    }

    private void SendUserToRegisterActivity()
    {
        Intent register = new Intent(UserLoginActivity.this, RegisterActivity.class);
        startActivity(register);
    }


}
