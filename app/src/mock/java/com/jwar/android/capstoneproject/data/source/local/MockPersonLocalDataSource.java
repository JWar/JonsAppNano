package com.jwar.android.capstoneproject.data.source.local;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

import com.jraw.android.capstoneproject.data.model.Person;
import com.jraw.android.capstoneproject.data.model.cursorwrappers.PersonCursorWrapper;
import com.jraw.android.capstoneproject.data.source.local.PersonLocalDataSource;
import com.jraw.android.capstoneproject.database.DbSchema.PersonTable;
import com.jraw.android.capstoneproject.utils.Utils;

public class MockPersonLocalDataSource implements PersonLocalDataSource {
    private static MockPersonLocalDataSource sInstance=null;
    public static synchronized MockPersonLocalDataSource getInstance() {
        if (sInstance==null) {
            sInstance=new MockPersonLocalDataSource();
        }
        return sInstance;
    }
    private MockPersonLocalDataSource() {}

    @Override
    public CursorLoader getPersons(Context aContext) {
        return new CursorLoader(aContext,
                PersonTable.CONTENT_URI,
                null,
                null,
                null,
                PersonTable.Cols.FIRSTNAME + " ASC"
        );
    }

    @Override
    public Person getPerson(Context aContext, int aPersonId) {
        PersonCursorWrapper personCursorWrapper = new PersonCursorWrapper(
                aContext.getContentResolver().query(
                        PersonTable.CONTENT_URI,
                        null,
                        PersonTable.Cols.ID + "="+aPersonId,
                        null,
                        null
                )
        );
        Person person = personCursorWrapper.getPerson();
        personCursorWrapper.close();
        return person;
    }

    @Override
    public Person getPerson(Context aContext, String aPersonTel) {
        PersonCursorWrapper personCursorWrapper = new PersonCursorWrapper(
                aContext.getContentResolver().query(
                        PersonTable.CONTENT_URI,
                        null,
                        PersonTable.Cols.TELNUM + " LIKE"+
                                "%"+aPersonTel+"%",
                        null,
                        null
                )
        );
        Person person = personCursorWrapper.getPerson();
        personCursorWrapper.close();
        return person;
    }

    @Override
    public Cursor getPersonsFromPeIds(Context aContext, String[] aPeIds) {
        return aContext.getContentResolver().query(
                PersonTable.CONTENT_URI,
                new String[] {
                        PersonTable.Cols.TELNUM
                },
                PersonTable.Cols.ID + " IN (" + Utils.makePlaceholders(aPeIds.length) + ")",
                aPeIds,
                null
        );
    }

    @Override
    public long savePerson(Context aContext, Person aPerson) {
        return ContentUris.parseId(
                aContext.getContentResolver().insert(
                        PersonTable.CONTENT_URI,
                        aPerson.toCV()
                )
        );
    }
}
