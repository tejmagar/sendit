package bca.sendit.filetransfer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import bca.sendit.filetransfer.R;
import bca.sendit.filetransfer.server.auths.AuthToken;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DeviceHolder> {

    private final Context context;
    private final List<AuthToken> authTokens;

    public DevicesAdapter(Context context, List<AuthToken> authTokens) {
        this.context = context;
        this.authTokens = authTokens;
    }
    @NonNull
    @Override
    public DeviceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_auth_device, parent, false);
        return new DeviceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return authTokens.size();
    }

    class DeviceHolder extends RecyclerView.ViewHolder {

        public DeviceHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
