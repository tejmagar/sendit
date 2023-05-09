package bca.sendit.filetransfer.ui.tabs;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import bca.sendit.filetransfer.App;
import bca.sendit.filetransfer.R;
import bca.sendit.filetransfer.files.FileUploadSession;
import bca.sendit.filetransfer.files.OnSharedFileListener;
import bca.sendit.filetransfer.files.SharedFileItem;
import bca.sendit.filetransfer.ui.adapters.SharedFilesAdapter;

public class HomeFragment extends Fragment {
    public static final String TAG = "HomeFragment";
    private View view;
    private SharedFilesAdapter adapter;
    private final List<SharedFileItem> sharedFileItems = new ArrayList<>();
    private OnSharedFileListener onSharedFileListener;
    private ImageView emptyUploadImageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_home, container,
                false);
        emptyUploadImageView = view.findViewById(R.id.empty_uploads_image_view);

        initSharingUrlInfo();
        initUploadList();
        attachListener();
        return view;
    }

    private String constructSharingUrl(int port) {
        List<String> ipAddresses = App.getServerPort(port);
        String ipAddress = "http://127.0.0.1:" + port;

        if (ipAddresses.size() > 0) {
            ipAddress = "http://" + ipAddresses.get(0);
        }

        return ipAddress;
    }

    private void initUploadList() {
        RecyclerView filesListRecyclerView = view.findViewById(R.id.upload_files_list_recycler_view);
        filesListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SharedFilesAdapter(getContext(), sharedFileItems,
                new SharedFilesAdapter.OnSharedFilesEventListener() {
                    @Override
                    public void onClick(SharedFileItem sharedFileItem) {

                    }

                    @Override
                    public void onRemove(SharedFileItem sharedFileItem) {
                        FileUploadSession.remove(sharedFileItem);
                    }
                });
        filesListRecyclerView.setAdapter(adapter);
    }


    private void initSharingUrlInfo() {
        String ipAddress = constructSharingUrl(App.getServerPort(getContext()));
        TextView ipAddressText = view.findViewById(R.id.ip_address);
        ipAddressText.setText(ipAddress);

        ImageView copyUlr = view.findViewById(R.id.copy_url);
        copyUlr.setOnClickListener(v -> {
            if (getContext() != null) {
                copyText(getContext(), getString(R.string.app_name), ipAddress);
            }
        });
    }

    private void copyText(Context context, String label, String text) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(
                Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(label, text);
        clipboardManager.setPrimaryClip(clipData);
    }

    private void attachListener() {

        onSharedFileListener = new OnSharedFileListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(SharedFileItem sharedFileItem) {
                Log.d(TAG, "Refreshing list");

                // Show default image when there is no uploaded items
                emptyUploadImageView.setVisibility((FileUploadSession.getFiles().size() == 0) ?
                        View.VISIBLE : View.GONE);
                sharedFileItems.clear();
                sharedFileItems.addAll(FileUploadSession.getFiles());
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onFileAdded(SharedFileItem sharedFileItem) {

            }

            @Override
            public void onFileRemoved(SharedFileItem sharedFileItem) {

            }
        };

        FileUploadSession.attachListener(onSharedFileListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (onSharedFileListener != null) {
            FileUploadSession.deAttachListener(onSharedFileListener);
        }
    }
}
