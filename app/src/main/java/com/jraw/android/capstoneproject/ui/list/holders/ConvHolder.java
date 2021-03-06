package com.jraw.android.capstoneproject.ui.list.holders;

import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import com.jraw.android.capstoneproject.R;
import com.jraw.android.capstoneproject.data.model.Conversation;

public class ConvHolder extends AbstractHolder {
    private View mView;
    private TextView mTitleTV;
    private TextView mSnippetTV;
    private TextView mUnreadCountTV;

    public ConvHolder(View view) {
        super(view);
        mView = view;
        mTitleTV = view.findViewById(R.id.list_item_convs_title);
        mSnippetTV = view.findViewById(R.id.list_item_convs_snippet);
        mUnreadCountTV = view.findViewById(R.id.list_item_convs_unread_count);
    }
    public String bindData(Conversation aConversation, int aPos) {
        return setViews(aConversation,aPos);
    }
    private String setViews(Conversation aConv, int aPos) {
        mTitleTV.setText(aConv.getCOTitle());
        mSnippetTV.setText(aConv.getCOSnippet());
        int unreadCount = aConv.getCOUnread();
        String toDisplay = ""+unreadCount;
        mUnreadCountTV.setText(toDisplay);
        if (unreadCount>0) {//Boldify everything if there are unread msgs.
            mTitleTV.setTypeface(mTitleTV.getTypeface(),Typeface.BOLD);
            mSnippetTV.setTypeface(mSnippetTV.getTypeface(),Typeface.BOLD);
            mUnreadCountTV.setTypeface(mUnreadCountTV.getTypeface(),Typeface.BOLD);
        }
        return aConv.getCOPublicId()+"/"+aConv.getCOTitle();
    }

    @Override
    public void setListener(View.OnClickListener aListener) {
        mView.setOnClickListener(aListener);
    }
    @Override
    public String toString() {
        return super.toString() + " '" + mTitleTV.getText() + " '";
    }
}