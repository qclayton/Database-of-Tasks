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
import com.example.quentinclayton.databaseoftask.adapter.CurrentTasksAdapter;
import com.example.quentinclayton.databaseoftask.database.DBHelper;
import com.example.quentinclayton.databaseoftask.model.ModelSeparator;
import com.example.quentinclayton.databaseoftask.model.ModelTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentTaskFragment extends TaskFragment {



    protected OnTaskDoneListener mOnTaskDoneListener;

    public CurrentTaskFragment() {
        // Required empty public constructor
    }

    public interface OnTaskDoneListener {
        void onTaskDone(ModelTask task);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mOnTaskDoneListener = (OnTaskDoneListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTaskDoneListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_current_task, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvCurrentTasks);

        layoutManager = new LinearLayoutManager(getActivityForTaskFragment());

        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new CurrentTasksAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }


    @Override
    public void findTasks(String title) {
        mAdapter.removeAllItems();
        List<ModelTask> tasks = new ArrayList<>();
        tasks.addAll(activity.getDbHelper().query().getTasks(DBHelper.SELECTION_LIKE_TITLE + " AND "
                        + DBHelper.SELECTION_STATUS + " OR " + DBHelper.SELECTION_STATUS,
                new String[]{"%" + title + "%", Integer.toString(ModelTask.STATUS_CURRENT),
                        Integer.toString(ModelTask.STATUS_OVERDUE)}, DBHelper.TASK_DATE_COLUMN));
        for (int i = 0; i < tasks.size(); i++) {
            addTask(tasks.get(i), false);
        }
    }

    // checkAdapter fix bug
    @Override
    public void checkAdapter() {
        if (mAdapter == null) {
            mAdapter = new CurrentTasksAdapter(this);
            addTaskFromDB();
        }
    }

    @Override
    public void addTaskFromDB() {
        checkAdapter();
        mAdapter.removeAllItems();

        List<ModelTask> tasks = new ArrayList<>();
        tasks.addAll(activity.getDbHelper().query().getTasks(DBHelper.SELECTION_STATUS + " OR "
                + DBHelper.SELECTION_STATUS, new String[]{Integer.toString(ModelTask.STATUS_CURRENT),
                Integer.toString(ModelTask.STATUS_OVERDUE)}, DBHelper.TASK_DATE_COLUMN));
        for (int i = 0; i < tasks.size(); i++) {
            addTask(tasks.get(i), false);
        }
    }


    @Override
    public void addTask(ModelTask newTask, boolean saveToDB) {
        int position = -1;
        ModelSeparator separator = null;

        //sorts tasks by date
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            if (mAdapter.getItem(i).isTask()) {
                ModelTask task = (ModelTask) mAdapter.getItem(i);
                if (newTask.getDate() < task.getDate()) {
                    position = i;
                    break;
                }
            }
        }

        if (newTask.getDate() != 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(newTask.getDate());

            if (calendar.get(Calendar.DAY_OF_YEAR) < Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
                newTask.setDateStatus(ModelSeparator.TYPE_OVERDUE);
                if (!mAdapter.isContainsSeparatorOverdue()) {
                    mAdapter.setContainsSeparatorOverdue(true);
                    separator = new ModelSeparator(ModelSeparator.TYPE_OVERDUE);
                }
            } else if (calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
                newTask.setDateStatus(ModelSeparator.TYPE_TODAY);
                if (!mAdapter.isContainsSeparatorToday()) {
                    mAdapter.setContainsSeparatorToday(true);
                    separator = new ModelSeparator(ModelSeparator.TYPE_TODAY);
                }
            } else if (calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 1) {
                newTask.setDateStatus(ModelSeparator.TYPE_TOMORROW);
                if (!mAdapter.isContainsSeparatorTomorrow()) {
                    mAdapter.setContainsSeparatorTomorrow(true);
                    separator = new ModelSeparator(ModelSeparator.TYPE_TOMORROW);
                }
            } else if (calendar.get(Calendar.DAY_OF_YEAR) > Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 1) {
                newTask.setDateStatus(ModelSeparator.TYPE_TOMORROW);
                if (!mAdapter.isContainsSeparatorFuture()) {
                    mAdapter.setContainsSeparatorFuture(true);
                    separator = new ModelSeparator(ModelSeparator.TYPE_FUTURE);
                }
            }

        }

        //sorts to a position below
        if (position != -1) {

            if (!mAdapter.getItem(position - 1).isTask()) {
                if (position - 2 >= 0 && mAdapter.getItem(position - 2).isTask()) {
                    ModelTask task = (ModelTask) mAdapter.getItem(position - 2);
                    if (task.getDateStatus() == newTask.getDateStatus()) {
                        position -= 1;
                    }
                } else if (position - 2 < 0 && newTask.getDate() == 0) {
                    position -= 1;
                }
            }

            if (separator != null) {
                mAdapter.addItem(position - 1, separator);
            }
            mAdapter.addItem(position, newTask);
        } else {
            if (separator != null) {
                mAdapter.addItem(separator);
            }
            mAdapter.addItem(newTask);
        }

        if (saveToDB) {
            activity.getDbHelper().saveTask(newTask);
        }
    }


    @Override
    public void moveTask(ModelTask task) {
        mAlarmHelper.removeAlarm(task.getTimeStamp());
        mOnTaskDoneListener.onTaskDone(task);
    }
}
