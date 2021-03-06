package com.jraw.android.capstoneproject.ui.list.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.jraw.android.capstoneproject.R;
import com.jraw.android.capstoneproject.data.model.Msg;
import com.jraw.android.capstoneproject.ui.install.InstallFragment;
import com.jraw.android.capstoneproject.utils.Utils;

public class MsgHolder extends AbstractHolder {

    private View mView;
    private RelativeLayout mBodyRL;
    private TextView mDateTV;//datetime text view
    private TextView mBodyTV;//body text view
    private ImageView mTickIV;//This can be used to indicated state of msg.

    public MsgHolder(View view) {
        super(view);
        mView = view;
        mBodyRL = view.findViewById(R.id.list_item_msgs_body_rl);
        mDateTV = view.findViewById(R.id.list_item_msgs_time);
        mBodyTV = view.findViewById(R.id.list_item_msgs_body_tv);
    }

    private void setBodyTVToEnd() {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_END);
        lp.addRule(RelativeLayout.BELOW, R.id.list_item_msgs_time);
        lp.setMargins(4, 4, 4, 4);
        mBodyRL.setLayoutParams(lp);
        mBodyTV.setBackground(mView.getContext().getResources().getDrawable(R.drawable.rounded_corner_sent_msg));
        mBodyTV.setTextColor(mView.getContext().getResources().getColor(R.color.colorPrimary));
    }
    private void setBodyTVToStart() {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_START);
        lp.addRule(RelativeLayout.BELOW, R.id.list_item_msgs_time);
        lp.setMargins(4, 4, 4, 4);
        mBodyRL.setLayoutParams(lp);
        mBodyTV.setBackground(mView.getContext().getResources().getDrawable(R.drawable.rounded_corner_rcd_msg));
        mBodyTV.setTextColor(mView.getContext().getResources().getColor(R.color.colorText));
    }

    private String setViews(Msg aMsg, int aPos) throws Exception {
        String toDisplay=aMsg.getMSEventDate();
        mDateTV.setText(toDisplay);
        toDisplay="";
        String usersTel = mView
                .getContext()
                .getSharedPreferences(Utils.SHAR_PREFS,0).getString(InstallFragment.TEL_NUM,null);
        if (usersTel!=null) {
            if (aMsg.getMSFromTel().equals(usersTel)) {//If msg is not from this user
                setBodyTVToEnd();
            } else {
                setBodyTVToStart();//Needs to be done to ensure View is 'reset' when recycled.
                toDisplay += aMsg.getMSFromTel() + ":\n";
            }
        } else {
            throw new Exception("userstel==null");
        }
        toDisplay+=aMsg.getMSBody();
        mBodyTV.setText(toDisplay);
        return aMsg.getId()+"";
    }

    public String bindData(Msg aMsg, int aPos) {
        try {
            return setViews(aMsg,aPos);
        } catch (Exception e) {
            Utils.logDebug("Error in MsgsHolder.bindData: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void setListener(View.OnClickListener aViewListener) {
        mView.setOnClickListener(aViewListener);
    }

    @Override
    public String toString() {
        return super.toString() + " '" + mDateTV.getText() + " '" + mBodyTV.getText() + "'";
    }
}
