package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// 정민형 : 사용자 로그인 및 회원가입 창 구현
//         signInWithEmailAndPassword() 메소드 구현
public class MainActivity extends AppCompatActivity {
    ProgessDialog dialog;

    private long backKeyPressedTime = 0;
    private Toast toast;

    private FirebaseAuth mfirebaseAuth; //파이어베이스 인증
    private DatabaseReference databaseRef; //실시간 데이터베이스 서버에 연동하는것
    private EditText mEtemail , mEtPwd; //회원가입 입력필드



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mfirebaseAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("Test");

        mEtemail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);

        Button btn_search = findViewById(R.id.Search_btn);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);

            }
        });





        Button btn_login = findViewById(R.id.btn_Login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String strEmail = mEtemail.getText().toString();
                String strPwd = mEtPwd.getText().toString();

                mfirebaseAuth.signInWithEmailAndPassword(strEmail , strPwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful()) {
                            Intent intent = new Intent(MainActivity.this, MainPage.class);
                            Toast.makeText(MainActivity.this,"로그인에 성공했습니다",Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this,"로그인 실패 하셨습니다!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });



        Button btn_register = findViewById(R.id.btn_register);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}