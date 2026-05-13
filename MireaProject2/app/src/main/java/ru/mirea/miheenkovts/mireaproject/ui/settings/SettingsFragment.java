package ru.mirea.miheenkovts.mireaproject.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.mirea.miheenkovts.mireaproject.LoginActivity;
import ru.mirea.miheenkovts.mireaproject.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SettingsViewModel settingsViewModel;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "ProfilePrefs";

    private static final String KEY_NAME = "profile_name";
    private static final String KEY_EMAIL = "profile_email";
    private static final String KEY_AGE = "profile_age";
    private static final String KEY_PHONE = "profile_phone";

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();

        loadProfileData();
        populateEmailFromFirebase();
        makeEmailReadOnly();

        binding.saveProfileButton.setOnClickListener(v -> saveProfileData());

        binding.logoutButton.setOnClickListener(v -> logout());

        settingsViewModel.getText().observe(getViewLifecycleOwner(), binding.textTitle::setText);

        return root;
    }

    private void populateEmailFromFirebase() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            if (email != null) {
                binding.editEmail.setText(email);
            }
        }
    }

    private void makeEmailReadOnly() {
        binding.editEmail.setFocusable(false);
        binding.editEmail.setClickable(false);
        binding.editEmail.setLongClickable(false);
        binding.editEmail.setCursorVisible(false);
    }

    private void loadProfileData() {
        binding.editName.setText(sharedPreferences.getString(KEY_NAME, ""));
        String savedEmail = sharedPreferences.getString(KEY_EMAIL, "");
        if (!savedEmail.isEmpty() && (binding.editEmail.getText() == null || binding.editEmail.getText().toString().isEmpty())) {
            binding.editEmail.setText(savedEmail);
        }
        binding.editAge.setText(sharedPreferences.getString(KEY_AGE, ""));
        binding.editPhone.setText(sharedPreferences.getString(KEY_PHONE, ""));
    }

    private void saveProfileData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_NAME, binding.editName.getText().toString());
        editor.putString(KEY_EMAIL, binding.editEmail.getText() != null ? binding.editEmail.getText().toString() : "");
        editor.putString(KEY_AGE, binding.editAge.getText().toString());
        editor.putString(KEY_PHONE, binding.editPhone.getText().toString());

        editor.apply();

        Toast.makeText(getContext(), "Профиль сохранен!", Toast.LENGTH_SHORT).show();
    }

    private void logout() {
        mAuth.signOut();
        Toast.makeText(getContext(), "Выход выполнен", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
