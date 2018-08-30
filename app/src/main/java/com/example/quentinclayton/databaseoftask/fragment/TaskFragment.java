package com.example.quentinclayton.databaseoftask.fragment;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.quentinclayton.databaseoftask.MainActivity;
import com.example.quentinclayton.databaseoftask.R;
import com.example.quentinclayton.databaseoftask.adapter.TaskAdapter;
import com.example.quentinclayton.databaseoftask.alarm.AlarmHelper;
import com.example.quentinclayton.databaseoftask.dialog.EditTaskDialogFragment;
import com.example.quentinclayton.databaseoftask.model.Item;
import com.example.quentinclayton.databaseoftask.model.ModelTask;

/**
 * Created by SnowFlake on 05.02.2016.
 */
public abstract class TaskFragment
        extends Fragment {

    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager layoutManager;

    protected TaskAdapter mAdapter;

    protected MainActivity activity;

    protected AlarmHelper mAlarmHelper;

    public MainActivity getActivityForTaskFragment() {
        return activity;
    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivityForTaskFragment() != null) {
            activity = (MainActivity) getActivityForTaskFragment();
        }

        mAlarmHelper = AlarmHelper.getInstance();


        setActivity((MainActivity) getActivity());
        addTaskFromDB();
    }

    public abstract void addTask(ModelTask newTask, boolean saveToDB);

    public void updateTask(ModelTask task) {
        mAdapter.updateTask(task);
    }

    public void removeTaskDialog(final int location) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivityForTaskFragment());
        dialogBuilder.setMessage(R.string.dialog_removing_message);

        Item item = mAdapter.getItem(location);

        if (item.isTask()) {

            ModelTask removingTask = (ModelTask) item;

            final long timeStamp = removingTask.getTimeStamp();

            final boolean[] isRemoved = {false};

            dialogBuilder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mAdapter.removeItem(location);
                    isRemoved[0] = true;
                    Snackbar snackbar = Snackbar.make(getActivityForTaskFragment().findViewById(R.id.coordinator),
                            R.string.removed, Snackbar.LENGTH_LONG);
                    snackbar.setAction(R.string.dialog_cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addTask(activity.getDbHelper().query().getTask(timeStamp), false);
                            isRemoved[0] = false;
                        }
                    });
                    snackbar.getView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                        @Override
                        public void onViewAttachedToWindow(View v) {

                        }

                        @Override
                        public void onViewDetachedFromWindow(View v) {
                            if (isRemoved[0]) {
                                mAlarmHelper.removeAlarm(timeStamp);
                                activity.getDbHelper().removeTask(timeStamp);
                            }
                        }
                    });

                    snackbar.show();

                    dialog.dismiss();
                }
            });

            dialogBuilder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }

        dialogBuilder.show();
    }

    public void showTaskEditDialog(ModelTask task) {
        DialogFragment editingTaskDialog = EditTaskDialogFragment.newInstance(task);
        editingTaskDialog.show(getActivityForTaskFragment().getFragmentManager(), "EditTaskDialogFragment");
    }

    public abstract void findTasks(String title);

    public abstract void checkAdapter();

    public abstract void addTaskFromDB();

    public abstract void moveTask(ModelTask task);
}
