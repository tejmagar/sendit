package bca.sendit.filetransfer.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import bca.sendit.filetransfer.R;
import bca.sendit.filetransfer.server.Utils;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FileViewHolder> {
    public interface OnActionListener {
        void removeAccess(Utils.UriFileDetails uriFileDetails);
    }

    private final Context context;
    private final List<Utils.UriFileDetails> uriFileDetails;
    private final OnActionListener onActionListener;

    public FilesAdapter(Context context, List<Utils.UriFileDetails> uriFileDetails,
                        OnActionListener onActionListener) {
        this.context = context;
        this.uriFileDetails = uriFileDetails;
        this.onActionListener = onActionListener;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_file_details, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        Utils.UriFileDetails details = uriFileDetails.get(position);
        holder.filename.setText(details.getFilename());
        String fileSize = "File size: " + details.getSize() + " Bytes";
        holder.fileSize.setText(fileSize);

        holder.removeImageView.setOnClickListener(v-> onActionListener.removeAccess(details));
    }

    @Override
    public int getItemCount() {
        return uriFileDetails.size();
    }

    static class FileViewHolder extends RecyclerView.ViewHolder {

        TextView filename, fileSize;
        ImageView removeImageView;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            filename = itemView.findViewById(R.id.filename);
            fileSize = itemView.findViewById(R.id.file_size);
            removeImageView = itemView.findViewById(R.id.remove_image_view);
        }
    }
}
