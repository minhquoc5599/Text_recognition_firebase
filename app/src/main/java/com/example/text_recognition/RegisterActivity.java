package com.example.text_recognition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    Window window;
    Button btnRegister;
    EditText registerEmail, registerPass;
    FirebaseAuth mAuth;
    DatabaseReference mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if(Build.VERSION.SDK_INT>=23)
        {
            window=this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.colorWhile));
        }

        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();

        Mapping();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = registerEmail.getText().toString().trim();
                String password = registerPass.getText().toString().trim();
                if(email.isEmpty())
                {
                    registerEmail.setError("Vui lòng nhập email");
                    registerEmail.requestFocus();
                }
                else if(password.isEmpty())
                {
                    registerPass.setError("Vui lòng nhập mật khẩu");
                    registerPass.requestFocus();
                }
                else if(email.isEmpty() && password.isEmpty())
                {
                    Toast.makeText(RegisterActivity.this, "Bạn chưa nhập email và mật khẩu !", Toast.LENGTH_SHORT).show();
                }
                else if(!email.isEmpty() && !password.isEmpty())
                {
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful())
                            {
                                Toast.makeText(RegisterActivity.this, "Lỗi!!!", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Users users = new Users(email);
                                mData.child("Users").push().setValue(users, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        if(databaseError==null)
                                        {
                                            Toast.makeText(RegisterActivity.this, "Lưu dữ liệu thành công", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            Toast.makeText(RegisterActivity.this, "Lỗi dữ liệu!!!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(RegisterActivity.this, "Lỗi!!!", Toast.LENGTH_SHORT). show();
                }
            }
        });
    }
    private void Mapping() {

        btnRegister = findViewById(R.id.btnRegister);
        registerEmail = findViewById(R.id.registerEmail);
        registerPass = findViewById(R.id.registerPass);
    }
}
