package com.meow.chat;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {

    MaterialEditText email, pass;
    TextView forgot_password, txt_res;
    Button btn_login;

    DatabaseReference cnt_user_cur;
    AlertDialog dialog;

    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        dialog =  new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        btn_login = findViewById(R.id.btn_login);
        txt_res = findViewById(R.id.txt_res);
        forgot_password = findViewById(R.id.forgot_password);
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        txt_res.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                //Until.getInstance().setUid(auth.getCurrentUser().getUid());
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                String txt_email = email.getText().toString();
                String txt_password = pass.getText().toString();

                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                    Toast.makeText(LoginActivity.this, "Yêu cập nhập đầy đủ thông tin!!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {

                    if(txt_email.endsWith("@gmail.com"))
                    {

                    }
                    else txt_email += "@gmail.com";

                    Log.d("ccnhe", txt_email + "   " + txt_password);
                    auth.signInWithEmailAndPassword(txt_email, txt_password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        Intent intent = new Intent(LoginActivity.this, TestActivity.class);
                                        Until.getInstance().setUid(auth.getCurrentUser().getUid());
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        resetListCountMessage();
                                        startActivity(intent);
                                        dialog.dismiss();
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại!!", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                }
                            });
                }
            }
        });


    }
     void resetListCountMessage()
    {


        cnt_user_cur = FirebaseDatabase.getInstance().getReference("bubble").child(Until.getInstance().getUid());

        cnt_user_cur.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot item: dataSnapshot.getChildren())
                {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("cnt", 0);
                    hashMap.put("mes", "xxx");

                    item.getRef().updateChildren(hashMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
