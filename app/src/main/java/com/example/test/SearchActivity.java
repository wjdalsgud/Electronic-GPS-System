package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// 정민형: 사용자 아이디 찾기 구현 OnDataChange() 구현 ,   sendPasswordResetEmail() 구현
public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth firebaseAuth;
    private ProgessDialog progessDialog;
    private EditText editTextUserEmail;
    private TextView textviewMessage;
    private Button ButtonFind;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();


    String getTest = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        firebaseAuth = FirebaseAuth.getInstance();

        EditText find_id_name_text = (EditText) findViewById(R.id.find_ID_Name_Text);
        Button find_id_button = (Button) findViewById(R.id.find_ID_Button);
        editTextUserEmail = (EditText) findViewById(R.id.editTextUserEmail);
        ButtonFind = (Button) findViewById(R.id.buttonFind);

        ButtonFind.setOnClickListener((View.OnClickListener) this);

        find_id_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance().getReference("UserAccount")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String find_id_name = find_id_name_text.getText().toString().trim();
                                int count = 0;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    String parsing = snapshot.getValue().toString();
                                    int creatStart;
                                    int creatEnd;
                                    creatStart = parsing.indexOf("name=");
                                    creatEnd = parsing.indexOf(", e");
                                    String compare = parsing.substring(creatStart, creatEnd).replace("name=", "");
                                    if (compare.equals(find_id_name)) {
                                        creatStart = parsing.indexOf("emailID=");
                                        creatEnd = parsing.indexOf("}");
                                        String id = parsing.substring(creatStart, creatEnd).replace("emailID=", "");
                                        AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                                        builder.setTitle("아이디"); //AlertDialog의 제목 부분
                                        builder.setMessage(id); //AlertDialog의 내용 부분
                                        builder.setPositiveButton("확인", null);
                                        builder.create().show(); //보이기
                                        count = 1;
                                        break;
                                    }
                                }
                                if (count == 0) {
                                    Toast.makeText(SearchActivity.this, "일치하는 아이디가 없습니다", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == ButtonFind) {
            //비밀번호 재설정 이메일 보내기
            String emailAddress = editTextUserEmail.getText().toString().trim();
            firebaseAuth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SearchActivity.this, "이메일을 보냈습니다.", Toast.LENGTH_LONG).show();
                                finish();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else {
                                Toast.makeText(SearchActivity.this, "메일 보내기 실패!", Toast.LENGTH_LONG).show();
                            }
                            progessDialog.dismiss();
                        }
                    });
        }
    }
}