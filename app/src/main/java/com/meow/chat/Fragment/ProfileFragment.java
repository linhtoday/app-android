package com.meow.chat.Fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.meow.chat.R;
import com.meow.chat.Until;
import com.meow.chat.model.User;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    MaterialEditText txt_name, txt_pass_old, txt_pass_new;
    AlertDialog dialog;
    CircleImageView image_profile;
    TextView username;
    CardView cardView_edit_name,cardView_edit_pass;
    DatabaseReference reference, ref_name_user_cur;
    String user_cur = Until.getInstance().getUid();
    ImageView img_edit;
    StorageReference storageReference;
    static final int IMAGE_REQUEST = 1;
    Uri imageUri;
    StorageTask uploadTask;
    ImageView img_menu;
    //Button img_menu;
    Button btn_edit_name, btn_edit_pass;

    FirebaseAuth mAuth;

    private BroadcastReceiver mRefreshReceiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("card_view", "olalalal");
            if (intent.getAction().equals("My Broadcast")) {
                cardView_edit_name.setVisibility(View.GONE);
                cardView_edit_pass.setVisibility(View.GONE);
            }
        }
    };


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("fragments_profile", "onCreateView");
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("fragments_profile", "onCreatedddd View");
        image_profile = view.findViewById(R.id.profile_image);
        username = view.findViewById(R.id.username);
        //img_edit = view.findViewById(R.id.img_edit);
        cardView_edit_name = view.findViewById(R.id.card_view_edit_name);
        cardView_edit_pass = view.findViewById(R.id.card_view_reset_pass);
        btn_edit_name = view.findViewById(R.id.card_view_btn);
        btn_edit_pass = view.findViewById(R.id.btn_reset_pass);
        txt_name = view.findViewById(R.id.card_view_tvName);
        img_menu = view.findViewById(R.id.img_menu);
        dialog =  new SpotsDialog.Builder().setContext(getActivity()).setCancelable(false).build();
        mAuth = FirebaseAuth.getInstance();

        txt_pass_new = view.findViewById(R.id.txt_pass_new);
        txt_pass_old = view.findViewById(R.id.txt_pass_old);

        registerForContextMenu(img_menu);


        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_menu.showContextMenu();
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction("My Broadcast");

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRefreshReceiver, filter);

        btn_edit_pass.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(txt_pass_new.getText().toString().equals("") || txt_pass_old.getText().toString().equals(""))
                {
                    Toast.makeText(getActivity(), "Yêu cầu mật khẩu không được để trống", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    dialog.show();
                    final FirebaseUser user;
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    final String email = user.getEmail();
                    AuthCredential credential = EmailAuthProvider.getCredential(email,txt_pass_old.getText().toString());

                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                user.updatePassword(txt_pass_new.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getActivity(), "OK", Toast.LENGTH_SHORT).show();
                                        }else {
                                            Toast.makeText(getActivity(), "Sonmething wrong :v", Toast.LENGTH_SHORT).show();
                                        }

                                        dialog.dismiss();
                                    }
                                });
                            }else {
                                Toast.makeText(getActivity(), "Sonmething wrong :v", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    });
                }
            }

//                    mAuth.confirmPasswordReset(txt_pass_old.getText().toString(), txt_pass_new.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if(task.isSuccessful())
//                            {
//                                cardView_edit_pass.setAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.fade_fake));
//
//                                cardView_edit_pass.setVisibility(View.GONE);
//
//                                Toast.makeText(getActivity(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
//                            }
//                            else
//                            {
//                                Log.d("wtfLoi", task.getException().getMessage());
//                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//
//
//                            dialog.dismiss();
//                        }
//                    });







        });

        btn_edit_name.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {



                //txt_name.
                if(txt_name.getText().toString().equals(""))
                {
                    Toast.makeText(getActivity(), "Yêu cầu tên không được để trống", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    cardView_edit_name.setAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.fade_fake));

                    cardView_edit_name.setVisibility(View.GONE);


                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("username", txt_name.getText().toString());
                    ref_name_user_cur.updateChildren(hashMap);
                    username.setText(txt_name.getText().toString());
                    txt_name.setText("");
                }


            }
        });

        storageReference = FirebaseStorage.getInstance().getReference("uploads");


//        img_edit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cardView_edit_name.setVisibility(View.VISIBLE);
//                cardView_edit_name.setAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.fade_scale_anim));
//            }
//        });
        reference = FirebaseDatabase.getInstance().getReference("user").child(user_cur);


        ref_name_user_cur = FirebaseDatabase.getInstance().getReference("user").child(user_cur);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {
                    User user = dataSnapshot.getValue(User.class);
                    username.setText(user.getUsername());
                    if (user.getImageURL().equals("df")){
                        image_profile.setImageResource(R.drawable.ic_account_circle_24dp);
                    } else {
                        // Glide.with(getContext()).load(user.getImageURL()).into(image_profile);
                        Picasso.with(getActivity()).load(user.getImageURL()).placeholder(R.drawable.ic_account_circle_24dp).into(image_profile);
                    }
                }
                catch(Exception ex)
                {

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        image_profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null){
            final  StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw  task.getException();
                    }

                    return  fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", ""+mUri);
                        reference.updateChildren(map);

                        pd.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUri = data.getData();

            Log.d("givai", "huhu");
            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getContext(), "Upload in preogress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        Log.d("givai", "huhu");
        getActivity().getMenuInflater().inflate(R.menu.menu_item_edit, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.d("givai", "huhu" + item.getItemId());
        switch (item.getItemId())
        {
            case R.id.edit_name:
                cardView_edit_name.setVisibility(View.VISIBLE);
                cardView_edit_name.setAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.fade_scale_anim));
                return true;
            case R.id.edit_pass:
                cardView_edit_pass.setVisibility(View.VISIBLE);
                cardView_edit_pass.setAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.fade_scale_anim));
                return  true;
            case R.id.edit_avt:
                openImage();
                return true;



        }

        return super.onContextItemSelected(item);




    }
}
