package com.tranthibich.chatapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private View privateChatsView;
    private RecyclerView chatsList;
    private RecyclerView myRequestList;
    private String retImage = "default_image";

    private DatabaseReference chatsRef, userRef, contactRef, chatRequestRef;
    private FirebaseAuth mAuth;
    private String currentUserID;




    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        privateChatsView = inflater.inflate(R.layout.fragment_chat, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        chatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        contactRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Request");

        IntializeFields();


        return privateChatsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if(chatRequestRef != null)
        {
            FirebaseRecyclerOptions<Contacst> options = new FirebaseRecyclerOptions.Builder<Contacst>()
                    .setQuery(chatRequestRef.child(currentUserID), Contacst.class)
                    .build();

            FirebaseRecyclerAdapter<Contacst, RequestFragment.RequestViewHolder> adapter =
                    new FirebaseRecyclerAdapter<Contacst, RequestFragment.RequestViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull final RequestFragment.RequestViewHolder holder, int position, @NonNull Contacst model) {

                            holder.itemView.findViewById(R.id.request_accept_button).setVisibility(View.VISIBLE);
                            holder.itemView.findViewById(R.id.request_cancel_button).setVisibility(View.VISIBLE);

                            final String listUserID = getRef(position).getKey();

                            DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();

                            getTypeRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists())
                                    {
                                        String type = snapshot.getValue().toString();

                                        if(type.equals("received"))
                                        {
                                            userRef.child(listUserID).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(snapshot.hasChild("image"))
                                                    {

                                                        final String requestImage = snapshot.child("image").getValue().toString();

                                                        Picasso.get().load(requestImage).placeholder(R.drawable.profile_image).into(holder.userImage);
                                                    }

                                                    final String requestUserName = snapshot.child("name").getValue().toString();
                                                    final String requestStatus = snapshot.child("status").getValue().toString();

                                                    holder.userName.setText(requestUserName);
                                                    holder.userStatus.setText(requestUserName + " muốn nhắn tin với bạn.");

                                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            CharSequence options[] = new CharSequence[]
                                                                    {
                                                                            "Accept",
                                                                            "Cancel"
                                                                    };

                                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                            builder.setTitle(requestUserName + "Chat Request");

                                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    if(which == 0)
                                                                    {
                                                                        contactRef.child(currentUserID).child(listUserID)
                                                                                .child("Contact").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if(task.isSuccessful())
                                                                                {
                                                                                    contactRef.child(listUserID).child(currentUserID)
                                                                                            .child("Contact").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if(task.isSuccessful())
                                                                                            {
                                                                                                chatRequestRef.child(currentUserID)
                                                                                                        .child(listUserID).removeValue()
                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                if(task.isSuccessful())
                                                                                                                {
                                                                                                                    chatRequestRef.child(listUserID)
                                                                                                                            .child(currentUserID).removeValue()
                                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                @Override
                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                    if(task.isSuccessful())
                                                                                                                                    {
                                                                                                                                        Toast.makeText(getContext(), "Lưu Liên Hệ", Toast.LENGTH_SHORT).show();
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            });
                                                                                                                }
                                                                                                            }
                                                                                                        });
                                                                                            }
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                    if(which == 1)
                                                                    {
                                                                        chatRequestRef.child(currentUserID)
                                                                                .child(listUserID).removeValue()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if(task.isSuccessful())
                                                                                        {
                                                                                            chatRequestRef.child(listUserID)
                                                                                                    .child(currentUserID).removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if(task.isSuccessful())
                                                                                                            {
                                                                                                                Toast.makeText(getContext(), "Xóa Liên Hệ", Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            });

                                                            builder.show();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                        }

                                        else if(type.equals("sent"))
                                        {
                                            Button request_sent_btn = holder.itemView.findViewById(R.id.request_accept_button);
                                            request_sent_btn.setText("Đã Gửi");

                                            holder.itemView.findViewById(R.id.request_cancel_button).setVisibility(View.INVISIBLE);


                                            userRef.child(listUserID).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(snapshot.hasChild("image"))
                                                    {

                                                        final String requestImage = snapshot.child("image").getValue().toString();

                                                        Picasso.get().load(requestImage).placeholder(R.drawable.profile_image).into(holder.userImage);
                                                    }

                                                    final String requestUserName = snapshot.child("name").getValue().toString();
                                                    final String requestStatus = snapshot.child("status").getValue().toString();

                                                    holder.userName.setText(requestUserName);
                                                    holder.userStatus.setText("Bạn đã gửi yêu cầu nhắn tin đến " + requestUserName);

                                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            CharSequence options[] = new CharSequence[]
                                                                    {
                                                                            "Hủy yêu cầu trò chuyện"
                                                                    };

                                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                            builder.setTitle("Sẵn sàng gửi yêu cầu");

                                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    if(which == 0)
                                                                    {
                                                                        chatRequestRef.child(currentUserID)
                                                                                .child(listUserID).removeValue()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if(task.isSuccessful())
                                                                                        {
                                                                                            chatRequestRef.child(listUserID)
                                                                                                    .child(currentUserID).removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if(task.isSuccessful())
                                                                                                            {
                                                                                                                Toast.makeText(getContext(), "Bạn đã hủy yêu cầu nhắn tin", Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            });

                                                            builder.show();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
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
                        public RequestFragment.RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display_layout, parent, false);
                            RequestFragment.RequestViewHolder holder = new RequestFragment.RequestViewHolder(view);
                            return  holder;
                        }
                    };


            myRequestList.setAdapter(adapter);

            adapter.startListening();
        }

            FirebaseRecyclerOptions<Contacst> options
                    = new FirebaseRecyclerOptions.Builder<Contacst>()
                    .setQuery(chatsRef, Contacst.class)
                    .build();

            FirebaseRecyclerAdapter<Contacst,
                    ChatsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacst, ChatsViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final ChatsViewHolder holder,
                                                int position, @NonNull Contacst model) {

                    final String userIDs = getRef(position).getKey();

                    userRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists())
                            {
                                if(snapshot.hasChild("image"))
                                {
                                    retImage = snapshot.child("image").getValue().toString();
                                    Picasso.get().load(retImage)
                                            .placeholder(R.drawable.profile_image).into(holder.userImage);
                                }

                                final String retName = snapshot.child("name")
                                        .getValue().toString();
                                final String retStatus = snapshot.child("status")
                                        .getValue().toString();

                                holder.userName.setText(retName);
                                holder.userStatus.setText("Đã xem: " +"\n"+ "Ngày" + "Giờ");


                                if(snapshot.child("userState").hasChild("state"))
                                {
                                    String state = snapshot.child("userState")
                                            .child("state").getValue().toString();
                                    String date = snapshot.child("userState")
                                            .child("date").getValue().toString();
                                    String time = snapshot.child("userState")
                                            .child("time").getValue().toString();

                                    if(state.equals("online"))
                                    {
                                        holder.userStatus.setText("online");
                                    }
                                    else if(state.equals("offline"))
                                    {
                                        holder.userStatus.setText("Đã xem: " + date + " " + time);
                                    }
                                }
                                else
                                {
                                    holder.userStatus.setText("offline");
                                }


                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                        chatIntent.putExtra("visit_user_id", userIDs);
                                        chatIntent.putExtra("visit_user_name", retName);
                                        chatIntent.putExtra("visit_user_image", retImage);
                                        startActivity(chatIntent);
                                    }
                                });
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @NonNull
                @Override
                public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.user_display_layout, parent, false);

                    return new ChatsViewHolder(view);
                }
            };

            chatsList.setAdapter(adapter);
            adapter.startListening();





    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView userImage;
        TextView userName, userStatus;


        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.user_profile_image);
            userName = itemView.findViewById(R.id.user_frofile_name);
            userStatus = itemView.findViewById(R.id.user_frofile_status);
        }
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName, userStatus;
        CircleImageView userImage;
        Button acceptButton, cancelButton;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_frofile_name);
            userStatus = itemView.findViewById(R.id.user_frofile_status);
            userImage = itemView.findViewById(R.id.user_profile_image);
            acceptButton = itemView.findViewById(R.id.request_accept_button);
            cancelButton = itemView.findViewById(R.id.request_cancel_button);
        }
    }

    private void IntializeFields() {
        myRequestList = (RecyclerView) privateChatsView.findViewById(R.id.chat_request_list);
        myRequestList.setLayoutManager(new LinearLayoutManager(getContext()));

        chatsList = (RecyclerView) privateChatsView.findViewById(R.id.chats_list);
        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
