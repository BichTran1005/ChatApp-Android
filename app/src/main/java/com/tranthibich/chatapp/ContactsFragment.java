package com.tranthibich.chatapp;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    private View contactView;
    private RecyclerView myContactsList;

    private DatabaseReference contactRef, userRef;
    private FirebaseAuth mAuth;
    private String curentUserID;

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contactView = inflater.inflate(R.layout.fragment_contacts, container, false);


        IntializeFields();

        mAuth = FirebaseAuth.getInstance();
        curentUserID = mAuth.getCurrentUser().getUid();

        contactRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(curentUserID);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");


        return contactView;
    }

    private void IntializeFields() {
        myContactsList = (RecyclerView) contactView.findViewById(R.id.contacts_list);
        myContactsList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options
                = new FirebaseRecyclerOptions.Builder<Contacst>()
                .setQuery(contactRef, Contacst.class)
                .build();

        FirebaseRecyclerAdapter<Contacst, ContactsViewHolder > adapter
                = new FirebaseRecyclerAdapter<Contacst, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder,
                                            int position, @NonNull Contacst model)
            {
                String userIds = getRef(position).getKey();

                userRef.child(userIds).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            if(snapshot.child("userState").hasChild("state"))
                            {
                                String state = snapshot.child("userState").child("state")
                                        .getValue().toString();
                                String date = snapshot.child("userState").child("date")
                                        .getValue().toString();
                                String time = snapshot.child("userState").child("time")
                                        .getValue().toString();

                                if(state.equals("online"))
                                {
                                    holder.onlineIcon.setVisibility(View.VISIBLE);
                                }
                                else if(state.equals("offline"))
                                {
                                    holder.onlineIcon.setVisibility(View.INVISIBLE);
                                }
                            }
                            else
                            {
                                holder.onlineIcon.setVisibility(View.INVISIBLE);
                            }

                            if(snapshot.hasChild("image"))
                            {
                                String profileImage = snapshot.child("image")
                                        .getValue().toString();
                                String profileName = snapshot.child("name")
                                        .getValue().toString();
                                String profileStatus= snapshot.child("status")
                                        .getValue().toString();

                                holder.userName.setText(profileName);
                                holder.userStatus.setText(profileStatus);
                                Picasso.get().load(profileImage)
                                        .placeholder(R.drawable.profile_image)
                                        .into(holder.userImage);
                            }
                            else
                            {
                                String profileName = snapshot.child("name")
                                        .getValue().toString();
                                String profileStatus= snapshot.child("status")
                                        .getValue().toString();

                                holder.userName.setText(profileName);
                                holder.userStatus.setText(profileStatus);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_display_layout, parent,
                                false);
                ContactsViewHolder viewHolder = new ContactsViewHolder(view);

                return viewHolder;
            }
        };

        myContactsList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userStatus;
        ImageView userImage;
        ImageView onlineIcon;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_frofile_name);
            userStatus = itemView.findViewById(R.id.user_frofile_status);
            userImage = itemView.findViewById(R.id.user_profile_image);

            onlineIcon = itemView.findViewById(R.id.user_online_status);
        }
    }
}
