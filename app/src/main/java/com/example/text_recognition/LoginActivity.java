package com.example.text_recognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    Window window;
    Button btnLogin;
    EditText loginEmail, loginPass;
    TextView txtCreate, txtForgot;
    static boolean count;
    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(Build.VERSION.SDK_INT>=23)
        {
            window=this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.colorWhile));
        }
        count = false;
        mAuth = FirebaseAuth.getInstance();
        Mapping();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
                if(mFirebaseUser!= null)
                {
                    Toast.makeText(LoginActivity.this, "Bạn đã đăng nhập", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        };

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString().trim();
                String password = loginPass.getText().toString().trim();
                if(email.isEmpty())
                {
                    loginEmail.setError("Vui lòng nhập email");
                    loginEmail.requestFocus();
                }
                else if(password.isEmpty())
                {
                    loginPass.setError("Vui lòng nhập mật khẩu");
                    loginPass.requestFocus();
                }
                else if(email.isEmpty() && password.isEmpty())
                {
                    Toast.makeText(LoginActivity.this, "Bạn chưa nhập email và mật khẩu !", Toast.LENGTH_SHORT).show();
                }
                else if(!email.isEmpty() && !password.isEmpty())
                {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful())
                            {
                                Toast.makeText(LoginActivity.this, "Đăng nhập không thành công", Toast.LENGTH_SHORT). show();
                            }
                            else
                            {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Lỗi!!!", Toast.LENGTH_SHORT). show();
                }
            }
        });

        txtCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void Mapping() {
        txtCreate=findViewById(R.id.loginCreate);
        txtForgot=findViewById(R.id.loginForgot);

        btnLogin=findViewById(R.id.btnLogin);

        loginEmail=findViewById(R.id.loginEmail);
        loginPass=findViewById(R.id.loginPass);
    }

    @Override
    protected void onResume() {
        count = false;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
        if(!count){
            count = true;
            Toast.makeText(this, "Chạm lần nữa để thoát", Toast.LENGTH_SHORT).show();
        } else{
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }
}
