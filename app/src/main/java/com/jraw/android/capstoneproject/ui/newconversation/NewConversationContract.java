package com.jraw.android.capstoneproject.ui.newconversation;

import android.content.Context;
import android.support.v4.content.CursorLoader;

import com.jraw.android.capstoneproject.data.model.Person;

import java.util.List;

public interface NewConversationContract {
    interface ViewNewConversation {
        void setPresenter(PresenterNewConversation aPresenter);
    }
    interface PresenterNewConversation {
        CursorLoader getPersons(Context aContext);
        List<Person> getAddedPersons();
        void addAddedPerson(Person aPerson);
        void removeAddedPerson(Person aPerson);
        void onCreateConv(Context aContext, String aCoTitle);//Will need to take added person list and start new conversation with them.
    }
}
