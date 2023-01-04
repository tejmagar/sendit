package bca.sendit.filetransfer.server.auths;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class AuthToken {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "token")
    public String token;

    @ColumnInfo(name = "active")
    public boolean active;

    public AuthToken() {

    }

    public AuthToken(String token, boolean isActive) {
        this.token = token;
        this.active = isActive;
    }
}
