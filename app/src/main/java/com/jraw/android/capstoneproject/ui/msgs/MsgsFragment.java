package com.jraw.android.capstoneproject.ui.msgs;


import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.jraw.android.capstoneproject.R;
import com.jraw.android.capstoneproject.data.model.Msg;
import com.jraw.android.capstoneproject.ui.list.ListHandler;
import com.jraw.android.capstoneproject.ui.list.ListHandlerCallback;
import com.jraw.android.capstoneproject.ui.list.ListRecyclerViewAdapter;

import java.util.List;

/**
 * Handles View part of Msgs functionality.
 * Holds list of msgs.
 *
 * Uses a custom 'ActionBar' called SearchBar. Simply provides a SearchView for the user to query
 * data via Presenter. Should be flexible and easily added to other views. Can also be extended and
 * allows for communication between host and SearchBar. Its basically a View so resides in View part.
 */
public class MsgsFragment extends Fragment implements MsgsContract.ViewMsgs,
        ListHandler.ListHandlerContract,
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = "msgsFragTag";
    private static final String CO_PUBLIC_ID = "coPubId";
    private int mCOPubId;

    private MsgsContract.PresenterMsgs mPresenterMsgs;

    private ListHandler mListHandler;
    private static final String LIST_STATE = "listState";
    private Parcelable mListState;

    public MsgsFragment() {}

    public static MsgsFragment getInstance(int aCOPublicID) {
        MsgsFragment fragment = new MsgsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(CO_PUBLIC_ID,aCOPublicID);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null) {
            mCOPubId = savedInstanceState.getInt(CO_PUBLIC_ID);
            mListState = savedInstanceState.getParcelable(LIST_STATE);
        } else if (getArguments()!=null) {
            mCOPubId = getArguments().getInt(CO_PUBLIC_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_msgs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Set ListHandler here
        RecyclerView recyclerView = view.findViewById(R.id.fragment_msgs_recycler_view);
        mListHandler = new ListHandler(this,
                recyclerView,
                new ListRecyclerViewAdapter(new ListHandlerCallback() {
                    @Override
                    public void onListClick(int aPosition, String aId) {
                        //This is what is set on every item in the list
                        //For now nothing happening on Msg click. Extending would add Msg info? Save/Copy/blah to Msg?
                    }

                    @Override
                    public void onListTouch(View aView, MotionEvent aMotionEvent) {
                        //This is what is set on every item in the list
                    }
                }, R.layout.fragment_list_item_msgs),
                new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.VERTICAL,false));
        Bundle args = new Bundle();
        args.putInt(CO_PUBLIC_ID,mCOPubId);
        getLoaderManager().initLoader(1,args,this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
//        setMsgs(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {}

    @Override
    public void setMsgs(List<Msg> aList) {
        mListHandler.swapMsgs(aList);
        if (mListState!=null) {
            mListHandler.setState(mListState);
        }
    }

    @Override
    public void setPresenter(MsgsContract.PresenterMsgs aPresenter) {
        mPresenterMsgs=aPresenter;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CO_PUBLIC_ID,mCOPubId);
        outState.putParcelable(LIST_STATE,mListHandler.getState());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearListHandler();
    }

    @Override
    public void clearListHandler() {
        mListHandler.clearListHandler();
    }
}