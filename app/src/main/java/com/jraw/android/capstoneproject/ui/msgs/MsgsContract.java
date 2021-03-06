package com.jraw.android.capstoneproject.ui.msgs;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

import com.jraw.android.capstoneproject.data.model.Msg;

/**
 * Created by JonGaming on 05/01/2018.
 * Interface to provide the contract for Msgs.
 */

public interface MsgsContract {
    interface ViewMsgs {
        //Sets ListHandler to use this list.
        void setMsgs(Cursor aList);
        void sendNewMsg();
        void sentNewMsg();//This is so view knows to clear edittext!
        void problemSendingMsg();//This is triggered on exception. Could be more specific but atm just general
        void setPresenter(PresenterMsgs aPresenter);
    }
    interface PresenterMsgs {
        //I hope this is fairly self explanatory. Called by View.
        CursorLoader getMsgs(Context aContext, long aCoPublicId);
        //Used in Search Query to filter Msgs by their content
        CursorLoader getMsgsViaBody(Context aContext, long aCoPublicId, String aText);
        void sendNewMsg(Context aContext, long aCoPublicId, String aCoTitle, String aBody);
    }
}
