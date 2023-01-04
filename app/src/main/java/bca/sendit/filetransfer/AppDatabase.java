package bca.sendit.filetransfer;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import bca.sendit.filetransfer.server.auths.AuthToken;
import bca.sendit.filetransfer.server.auths.AuthsTokenDao;

@Database(entities = {AuthToken.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AuthsTokenDao authsTokenDao();
}
