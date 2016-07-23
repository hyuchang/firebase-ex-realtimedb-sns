package com.hucloud.fbex.chat.firebase;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

/**
 * 해당 파일은 소유권은 신휴창에게 있습니다.
 * 현재 오픈 소스로 공개중인 버전은 AGPL을 따르는 오픈 소스 프로젝트이며,
 * 소스 코드를 수정하셔서 사용하는 경우에는 반드시 동일한 라이센스로 소스 코드를 공개하여야 합니다.
 * 만약 HUCLOUD를 상업적으로 이용하실 경우에는 라이센스를 구매하여 사용하셔야 합니다.
 * email : huttchang@gmail.com
 * 프로젝트명    : fbex
 * 작성 및 소유자 : hucloud
 * 최초 생성일   : 2016. 6. 23.
 */
public class FirebaseWrapp {

    private static FirebaseAuth authInstance;
    private static FirebaseDatabase dbInstance;
    private static FirebaseStorage storageInstance;
    private static FirebaseCrash firebaseCrashInstance;

    public static FirebaseAuth getAuthInstance() {
        if ( authInstance == null ) {
            authInstance = FirebaseAuth.getInstance();
        }
        return authInstance;
    }

    public static FirebaseDatabase getDbInstance() {
        if ( dbInstance == null ) {
            dbInstance = FirebaseDatabase.getInstance();
        }
        return dbInstance;
    }

    public static FirebaseStorage getStorageInstance() {
        if ( storageInstance == null ) {
            storageInstance = FirebaseStorage.getInstance();
        }
        return storageInstance;
    }

    public static FirebaseCrash getCrashInstance() {
        if ( firebaseCrashInstance == null ) {
            firebaseCrashInstance = FirebaseCrash.getInstance(FirebaseApp.getInstance());
        }
        return firebaseCrashInstance;
    }
}
