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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// 정민형: 사용자 회원가입 구현 , Firebase 구현 및 createUserWithEmailAndPassword() 메소드 구현
// 조진석: (회원가입 시 아이디와 패스워드 잘못 입력시 알림 표시 ( onComplete( ) ) )
public class RegisterActivity extends AppCompatActivity {

    private EditText email_join;
    private  EditText Pwd_join;
    private EditText name_join;
    private EditText register_Pass_recheck;
    private Button register_button;

    FirebaseAuth firebaseAuth;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name_join = (EditText) findViewById(R.id.register_Name);
        email_join = (EditText) findViewById(R.id.register_ID);
        Pwd_join = (EditText) findViewById(R.id.register_Pass);
        register_button = (Button) findViewById(R.id.register_Button);
        register_Pass_recheck = (EditText) findViewById(R.id.register_Pass_recheck);

        firebaseAuth = FirebaseAuth.getInstance();

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = email_join.getText().toString().trim();
                final String pwd = Pwd_join.getText().toString().trim();
                final String name = name_join.getText().toString().trim();
                final String re_pwd = register_Pass_recheck.getText().toString().trim();
                if (!email.equals("") && !pwd.equals("") && !name.equals("") && !re_pwd.equals("")){
                    if (pwd.equals(re_pwd)) {
                        firebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseDatabase.getInstance().getReference("ElectronicGPS").child("UserAccount").child(firebaseAuth.getInstance().getCurrentUser().getUid()).setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                            UserAccount account = new UserAccount();

                                            account.setIdToken(firebaseUser.getUid());
                                            account.setEmailID(firebaseUser.getEmail());
                                            account.setPassword(firebaseAuth.hashCode());
                                            account.setName(name);
                                            databaseReference.child("UserAccount").child(firebaseUser.getUid()).setValue(account);
                                            Toast.makeText(RegisterActivity.this, "Successful Registered", Toast.LENGTH_SHORT).show();


                                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            Toast.makeText(RegisterActivity.this, "회원 가입 성공", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });
                                } else {
                                    if(pwd.length()<8) {
                                        Toast.makeText(RegisterActivity.this, "비밀번호를 8자리 이상으로 입력해주세요", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(RegisterActivity.this, "아이디를 이메일 형식으로 입력해주세요", Toast.LENGTH_SHORT).show();
                                    }
                                    return;

                                }
                            }
                        });
                    } else {
                        Toast.makeText(RegisterActivity.this, "패스워드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }else{
                    Toast.makeText(RegisterActivity.this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
    }
}