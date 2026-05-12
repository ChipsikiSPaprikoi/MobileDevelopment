package ru.mirea.miheenkovts.mireaproject.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ru.mirea.miheenkovts.mireaproject.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SettingsViewModel settingsViewModel;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "ProfilePrefs";

    // Ключи для SharedPreferences
    private static final String KEY_NAME = "profile_name";
    private static final String KEY_EMAIL = "profile_email";
    private static final String KEY_AGE = "profile_age";
    private static final String KEY_PHONE = "profile_phone";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Инициализация SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Загрузка сохраненных данных
        loadProfileData();

        // Обработчик кнопки сохранения
        Button saveButton = binding.saveProfileButton;
        saveButton.setOnClickListener(v -> saveProfileData());

        // Заголовок из ViewModel
        settingsViewModel.getText().observe(getViewLifecycleOwner(), binding.textTitle::setText);

        return root;
    }

    private void loadProfileData() {
        binding.editName.setText(sharedPreferences.getString(KEY_NAME, ""));
        binding.editEmail.setText(sharedPreferences.getString(KEY_EMAIL, ""));
        binding.editAge.setText(sharedPreferences.getString(KEY_AGE, ""));
        binding.editPhone.setText(sharedPreferences.getString(KEY_PHONE, ""));
    }

    private void saveProfileData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_NAME, binding.editName.getText().toString());
        editor.putString(KEY_EMAIL, binding.editEmail.getText().toString());
        editor.putString(KEY_AGE, binding.editAge.getText().toString());
        editor.putString(KEY_PHONE, binding.editPhone.getText().toString());

        editor.apply();

        Toast.makeText(getContext(), "Профиль сохранен!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
