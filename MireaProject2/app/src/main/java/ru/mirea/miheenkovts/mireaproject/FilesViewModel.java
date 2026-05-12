package ru.mirea.miheenkovts.mireaproject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FilesViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public FilesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Работа с файлами");
    }

    public LiveData<String> getText() {
        return mText;
    }
}