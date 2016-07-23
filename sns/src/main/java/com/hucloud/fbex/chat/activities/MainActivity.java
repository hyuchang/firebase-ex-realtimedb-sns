package com.hucloud.fbex.chat.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.hucloud.fbex.chat.R;
import com.hucloud.fbex.chat.firebase.FirebaseWrapp;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private LinearLayout mChatArea;
    private ScrollView mScrollView;
    private EditText mEtMessage;
    private FirebaseUser mUser;
    private FirebaseDatabase mDb;
    private DatabaseReference mDbRef;
    public static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setView();
        setData();
    }

    private void setView(){
        mChatArea = (LinearLayout) findViewById(R.id.chatArea);
        mScrollView = (ScrollView) findViewById(R.id.scroll);
        mEtMessage = (EditText) findViewById(R.id.msg);
        mUser = FirebaseWrapp.getAuthInstance().getCurrentUser();
    }

    private void setData(){
        mDb = FirebaseWrapp.getDbInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        db();
    }

    private void db() {
        mDbRef = mDb.getReference("messages");

        mDbRef.addChildEventListener(new ChildEventListener() {

            // Firebase DataAdd 시 호출(RealTime)
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i(TAG, "LoginActivity.onComplete");
                Map<String, Object> messageMap = (Map)dataSnapshot.getValue();
                if ( messageMap == null ) {
                    return;
                }
                String sender = messageMap.get("senderId").toString();
                String senderNm = messageMap.get("senderEmail").toString();
                String msg = messageMap.get("message").toString();
                if ( sender.equals(mUser.getUid())) {
                    addMeArea(messageMap);
                } else {
                    addUArea(messageMap);
                }
                scrollToEnd();
            }

            // Firebase Data내용 변경 시 호출(RealTime)
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                System.out.println( "onChildChangeds!!!!");
                if ( s != null ) {
                    System.out.println( s );
                }
                Toast.makeText(getApplication(), "dataChanged", Toast.LENGTH_LONG).show();
                scrollToEnd();
            }

            // Firebase Data 삭제 시 호출(RealTime)
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                System.out.println( "onChildRemoved!!!!");
                Toast.makeText(getApplication(), "dataremoved!!!!", Toast.LENGTH_LONG).show();

                System.out.println( dataSnapshot.getKey() + " [ " + dataSnapshot.getValue() + " ] ");
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //메세지 발송
    public void sendMessage(View view) {
        if ( mEtMessage.getText().toString().isEmpty() ) {
            return;
        }
        // Firebase DB 스키마 설정
        HashMap<String, Object> m = new HashMap<String, Object>();
        m.put("senderId", mUser.getUid());
        m.put("senderEmail", mUser.getEmail());
        m.put("message",mEtMessage.getText().toString());
        m.put("type","txtMsg");

        // Row 추가 후 해당 Row 의 데이터 업데이트
        mDbRef.push().updateChildren(m);
        mEtMessage.setText("");
    }

    private void addUArea(Map<String, Object> message) {
        TextView tv = new TextView(getApplication());
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout linearLayout = new LinearLayout(getApplication());
        linearLayout.setBackgroundColor(Color.YELLOW);
        linearLayout.setLayoutParams(param);
        linearLayout.setPadding(20, 30, 20, 30);
        param.topMargin = 20;
        param.gravity = Gravity.LEFT;

        tv.setLayoutParams(param);
        tv.setMaxWidth(700);
        tv.setTextSize(20);
        tv.setPadding(20,20,20,20);
        tv.setText( message.get("message").toString() );
        tv.setTextColor(Color.BLACK);

        linearLayout.addView(tv);
        mChatArea.addView(linearLayout);
    }

    private void addMeArea(Map<String, Object> message) {
        TextView tv = new TextView(getApplication());

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        param.bottomMargin = 10;
        LinearLayout linearLayout = new LinearLayout(getApplication());
        linearLayout.setBackgroundColor(Color.parseColor("#18D5FF"));
        linearLayout.setPadding(20, 30, 20, 30);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        param.gravity = Gravity.RIGHT;
        linearLayout.setLayoutParams(param);

        tv.setMaxWidth(700);
        tv.setTextSize(20);
        tv.setText( message.get("message").toString() );
        tv.setPadding(20,20,20,20);

        tv.setTextColor(Color.WHITE);
        tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(tv);
        mChatArea.addView(linearLayout);
    }

    public void scrollToEnd(){
        mScrollView.fullScroll(View.FOCUS_DOWN);
    }

}
