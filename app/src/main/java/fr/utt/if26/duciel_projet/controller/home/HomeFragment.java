package fr.utt.if26.duciel_projet.controller.home;

import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.sql.Time;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import fr.utt.if26.duciel_projet.R;
import fr.utt.if26.duciel_projet.databinding.FragmentHomeBinding;
import fr.utt.if26.duciel_projet.models.entity.GlobalSettingEntity;
import fr.utt.if26.duciel_projet.models.entity.RecordEntity;
import fr.utt.if26.duciel_projet.models.entity.TaskEntity;
import fr.utt.if26.duciel_projet.viewModel.GlobalSettingViewModel;
import fr.utt.if26.duciel_projet.viewModel.RecordViewModel;
import fr.utt.if26.duciel_projet.viewModel.TasksViewModel;
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TasksViewModel tasksViewModel;
    private RecordViewModel recordViewModel;

    private TaskEntity chosenTaskEntity;
    private String taskName;
    private ArrayAdapter<String> adapter;
    private Chronometer chronometer;
    private LiveData<List<TaskEntity>> allTaskEntities;
    private Button startButton;
    private Button stopButton;
    private Spinner dropdown;
    private RecordEntity currentRecord;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        tasksViewModel = new TasksViewModel( this.getActivity().getApplication() );
        recordViewModel = new RecordViewModel(this.getActivity().getApplication());

        this.dropdown = root.findViewById(R.id.spinner1);
        this.startButton = root.findViewById(R.id.startRecordButton);
        this.stopButton = root.findViewById(R.id.stopRecordButton);
        this.chronometer = root.findViewById(R.id.chronometer);
        this.adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_dropdown_item);

        dropdown.setAdapter(adapter);
        adapter.add("Aucune sélection");
        dropdown.setSelection(0);

        RecordEntity initialValue = recordViewModel.getCurrentlyRecordingRecord();

        this.startButton.setEnabled(false);
        this.stopButton.setEnabled(false);

        allTaskEntities = tasksViewModel.getAllTasks();

        allTaskEntities.observe(getViewLifecycleOwner(), (Observer<? super List<TaskEntity>>) o -> {
            adapter.clear();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                adapter.add("Aucune sélection");

                o.forEach(item -> {
                    adapter.add(item.getName());
                });

            }
        });

        startButton.setOnClickListener(view1 -> {
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            dropdown.setEnabled(false);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                currentRecord = new RecordEntity(taskName, LocalDateTime.now(),
                        null, true);

                recordViewModel.insertRecord(currentRecord);
            }

        });

        stopButton.setOnClickListener(view12 -> {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                currentRecord.setFinalDate(LocalDateTime.now());
                currentRecord.setCurrentlyRecording(false);

                recordViewModel.updateCurrentlyRecordingRecord(taskName, currentRecord.getStartDate(), LocalDateTime.now());
            }

            chronometer.stop();
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            dropdown.setEnabled(true);
        });

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0)
                    return;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                   Optional<TaskEntity> tempTaskEntity = allTaskEntities.getValue()
                            .stream()
                            .filter(item -> item.getName().equals(adapterView.getItemAtPosition(i)))
                            .findFirst();

                   if(tempTaskEntity.isPresent())
                   {

                       taskName = tempTaskEntity.get().getName();
                       startButton.setEnabled(true);

                   }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if(initialValue != null){
            this.currentRecord = initialValue;

            taskName = initialValue.getTaskName();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                this.chronometer.setBase(
                        SystemClock.elapsedRealtime()
                                + this.currentRecord.getStartDate().atZone(ZoneId.of( "Europe/Paris")).toInstant().toEpochMilli()
                                - System.currentTimeMillis()
                        + 3600000

                );
            }

            chronometer.start();

            this.stopButton.setEnabled(true);
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}