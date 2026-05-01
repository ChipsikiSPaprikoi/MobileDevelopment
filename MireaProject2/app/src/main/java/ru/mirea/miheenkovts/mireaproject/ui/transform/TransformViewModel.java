package ru.mirea.miheenkovts.mireaproject.ui.transform;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;

public class TransformViewModel extends ViewModel {

    private final MutableLiveData<String> mStatusText;
    private final MutableLiveData<File> mCapturedImageFile;

    public TransformViewModel() {
        mStatusText = new MutableLiveData<>();
        mCapturedImageFile = new MutableLiveData<>();

        mStatusText.setValue("Разрешите CAMERA и нажмите Сфотографировать");
        mCapturedImageFile.setValue(null);
    }

    public LiveData<String> getStatusText() {
        return mStatusText;
    }

    public LiveData<File> getCapturedImageFile() {
        return mCapturedImageFile;
    }

    public void photoTaken(File imageFile) {
        mCapturedImageFile.setValue(imageFile);
        mStatusText.setValue("Фото сделано, коллаж готов");
    }

    public void clearPhoto() {
        mCapturedImageFile.setValue(null);
        mStatusText.setValue("Разрешите CAMERA и нажмите Сфотографировать");
    }
}
