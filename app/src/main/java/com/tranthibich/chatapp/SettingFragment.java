package com.tranthibich.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {

    private View settingFragmentView;

    private Button logout;
    private TextView userName, userStatus;
    private CircleImageView userProfileImage;

    private String curentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    private static final int galleryPick = 1;

    private StorageReference userProfileImageRef;
    private ProgressDialog loadingBar;


    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        settingFragmentView =  inflater.inflate(R.layout.fragment_setting, container, false);

        AnhXa();

        mAuth = FirebaseAuth.getInstance();
        curentUserID = FirebaseAuth.getInstance().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");


        userName.setVisibility(View.VISIBLE);

        RetrieveUserInfo();

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingActivity = new Intent(getContext(), SettingsActivity.class);
                startActivity(settingActivity);

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();

                SendUserToLoginActivity();
            }
        });


        return settingFragmentView;
    }

    private void RetrieveUserInfo()
    {
        rootRef.child("Users")
                .child(curentUserID)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if((dataSnapshot.exists())
                        && dataSnapshot.hasChild("name")
                        && (dataSnapshot.hasChild("image")))
                {
                    String retrieveUserName = dataSnapshot.child("name")
                            .getValue().toString();
                    String retrieveStatus = dataSnapshot.child("status")
                            .getValue().toString();
                    String retieveProfileImage = dataSnapshot.child("image")
                            .getValue().toString();

                    userName.setText(retrieveUserName);
                    userStatus.setText(retrieveStatus);

                    Picasso.get().load(retieveProfileImage)
                            .placeholder(R.drawable.profile_image)
                            .into(userProfileImage);
                }
                else if((dataSnapshot.exists()) && dataSnapshot.hasChild("name"))
                {
                    String retrieveUserName = dataSnapshot.child("name")
                            .getValue().toString();
                    String retrieveStatus = dataSnapshot.child("status")
                            .getValue().toString();

                    userName.setText(retrieveUserName);
                    userStatus.setText(retrieveStatus);
                }
                else
                {
                    userName.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(),
                            "Hãy cập nhật thông tin cá nhân của bạn", Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SendUserToLoginActivity()
    {
        Intent login = new Intent(getContext(), UserLoginActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(login);
    }



    private void AnhXa() {
        logout = (Button) settingFragmentView.findViewById(R.id.set_logout);
        userName = (TextView) settingFragmentView.findViewById(R.id.set_textview_user_name);
        userStatus = (TextView) settingFragmentView.findViewById(R.id.set_textview_user_status);
        userProfileImage = (CircleImageView) settingFragmentView.findViewById(R.id.set_img_profile_image);

        loadingBar = new ProgressDialog(getContext());
    }


}
