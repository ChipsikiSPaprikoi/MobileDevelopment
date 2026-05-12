package ru.mirea.miheenkovts.mireaproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {
    private List<FileItem> fileList;
    private OnEncryptListener encryptListener;
    private OnDecryptListener decryptListener;

    // Функциональные интерфейсы
    public interface OnEncryptListener {
        void onEncrypt(FileItem item);
    }

    public interface OnDecryptListener {
        void onDecrypt(FileItem item);
    }

    public FileAdapter(List<FileItem> fileList, OnEncryptListener encryptListener, OnDecryptListener decryptListener) {
        this.fileList = fileList;
        this.encryptListener = encryptListener;
        this.decryptListener = decryptListener;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        FileItem item = fileList.get(position);
        holder.fileName.setText(item.name);
        holder.encryptBtn.setOnClickListener(v -> encryptListener.onEncrypt(item));
        holder.decryptBtn.setOnClickListener(v -> decryptListener.onDecrypt(item));
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    static class FileViewHolder extends RecyclerView.ViewHolder {
        TextView fileName;
        Button encryptBtn, decryptBtn;

        FileViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.file_name);
            encryptBtn = itemView.findViewById(R.id.btn_encrypt);
            decryptBtn = itemView.findViewById(R.id.btn_decrypt);
        }
    }
}
