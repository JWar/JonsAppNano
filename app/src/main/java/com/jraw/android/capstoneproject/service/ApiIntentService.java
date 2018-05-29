package com.jraw.android.capstoneproject.service;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.jraw.android.capstoneproject.R;
import com.jraw.android.capstoneproject.data.model.Conversation;
import com.jraw.android.capstoneproject.data.model.Msg;
import com.jraw.android.capstoneproject.data.model.Person;
import com.jraw.android.capstoneproject.data.repository.ConversationRepository;
import com.jraw.android.capstoneproject.data.repository.MsgRepository;
import com.jraw.android.capstoneproject.data.repository.PersonRepository;
import com.jraw.android.capstoneproject.ui.msgs.MsgsActivity;
import com.jraw.android.capstoneproject.ui.widget.CapstoneAppWidgetProvider;
import com.jraw.android.capstoneproject.utils.Utils;
import com.jwar.android.capstoneproject.Injection;

import java.util.List;

/**
 * Handles getting and saving msgs via Retrofit.
 * Also handles notifications.
 * Also handles Widget via static method in provider
 */
public class ApiIntentService extends IntentService {

    private static final String ACTION_GET_NEW_MSGS = "com.jraw.android.capstoneproject.service.action.GET_NEW_MSGS";
    private static final String ACTION_SEND_NEW_MSG = "com.jraw.android.capstoneproject.service.action.SEND_NEW_MSG";

    private static final String EXTRA_MSG = "com.jraw.android.capstoneproject.service.extra.msg";

    //Urgh dont like this at all. Storing static field in IntentService sounds like a bad idea.
    private static MsgRepository sMsgRepository;
    private static ConversationRepository sConversationRepository;
    private static PersonRepository sPersonRepository;

    private int mNotificationId=1;

    public ApiIntentService() {
        super("ApiIntentService");
    }

    /**
     * Triggers app to get all msgs stored in server for this phone.
     */
    public static void startActionGetNewMsgs(Context context) {
        Intent intent = new Intent(context, ApiIntentService.class);
        intent.setAction(ACTION_GET_NEW_MSGS);
        context.startService(intent);
    }

    /**
     * Starts this service to perform send new msg. Requires Msg to be sent, strangely enough...
     */
    public static void startActionSendNewMsgs(Context context, Msg aMsg) {
        Intent intent = new Intent(context, ApiIntentService.class);
        intent.setAction(ACTION_SEND_NEW_MSG);
        intent.putExtra(EXTRA_MSG, aMsg);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            if (sMsgRepository==null) {
                try {//Init Repo if null
                    sMsgRepository = Injection.provideMsgRepository(
                            Injection.provideMsgLocalDataSource(),
                            Injection.provideMsgRemoteDataSource(
                                    Injection.provideBackendApi()),
                            Injection.provideConversationLocalDataSource());
                } catch (Exception e) {
                    Utils.logDebug("Problem in ApiIntentService.onHandleIntent: MsgRepo init");
                    showToastMsg("Problem initialising Messages");
                    return;
                }
            }
            if (sConversationRepository==null) {
                try {//Init Repo if null
                    sConversationRepository = Injection.provideConversationRepository(
                            Injection.provideConversationLocalDataSource());
                } catch (Exception e) {
                    Utils.logDebug("Problem in ApiIntentService.onHandleIntent: ConvRepo init");
                    showToastMsg("Problem initialising Conversations");
                    return;
                }
            }
            if (sPersonRepository==null) {
                try {//Init Repo if null
                    sPersonRepository = Injection.providePersonRepository(
                            Injection.providePersonLocalDataSource(),
                            Injection.providePersonRemoteDataSource(
                                    Injection.provideBackendApi()
                            ));
                } catch (Exception e) {
                    Utils.logDebug("Problem in ApiIntentService.onHandleIntent: PersonRepo init");
                    showToastMsg("Problem initialising Persons");
                    return;
                }
            }
            final String action = intent.getAction();
            if (ACTION_GET_NEW_MSGS.equals(action)) {
                handleActionGetNewMsgs();
            } else if (ACTION_SEND_NEW_MSG.equals(action)) {
                Msg newMsg = intent.getParcelableExtra(EXTRA_MSG);
                handleActionSendNewMsg(newMsg);
            }
        }
    }

    /**
     * Handle action get new msgs to check server for all messages pending for this phone
     */
    private void handleActionGetNewMsgs() {
        try {
            List<Msg> newMsgs = sMsgRepository.getNewMsgs(this);
            if (newMsgs!=null) {
                //Successfully save this number of new msgs. Just debug, no need to let user know.
                //TODO:This will need to update/add notifications AND update widget. Will need msgs for notifs?
                //So getNewMsgs cant just return the num of msgs, will need to return msg list...
                handleNotifications(newMsgs);
                updateWidgetConversations();
                Utils.logDebug("ApiIntentService.handleActionGetNewMsgs: saved " + newMsgs.size() + " from server!");
            } else {
                //Notify user that there has been a problem with getting new msgs.
                showToastMsg("Problem getting new msgs");
            }
        } catch (Exception e) {
            Utils.logDebug("ApiIntentService.handleActionSendNewMsg: "+e.getLocalizedMessage());
            showToastMsg("Problem getting new msgs");
        }
    }
    private void updateWidgetConversations() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, CapstoneAppWidgetProvider.class));
        Conversation[] conversations = sConversationRepository.getConversationsTopTwo(this);
        for (int appWidgetId: appWidgetIds) {
            CapstoneAppWidgetProvider.updateWidgetConversations(this, appWidgetManager, appWidgetId,
                    conversations);
        }
    }
    //Need to check presence of already set notifications.
    //Need to update notifications on read. Need to therefore store public id against the
    private void handleNotifications(List<Msg> aMsgList) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        NotificationCompat.Builder mBuilder;
        switch (aMsgList.size()) {
            case 1:
                Msg msg = aMsgList.get(0);
                Person person = sPersonRepository.getPerson(this,msg.getMSFromTel());
                PendingIntent pendingIntent = PendingIntent.getActivity(
                        this,
                        0,
                        MsgsActivity.getIntent(this,msg.getMSCOPublicId(),msg.getMSCOTitle()),
                        0);
                mBuilder = new NotificationCompat.Builder(this, Utils.CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_baseline_message_24px)
                        .setContentTitle(person.getFullName())
                        .setContentText(msg.getBodySnippet())
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                notificationManager.notify(mNotificationId, mBuilder.build());
                break;
            case 0://Shouldnt have a msg list of 0...
                break;
            default:
                //Set notification title as size of list + notification new msg string
                //TODO: this will not work beyond the first unread msg notification.
                //Will need a running total of unread msgs.
                //Perhaps can just have a more than 1 unread msg count.
                //Possible to get notification? So can check notification then get data from it.
                String toDisplay = aMsgList.size()+ " " + getString(R.string.notification_unread_msgs);
                mBuilder = new NotificationCompat.Builder(this, Utils.CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_baseline_message_24px)
                        .setContentText(toDisplay)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                notificationManager.notify(mNotificationId, mBuilder.build());
        }
    }
    /**
     * Handle action SendNewMsg in the provided background thread with the provided
     * parameters. Uses Retrofit to send Msg.
     */
    private void handleActionSendNewMsg(Msg aMsg) {
        try {
            if (sMsgRepository.saveMsg(this, aMsg)>0) {
                Utils.logDebug("ApiIntentService.handleActionSendMsg: Msg sent successfully. Msg Id: "+aMsg.getId());
            } else {
                showToastMsg(getString(R.string.send_msg_error));
            }
        } catch (Exception e) {
            Utils.logDebug("ApiIntentService.handleActionSendNewMsg: "+e.getLocalizedMessage());
        }
    }
    private void showToastMsg(final String aText) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), aText, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
