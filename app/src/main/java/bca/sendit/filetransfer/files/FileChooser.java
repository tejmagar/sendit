package bca.sendit.filetransfer.files;

import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class FileChooser {
    private final ActivityResultLauncher<String> activityResultLauncher;

    public FileChooser(AppCompatActivity activity, OnFileChooseListener onFileChooseListener) {
        activityResultLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.GetMultipleContents(), onFileChooseListener::onFilesSelected);
    }

    public FileChooser(Fragment fragment, OnFileChooseListener onFileChooseListener) {
        activityResultLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.GetMultipleContents(), onFileChooseListener::onFilesSelected);
    }

    public void chooseMultipleFiles() {
        activityResultLauncher.launch("*/*");
    }
}
