package ru.mirea.miheenkovts.mireaproject.ui.transform;

import static android.Manifest.permission.CAMERA;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import ru.mirea.miheenkovts.mireaproject.R;
import ru.mirea.miheenkovts.mireaproject.databinding.FragmentTransformBinding;

public class TransformFragment extends Fragment {

    private FragmentTransformBinding binding;
    private TransformViewModel mViewModel;

    private PreviewView previewView;
    private Button btnCapture;
    private ImageView ivCollage;
    private Button btnNewCollage;
    private TextView tvStatus;

    private ImageCapture imageCapture;
    private ProcessCameraProvider cameraProvider;
    private Camera camera;
    private Executor cameraExecutor;

    private static final int REQUEST_CODE_PERMISSIONS = 1001;
    private static final String[] REQUIRED_PERMISSIONS = { CAMERA };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentTransformBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mViewModel = new ViewModelProvider(this).get(TransformViewModel.class);

        previewView = binding.previewView;
        btnCapture = binding.btnCapture;
        ivCollage = binding.ivCollage;
        btnNewCollage = binding.btnNewCollage;
        tvStatus = binding.tvStatus;

        mViewModel.getStatusText().observe(getViewLifecycleOwner(), status -> {
            tvStatus.setText(status);
        });

        mViewModel.getCapturedImageFile().observe(getViewLifecycleOwner(), file -> {
            if (file != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                if (bitmap != null) {
                    ivCollage.setImageBitmap(bitmap);
                    ivCollage.setVisibility(View.VISIBLE);
                    btnNewCollage.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getContext(), "Не удалось загрузить фото", Toast.LENGTH_SHORT).show();
                }
            } else {
                ivCollage.setVisibility(View.GONE);
                btnNewCollage.setVisibility(View.GONE);
            }
        });

        btnCapture.setOnClickListener(v -> takePhoto());

        btnNewCollage.setOnClickListener(v -> {
            mViewModel.clearPhoto();
            ivCollage.setImageBitmap(null);
            ivCollage.setVisibility(View.GONE);
            btnNewCollage.setVisibility(View.GONE);
        });

        cameraExecutor = ContextCompat.getMainExecutor(requireContext());

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                tvStatus.setText("Требуется разрешение CAMERA");
                Toast.makeText(getContext(), "Нет разрешения камеры", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();

                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                tvStatus.setText("Ошибка камеры: " + e.getMessage());
            }
        }, cameraExecutor);
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        camera = cameraProvider.bindToLifecycle(
                (LifecycleOwner) this,
                cameraSelector,
                preview,
                imageCapture);

        preview.setSurfaceProvider(previewView.getSurfaceProvider());
    }

    private void takePhoto() {
        if (imageCapture == null || cameraProvider == null || camera == null) {
            tvStatus.setText("Камера не готова");
            Toast.makeText(getContext(), "Камера не готова", Toast.LENGTH_SHORT).show();
            return;
        }

        File photoFile = createImageFile();
        if (photoFile == null) {
            tvStatus.setText("Не удалось создать файл");
            Toast.makeText(getContext(), "Не удалось создать файл", Toast.LENGTH_SHORT).show();
            return;
        }

        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(
                outputOptions,
                cameraExecutor,
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        // Отправляем файл в ViewModel
                        mViewModel.photoTaken(photoFile);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        tvStatus.setText("Ошибка фото: " + exception.getMessage());
                        Toast.makeText(getContext(), "Ошибка фото: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private File createImageFile() {
        Context context = requireContext();

        // Вариант 1: пишем во внутренний каталог приложения (без разрешения WRITE_EXTERNAL_STORAGE)
        File imageDir = new File(context.getFilesDir(), "camera_photos");
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "JPEG_" + timeStamp + ".jpg";

        return new File(imageDir, fileName);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
    }
}
