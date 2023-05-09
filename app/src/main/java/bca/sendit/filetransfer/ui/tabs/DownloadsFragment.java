package bca.sendit.filetransfer.ui.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import bca.sendit.filetransfer.R;

public class DownloadsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Feeling lazy to implement room db to save files.
        // Save path in db and open file from uri if exists
        return inflater.inflate(R.layout.fragment_downloads, container, false);
    }
}
