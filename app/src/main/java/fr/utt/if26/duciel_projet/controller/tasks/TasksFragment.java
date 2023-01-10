package fr.utt.if26.duciel_projet.controller.tasks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fr.utt.if26.duciel_projet.R;
import fr.utt.if26.duciel_projet.controller.tasks.adapters.TaskRecyclerAdapter;
import fr.utt.if26.duciel_projet.databinding.FragmentTasksBinding;
import fr.utt.if26.duciel_projet.models.entity.TaskEntity;
import fr.utt.if26.duciel_projet.viewModel.TasksViewModel;

public class TasksFragment extends Fragment {

    private FragmentTasksBinding binding;
    private TasksViewModel tasksViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        tasksViewModel = new TasksViewModel( this.getActivity().getApplication() );
        binding = FragmentTasksBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = root.findViewById(R.id.tasksRecyclerView);
        TaskRecyclerAdapter adapter = new TaskRecyclerAdapter();

        tasksViewModel.getAllTasks().observe(getViewLifecycleOwner(), (Observer<? super List<TaskEntity>>) o -> {
            System.out.println(o);
            adapter.submitList(o);
        });


        recyclerView.setLayoutManager(new LinearLayoutManager( this.getActivity().getApplicationContext() ));
        recyclerView.setAdapter(adapter);

        Button popupButton = root.findViewById(R.id.buttonPopup);
        popupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AddTaskDialogFragment newFragment = new AddTaskDialogFragment();
                newFragment.show(getParentFragmentManager(), "test");

            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}