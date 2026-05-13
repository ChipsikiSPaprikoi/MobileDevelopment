package ru.mirea.miheenkovts.mireaproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    public NetworkFragment() { /* required empty constructor */ }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_network, container, false);
        recyclerView = root.findViewById(R.id.posts_recycler);
        progressBar = root.findViewById(R.id.posts_progress);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        loadPosts();

        return root;
    }

    private void loadPosts() {
        progressBar.setVisibility(View.VISIBLE);
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Post>> call = api.getPosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(@NonNull Call<List<Post>> call, @NonNull Response<List<Post>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    PostsAdapter adapter = new PostsAdapter(response.body());
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(requireContext(), "Ошибка получения данных", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Сетевая ошибка: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
