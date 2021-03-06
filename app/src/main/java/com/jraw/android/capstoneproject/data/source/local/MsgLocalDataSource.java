package com.jraw.android.capstoneproject.data.source.local;

import android.content.Context;
import android.support.v4.content.CursorLoader;
import com.jraw.android.capstoneproject.data.model.Msg;

public interface MsgLocalDataSource {
    CursorLoader getMsgs(Context aContext, long aConversationPublicId);
    long saveMsg(Context aContext, Msg aMsg);
    int deleteMsg(Msg aMsg);
}
