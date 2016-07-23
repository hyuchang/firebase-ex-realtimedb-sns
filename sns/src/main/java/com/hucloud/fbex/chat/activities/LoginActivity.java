package com.hucloud.fbex.chat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hucloud.fbex.chat.R;
import com.hucloud.fbex.chat.firebase.FirebaseWrapp;

/**
 * 해당 파일은 소유권은 신휴창에게 있습니다.
 * 현재 오픈 소스로 공개중인 버전은 AGPL을 따르는 오픈 소스 프로젝트이며,
 * 소스 코드를 수정하셔서 사용하는 경우에는 반드시 동일한 라이센스로 소스 코드를 공개하여야 합니다.
 * 만약 HUCLOUD를 상업적으로 이용하실 경우에는 라이센스를 구매하여 사용하셔야 합니다.
 * email : huttchang@gmail.com
 * 프로젝트명    : fbex
 * 작성 및 소유자 : hucloud
 * 최초 생성일   : 2016. 6. 24.
 */
public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    private EditText etEmail, etPwd;
    private FirebaseUser mUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setView();
    }

    private void setView(){
        etEmail = (EditText) findViewById( R.id.email );
        etPwd = (EditText) findViewById( R.id.pwd );
    }

    public  void signIn(View view){

        String email = etEmail.getText().toString();
        String passwd = etPwd.getText().toString();

        if ( email.isEmpty() ) {
            Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        if ( passwd.isEmpty() ) {
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        // authInstanceGet
        FirebaseAuth authInstance = FirebaseWrapp.getAuthInstance();

        // Firebase Auth SignInRequest
        Task<AuthResult> result = authInstance.signInWithEmailAndPassword(email, passwd);

        // Firebase Auth Complete Listener
        result.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                Log.i(TAG, "LoginActivity.onComplete");
            }
        });
        // Firebase Auth Success Listener
        result.addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.i(TAG, "LoginActivity.onSuccess");
                mUser = authResult.getUser();
                startAnalytics();
                startActivity(new Intent(LoginActivity.this, FeedActivity.class));
            }
        });
        // Firebase Auth Failure Listener
        result.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.i(TAG, "LoginActivity.onFailure");
                e.printStackTrace();
            }
        });
    }

    public void signUp(View view) {
        String email = etEmail.getText().toString();
        String passwd = etPwd.getText().toString();

        if ( email.isEmpty() ) {
            Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_LONG).show();
            return;
        }
        if ( passwd.isEmpty() ) {
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        //Firebase Email And Password, AddUser
        FirebaseWrapp.getAuthInstance()
            .createUserWithEmailAndPassword(email,passwd)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    if ( task.isSuccessful() ) {
                        Toast.makeText(getApplicationContext(), "등록 되셨습니다.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "등록에 실패하였습니다.", Toast.LENGTH_LONG).show();
                    }
                }
            });
    }
    // Firebase Analytics start
    private void startAnalytics(){
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);
        analytics.setAnalyticsCollectionEnabled(true);
        analytics.setUserId(mUser.getUid());
        analytics.setMinimumSessionDuration(300);

    }
}

