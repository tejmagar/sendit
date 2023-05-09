package bca.sendit.filetransfer.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import bca.sendit.filetransfer.R;
import bca.sendit.filetransfer.files.SharedFileItem;

public class SharedFilesAdapter extends RecyclerView.Adapter<SharedFilesAdapter.ItemViewHolder> {
    public interface OnSharedFilesEventListener {
        void onClick(SharedFileItem sharedFileItem);
        void onRemove(SharedFileItem sharedFileItem);
    }

    private final Context context;
    private final List<SharedFileItem> sharedFileItems;
    private final OnSharedFilesEventListener onSharedFilesEventListener;

    public SharedFilesAdapter(Context context, List<SharedFileItem> sharedFileItems,
                               OnSharedFilesEventListener onSharedFilesEventListener) {
        this.context = context;
        this.sharedFileItems = sharedFileItems;
        this.onSharedFilesEventListener = onSharedFilesEventListener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_shared_file, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        SharedFileItem sharedFileItem = sharedFileItems.get(position);
        holder.filename.setText(sharedFileItem.getName());
        String formattedSize = "Size: " + sharedFileItem.getSize() + " Bytes";
        holder.fileSize.setText(formattedSize);
        holder.removeSharedItemBtn.setOnClickListener(v->
                onSharedFilesEventListener.onRemove(sharedFileItem));
    }

    @Override
    public int getItemCount() {
        return sharedFileItems.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView filename, fileSize;
        ImageView removeSharedItemBtn;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            filename = itemView.findViewById(R.id.filename);
            fileSize = itemView.findViewById(R.id.file_size);
            removeSharedItemBtn = itemView.findViewById(R.id.remove_shared_item_btn);
        }
    }
}
