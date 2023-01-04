package bca.sendit.filetransfer.server;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

/**
 * This class is used for validating request in views
 */
public class RequestValidator {
    private final Context context;
    private final Request request;
    private boolean shouldAuthenticated = false;
    private boolean checkReadPermission = false;
    private boolean isPrivateMode = false;

    private String errorMessage = null;

    public RequestValidator(Context context, Request request) {
        this.context = context;
        this.request = request;
    }

    public void setShouldAuthenticated(boolean shouldAuthenticated) {
        this.shouldAuthenticated = shouldAuthenticated;
    }

    public void setCheckReadPermission(boolean checkReadPermission) {
        this.checkReadPermission = checkReadPermission;
    }

    public void setShouldPrivateMode(boolean privateMode) {
        isPrivateMode = privateMode;
    }

    public boolean isValid() {
        if (shouldAuthenticated && !request.isAuthenticated()) {
            errorMessage = "Not authenticated";
        }

        if (checkReadPermission && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            errorMessage = "File read permission not granted";
        }

        if (isPrivateMode && request.getConfiguration().isPrivateMode) {
            errorMessage = "Cannot access in private mode";
        }

       return errorMessage == null;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
