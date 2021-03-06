package com.jwar.android.capstoneproject;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jraw.android.capstoneproject.data.repository.ConversationRepository;
import com.jraw.android.capstoneproject.data.repository.MsgRepository;
import com.jraw.android.capstoneproject.data.repository.PeCoRepository;
import com.jraw.android.capstoneproject.data.repository.PersonRepository;
import com.jraw.android.capstoneproject.data.source.local.ConversationLocalDataSource;
import com.jraw.android.capstoneproject.data.source.local.MsgLocalDataSource;
import com.jraw.android.capstoneproject.data.source.local.PeCoLocalDataSource;
import com.jraw.android.capstoneproject.data.source.local.PersonLocalDataSource;
import com.jraw.android.capstoneproject.data.source.remote.BackendApi;
import com.jraw.android.capstoneproject.data.source.remote.MsgRemoteDataSource;
import com.jraw.android.capstoneproject.data.source.remote.PersonRemoteDataSource;
import com.jwar.android.capstoneproject.data.source.local.MockConversationLocalDataSource;
import com.jwar.android.capstoneproject.data.source.local.MockMsgLocalDataSource;
import com.jwar.android.capstoneproject.data.source.local.MockPeCoLocalDataSource;
import com.jwar.android.capstoneproject.data.source.local.MockPersonLocalDataSource;
import com.jwar.android.capstoneproject.data.source.remote.MockMsgRemoteDataSource;
import com.jwar.android.capstoneproject.data.source.remote.MockPersonRemoteDataSource;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by JonGaming on 16/04/2018.
 */

public class Injection {
    public static PeCoRepository providePeCoRepository(@NonNull PeCoLocalDataSource aPeCoLocalDataSource) throws Exception {
        return PeCoRepository.getInstance(aPeCoLocalDataSource);
    }
    public static PersonRepository providePersonRepository(@NonNull PersonLocalDataSource aPersonLocalDataSource,
                                                           @NonNull PersonRemoteDataSource aPersonRemoteDataSource) throws Exception {
        return PersonRepository.getInstance(
                aPersonLocalDataSource,
                aPersonRemoteDataSource);
    }
    public static ConversationRepository provideConversationRepository(@NonNull ConversationLocalDataSource aConversationLocalDataSource) throws Exception {
        return ConversationRepository.getInstance(aConversationLocalDataSource);
    }
    public static MsgRepository provideMsgRepository(@NonNull MsgLocalDataSource aMsgLocalDataSource,
                                                     @NonNull MsgRemoteDataSource aMsgRemoteDataSource,
                                                     @NonNull ConversationLocalDataSource aConversationLocalDataSource) throws Exception {
        return MsgRepository.getInstance(
                aMsgLocalDataSource,
                aMsgRemoteDataSource,
                aConversationLocalDataSource);
    }
    public static ConversationLocalDataSource provideConversationLocalDataSource() throws Exception {
        return MockConversationLocalDataSource.getInstance();
    }
    public static MsgLocalDataSource provideMsgLocalDataSource() throws Exception {
        return MockMsgLocalDataSource.getInstance();
    }
    public static MsgRemoteDataSource provideMsgRemoteDataSource(@NonNull BackendApi aBackendApi) {
        return MockMsgRemoteDataSource.getInstance(aBackendApi);
    }
    public static PersonRemoteDataSource providePersonRemoteDataSource(@NonNull BackendApi aBackendApi) {
        return MockPersonRemoteDataSource.getInstance(aBackendApi);
    }
    public static PersonLocalDataSource providePersonLocalDataSource() {
        return MockPersonLocalDataSource.getInstance();
    }
    public static PeCoLocalDataSource providePeCoLocalDataSource() {
        return MockPeCoLocalDataSource.getInstance();
    }
    public static BackendApi provideBackendApi() throws Exception {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BackendApi.END_POINT)
                .addConverterFactory(GsonConverterFactory.create(provideGson()))
                .build();
        return retrofit.create(BackendApi.class);
    }
    private static Gson provideGson() {
        return new GsonBuilder().create();
    }
    //Sets androidTest expected response for Mock
    public static String getTestConvTitle() {
        return "First conv";
    }
    //Sets androidTest expected response for Mock
    public static String getTestMsgBody() {
        return "I'm here";
    }
    //This is for making sure Mock AndroidTest of ConversationActivity doesnt trigger install fragment.
    public static boolean getIsInstallDefault() {
        return true;
    }
    //If mock build then log debug, so true.
    public static boolean isDebug() {return true;}
}
