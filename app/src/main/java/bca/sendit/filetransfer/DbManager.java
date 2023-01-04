package bca.sendit.filetransfer;

import android.content.Context;

import androidx.room.Room;

public class DbManager {
    private static AppDatabase appDatabase;

    public static AppDatabase get(Context context) {
        // Make sure single app AppDatabase is instance is made
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(context, AppDatabase.class, context.getString(R.string.app_name)).build();
        }

        return appDatabase;
    }

}
