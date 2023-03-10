package bca.sendit.filetransfer.ui.tabs;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import bca.sendit.filetransfer.App;
import bca.sendit.filetransfer.R;
import bca.sendit.filetransfer.adapters.FilesAdapter;
import bca.sendit.filetransfer.server.Utils;
import bca.sendit.filetransfer.server.files.FileUploadSession;
import bca.sendit.filetransfer.server.files.OnFilesChangeListener;

public class HomeFragment extends Fragment {
    public static final String TAG = "HomeFragment";
    private String ipAddress;

    private ImageView emptyUploadImageView;
    private final List<Utils.UriFileDetails> details = new ArrayList<>();
    private FilesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_home, container, false);
        ipAddress = "http://127.0.0.1:8080";

        List<String> ipAddresses = App.getIpAddress(8080);
        if (ipAddresses.size() > 0) {
            ipAddress = "http://" + ipAddresses.get(0);
        }

        TextView ipAddressText = view.findViewById(R.id.ip_address);
        ipAddressText.setText(ipAddress);

        ImageView copyUlr = view.findViewById(R.id.copy_url);
        copyUlr.setOnClickListener(v -> {
            if (getContext() != null) {
                ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText(getString(R.string.app_name), ipAddress);
                clipboardManager.setPrimaryClip(clipData);
            }
        });

        RecyclerView filesListRecyclerView = view.findViewById(R.id.upload_files_list_recycler_view);

        filesListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FilesAdapter(getContext(), details, uriFileDetails -> {
            FileUploadSession.denyFile(uriFileDetails.getUri());
            resetAdapterItems();
        });
        filesListRecyclerView.setAdapter(adapter);

        emptyUploadImageView = view.findViewById(R.id.empty_uploads_image_view);
        switchUploadImage();

        FileUploadSession.setCallback(new OnFilesChangeListener() {
            @Override
            public void onAllowed(Uri uri) {

            }

            @Override
            public void onDeny(Uri uri) {

            }

            @Override
            public void onChange() {
                switchUploadImage();
                resetAdapterItems();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        switchUploadImage();
        resetAdapterItems();

    }

    @SuppressLint("NotifyDataSetChanged")
    private void resetAdapterItems() {
        if (getContext() != null) {
            details.clear();
            details.addAll(FileUploadSession.getFilesUriDetails(getContext()));
            adapter.notifyDataSetChanged();
        }
    }

    private void switchUploadImage() {
        emptyUploadImageView.setVisibility((FileUploadSession.getAllowedFiles().size() == 0)
                ? View.VISIBLE : View.GONE);
    }

}
