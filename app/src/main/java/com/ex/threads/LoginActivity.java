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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    EditText password,email;
    Button login;
    TextView text_signup;
    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        login = findViewById(R.id.login);
        text_signup = findViewById(R.id.txt_signup);
        auth = FirebaseAuth.getInstance();

        text_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pd = new ProgressDialog(LoginActivity.this);
                pd.setMessage("Đang tải...");
                pd.show();
                //  String str_username= username.getText().toString();
                //  String str_fullname= fullname.getText().toString();
                String str_email= email.getText().toString();
                String str_password= password.getText().toString();

                if(TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)){

                    Toast.makeText(LoginActivity.this,"Tài khoản và mật khẩu không được để trống",Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }else if(str_password.length()<6) {
                    Toast.makeText(LoginActivity.this,"Mật khẩu phải có từ 6 kí tự",Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }else {

                    auth.signInWithEmailAndPassword(str_email,str_password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {


                            if (task.isSuccessful()){

                                DatabaseReference  reference = FirebaseDatabase.getInstance().getReference().child("Users")
                                        .child(auth.getCurrentUser().getUid());

                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        pd.dismiss();

                                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                        pd.dismiss();
                                    }
                                });


                            }else {
                                pd.dismiss();
                                Toast.makeText(LoginActivity.this,"Email hoặc mật khẩu không hợp lệ hoặc email chưa đăng kí",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}