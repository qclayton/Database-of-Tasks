package com.example.quentinclayton.databaseoftask.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quentinclayton.databaseoftask.R;
import com.example.quentinclayton.databaseoftask.adapter.DoneTaskAdapter;
import com.example.quentinclayton.databaseoftask.database.DBHelper;
import com.example.quentinclayton.databaseoftask.model.ModelTask;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DoneTaskFragment extends TaskFragment {

    protected OnTaskRestoreListener mOnTaskRestoreListener;

    public DoneTaskFragment() {
        // Required empty public constructor
    }

    public interface OnTaskRestoreListener {
        void onTaskRestore(ModelTask task);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mOnTaskRestoreListener = (OnTaskRestoreListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTaskRestoreListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_done_task, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvDoneTasks);

        layoutManager = new LinearLayoutManager(getActivityForTaskFragment());
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new DoneTaskAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        // Inflate the layout for this fragment
        return rootView;
    }


    @Override
    public void findTasks(String title) {
        mAdapter.removeAllItems();
        List<ModelTask> tasks = new ArrayList<>();
        tasks.addAll(activity.getDbHelper().query().getTasks(DBHelper.SELECTION_LIKE_TITLE + " AND "
                + DBHelper.SELECTION_STATUS, new String[]{"%" + title + "%",
                Integer.toString(ModelTask.STATUS_DONE)}, DBHelper.TASK_DATE_COLUMN));
        for (int i = 0; i < tasks.size(); i++) {
            addTask(tasks.get(i), false);
        }
    }

    @Override
    public void checkAdapter() {
        if (mAdapter == null) {
            mAdapter = new DoneTaskAdapter(this);
            addTaskFromDB();
        }
    }

    @Override
    public void addTaskFromDB() {
        checkAdapter();
        mAdapter.removeAllItems();
        List<ModelTask> tasks = new ArrayList<>();
        tasks.addAll(activity.getDbHelper().query().getTasks(DBHelper.SELECTION_STATUS,
                new String[]{Integer.toString(ModelTask.STATUS_DONE)}, DBHelper.TASK_DATE_COLUMN));
        for (int i = 0; i < tasks.size(); i++) {
            addTask(tasks.get(i), false);
        }
    }

    @Override
    public void addTask(ModelTask newTask, boolean saveToDB) {
        int position = -1;

        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            if (mAdapter.getItem(i).isTask()) {
                ModelTask task = (ModelTask) mAdapter.getItem(i);
                if (newTask.getDate() < task.getDate()) {
                    position = i;
                    break;
                }
            }
        }

        if (position != -1) {
            mAdapter.addItem(position, newTask);
        } else {
            mAdapter.addItem(newTask);
        }

        if (saveToDB) {
            activity.getDbHelper().saveTask(newTask);
        }
    }


    @Override
    public void moveTask(ModelTask task) {
        if (task.getDate() != 0) {
            mAlarmHelper.setAlarm(task);
        }
        mOnTaskRestoreListener.onTaskRestore(task);
    }
}
