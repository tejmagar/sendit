package bca.sendit.filetransfer.ui.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import bca.sendit.filetransfer.R;
import bca.sendit.filetransfer.server.files.FileUploadSession;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_home, container, false);
        FloatingActionButton fabUpload = view.findViewById(R.id.fab_upload);

        fabUpload.setOnClickListener(v -> {
            FileUploadSession.addFile("/src/hello.txt");
        });
        return view;
    }
}
