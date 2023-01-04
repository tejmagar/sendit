package bca.sendit.filetransfer.server.auths;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AuthsTokenDao {
    @Query("SELECT * FROM AuthToken")
    LiveData<List<AuthToken>> getTokens();

    @Query("SELECT * FROM AuthToken where token=:token and active=:active")
    AuthToken getToken(String token, boolean active);

    @Insert
    void addToken(AuthToken authToken);

    @Update
    void updateToken(AuthToken authToken);

    @Delete
    void deleteToken(AuthToken authToken);


}
