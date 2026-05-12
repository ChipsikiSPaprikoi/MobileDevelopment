package ru.mirea.miheenkovts.mireaproject;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.navigation.Navigation;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import ru.mirea.miheenkovts.mireaproject.databinding.FragmentFilesBinding;

public class FilesFragment extends Fragment {

    private FragmentFilesBinding binding;
    private SharedPreferences prefs;
    private FileAdapter adapter;
    private List<FileItem> fileList = new ArrayList<>();
    private FloatingActionButton fab;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFilesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        prefs = requireContext().getSharedPreferences("FilesPrefs", Context.MODE_PRIVATE);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FileAdapter(fileList, this::onEncryptClick, this::onDecryptClick);
        binding.recyclerView.setAdapter(adapter);

        loadFiles();

        fab = getActivity().findViewById(R.id.fab);
        if (fab != null) {
            fab.setImageResource(android.R.drawable.ic_input_add);
            fab.setOnClickListener(v -> showCreateFileDialog());
        }

        binding.textFilesTitle.setText("Работа с файлами");
        return root;
    }

    private void loadFiles() {
        fileList.clear();

        int fileCount = prefs.getInt("file_count", 0);
        for (int i = 0; i < fileCount; i++) {
            String name = prefs.getString("file_" + i + "_name", "Без названия");
            if (!name.equals("Без названия")) {
                fileList.add(new FileItem(name, "Содержимое файла"));
            }
        }

        adapter.notifyDataSetChanged();
        binding.emptyView.setVisibility(fileList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showCreateFileDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_create_file);

        EditText editName = dialog.findViewById(R.id.edit_file_name);
        EditText editContent = dialog.findViewById(R.id.edit_file_content);
        Button btnSave = dialog.findViewById(R.id.btn_save_file);

        btnSave.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String content = editContent.getText().toString().trim();

            if (!name.isEmpty() && !content.isEmpty()) {
                SharedPreferences.Editor editor = prefs.edit();
                int fileCount = prefs.getInt("file_count", 0);
                editor.putString("file_" + fileCount + "_name", name);
                editor.putString("file_" + fileCount + "_content", content);
                editor.putInt("file_count", fileCount + 1);
                editor.apply();

                Toast.makeText(requireContext(), "Файл '" + name + "' создан!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                loadFiles();
            } else {
                Toast.makeText(requireContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void onEncryptClick(FileItem item) {
        showPasswordDialog(item, true);
    }

    private void onDecryptClick(FileItem item) {
        showPasswordDialog(item, false);
    }

    private void showPasswordDialog(FileItem item, boolean isEncrypt) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_password);

        EditText editPassword = dialog.findViewById(R.id.edit_password);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);

        String action = isEncrypt ? "шифрование" : "дешифрование";
        btnConfirm.setText(action);

        btnConfirm.setOnClickListener(v -> {
            String password = editPassword.getText().toString();
            if (!password.isEmpty()) {
                if (password.equals("123")) {
                    Toast.makeText(requireContext(), item.name + " - " + action + " успешно!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Неверный пароль! (правильный: 123)", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            } else {
                Toast.makeText(requireContext(), "Введите пароль!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (fab != null) {
            fab.setImageResource(android.R.drawable.ic_input_add);
            fab.setOnClickListener(v -> showCreateFileDialog());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (fab != null) {
            fab.setImageResource(android.R.drawable.ic_dialog_email);
            fab.setOnClickListener(v -> Navigation.findNavController(requireView()).navigate(R.id.nav_data));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
