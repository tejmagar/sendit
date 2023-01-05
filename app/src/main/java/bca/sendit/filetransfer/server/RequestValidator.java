package bca.sendit.filetransfer.server;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

/**
 * This class is used for validating request in views
 */
public class RequestValidator {
    private final Context context;
    private final Request request;
    private boolean shouldAuthenticated = false;
    private boolean shouldReadPermission = false;
    private boolean shouldPrivateMode = false;

    private String errorMessage = null;

    public RequestValidator(Context context, Request request) {
        this.context = context;
        this.request = request;
    }

    public void setShouldAuthenticated(boolean shouldAuthenticated) {
        this.shouldAuthenticated = shouldAuthenticated;
    }

    public void setShouldReadPermission(boolean shouldReadPermission) {
        this.shouldReadPermission = shouldReadPermission;
    }

    public void setShouldPrivateMode(boolean shouldPrivateMode) {
        this.shouldPrivateMode = shouldPrivateMode;
    }

    public boolean isValid() {
        if (shouldAuthenticated == !request.isAuthenticated()) {
            errorMessage = "Not authenticated";
        }

        if (shouldReadPermission && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            errorMessage = "File read permission not granted";
        }

        if (shouldPrivateMode != request.getConfiguration().isPrivateMode) {
            errorMessage = "Cannot access in private mode";
        }

       return errorMessage == null;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
