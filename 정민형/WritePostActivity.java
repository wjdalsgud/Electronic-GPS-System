package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;


// 정민형 : 사용자가 입력한 게시판 글을 업로드하는 클래스 , ProfileUpdate() , uploader() 메소드 구현
public class WritePostActivity extends AppCompatActivity {

    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMdd");

    private static final String TAG = "writePostActivity";
    private FirebaseUser firebaseUser;
    private Object writeinfo;

    private String getTime() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }


    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_post_activity);

        findViewById(R.id.check).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v){
            switch(v.getId()) {
                case R.id.check:
                    profileUpdate();
                    Intent intent = new Intent(getApplicationContext(),info.class);
                    startActivity(intent);
                    break;
            }
            Toast.makeText(WritePostActivity.this, "글이 정상적으로 등록되었습니다", Toast.LENGTH_SHORT).show();
        }
    };

    public void profileUpdate() {
        final String title = ((EditText) findViewById(R.id.titleEditText)).getText().toString();
        final String contents = ((EditText) findViewById(R.id.contentEditText)).getText().toString();

        UserAccount account = new UserAccount();

        if(title.length() > 0 && contents.length() >0) {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            Writeinfo writeinfo = new Writeinfo(title,contents ,firebaseUser.getEmail() , getTime());
            uploader(writeinfo);

        }
        else {
            Toast.makeText(this,"회원정보를 입력해주세요",Toast.LENGTH_SHORT).show();
        }
    }
    private void uploader(Writeinfo writeinfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("post").add(writeinfo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG,"DocumentSnapshot written with ID: "+ documentReference.getId());
                    }
                })
               .addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Log.w(TAG,"Error adding document",e);
                   }
         });
    }
}