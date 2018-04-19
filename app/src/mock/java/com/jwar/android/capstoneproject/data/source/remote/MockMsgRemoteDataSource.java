package com.jwar.android.capstoneproject.data.source.remote;

import android.support.annotation.NonNull;

import com.jraw.android.capstoneproject.data.source.remote.BackendApi;
import com.jraw.android.capstoneproject.data.source.remote.MsgRemoteDataSource;
import com.jraw.android.capstoneproject.data.source.remote.ResponseServerMsg;
import com.jwar.android.capstoneproject.DummyData;

/**
 * Created by JonGaming on 16/04/2018.
 */

public class MockMsgRemoteDataSource implements MsgRemoteDataSource {
    private static MockMsgRemoteDataSource sInstance=null;
    private BackendApi mBackendApi;
    public static synchronized MockMsgRemoteDataSource getInstance(@NonNull BackendApi aBackendApi) {
        if (sInstance==null) {
            sInstance = new MockMsgRemoteDataSource(aBackendApi);
        }
        return sInstance;
    }
    private MockMsgRemoteDataSource(@NonNull BackendApi aBackendApi) {

    }

    @Override
    public ResponseServerMsg getMsgsFromServer() {
        ResponseServerMsg responseServerMsg = new ResponseServerMsg();
        responseServerMsg.action="COMPLETE";
        responseServerMsg.rows= DummyData.getRemoteMsgs();
        return null;
    }
}