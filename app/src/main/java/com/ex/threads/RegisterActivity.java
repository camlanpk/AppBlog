package com.ex.threads;

import android.app.ProgressDialog;
import android.content.Intent;
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

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText username, fullname, password, email;
    Button register;
    TextView text_login;
    FirebaseAuth mauth;
    DatabaseReference reference;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        fullname = findViewById(R.id.fullname);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        register = findViewById(R.id.register_btn);
        text_login = findViewById(R.id.txt_login);

        mauth = FirebaseAuth.getInstance();

        text_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd = new ProgressDialog(RegisterActivity.this);
                pd.setMessage("Please wait...");
                pd.show();

                String str_username = username.getText().toString();
                String str_fullname = fullname.getText().toString();
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();

                if (TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname) || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)) {
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else if (str_password.length() < 6) {
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this, "Password must have at least 6 characters", Toast.LENGTH_SHORT).show();
                } else {
                    register(str_username, str_fullname, str_email, str_password);
                }
            }
        });
    }

    private void register(final String username, final String fullname, final String email, String password) {
        mauth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = mauth.getCurrentUser();
                    if (firebaseUser != null) {
                        String userid = firebaseUser.getUid();
                        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("id", userid);
                        hashMap.put("username", username);
                        hashMap.put("fullname", fullname);
                        hashMap.put("bio", "");
                        hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/blogappxn-19.appspot.com/o/placeholder.png?alt=media&token=9d1523f8-193d-48a3-bfe2-a5db20cfb42c");

                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                pd.dismiss();
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(RegisterActivity.this, StartActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Failed to register. Try again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this, "You cannot register with this email and password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}