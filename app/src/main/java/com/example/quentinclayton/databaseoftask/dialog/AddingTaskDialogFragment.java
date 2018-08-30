package com.example.quentinclayton.databaseoftask.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.example.quentinclayton.databaseoftask.R;
import com.example.quentinclayton.databaseoftask.Utils;
import com.example.quentinclayton.databaseoftask.alarm.AlarmHelper;
import com.example.quentinclayton.databaseoftask.model.ModelTask;

import java.util.Calendar;

public class AddingTaskDialogFragment extends DialogFragment {

    private AddingTaskListener mAddingTaskListener;

    public interface AddingTaskListener {
        void onTaskAdded(ModelTask newTask);

        void onTaskAddingCancel();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mAddingTaskListener = (AddingTaskListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement AddingTaskListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //sets a title for the dialog task
        builder.setTitle(R.string.dialog_title);

        //object for working with Dialog
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //find the layout.dialog_task and all the elements within it.
        View container = inflater.inflate(R.layout.dialog_task, null);

        final TextInputLayout tilTitle = (TextInputLayout) container.findViewById(R.id.tilDialogTaskTitle);
        final EditText etTitle = tilTitle.getEditText();

        TextInputLayout tilDate = (TextInputLayout) container.findViewById(R.id.tilDialogTaskDate);
        final EditText etDate = tilDate.getEditText();

        final TextInputLayout tilTime = (TextInputLayout) container.findViewById(R.id.tilDialogTaskTime);
        final EditText etTime = tilTime.getEditText();

        Spinner spPriority = (Spinner) container.findViewById(R.id.spDialogTaskPriority);

        //adds clues to Edit Text
        tilTitle.setHint(getResources().getString(R.string.task_title));
        tilDate.setHint(getResources().getString(R.string.task_date));
        tilTime.setHint(getResources().getString(R.string.task_time));

        builder.setView(container);

        final ModelTask task = new ModelTask();

        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, ModelTask.PRIORITY_LEVELS);

        spPriority.setAdapter(priorityAdapter);

        spPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                task.setPriority(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Returns the current time
        final Calendar calendar = Calendar.getInstance();
        // A problem without time sets after 1 hour.
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);


        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etDate.length() == 0) {
                    etDate.setText(" ");
                }

                DialogFragment datePickerFragment = new DatePickerFragment() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        etDate.setText(Utils.getDate(calendar.getTimeInMillis()));

                    }

                    //Cancels setting date
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        etDate.setText(null);
                    }
                };
                //Displays the dialog
                datePickerFragment.show(getFragmentManager(), "DatePickerFragment");
            }
        });

        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etTime.length() == 0) {
                    etTime.setText(" ");
                }
                DialogFragment timePickerFragment = new TimePickerFragment() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);
                        etTime.setText(Utils.getTime(calendar.getTimeInMillis()));
                    }
                    //Cancels setting time
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        etTime.setText(null);
                    }
                };
                //Displays the dialog
                timePickerFragment.show(getFragmentManager(), "TimePickerFragment");
            }
        });

        //Creates button OK in the bottom of the dialog
        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                task.setTitle(etTitle.getText().toString());
                task.setStatus(ModelTask.STATUS_CURRENT);
                // Sets the time or date for task from a calendar if a user didn't choose them
                if (etDate.length() != 0 || etTime.length() != 0) {
                    task.setDate(calendar.getTimeInMillis());

                    AlarmHelper alarmHelper = AlarmHelper.getInstance();
                    alarmHelper.setAlarm(task);
                }
                task.setStatus(ModelTask.STATUS_CURRENT);
                mAddingTaskListener.onTaskAdded(task);
                dialog.dismiss();
            }
        });

        //Creates button CANCEL in the bottom of the dialog
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAddingTaskListener.onTaskAddingCancel();
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final Button positiveButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                //prevents the creation of empty task
                if (etTime.length() == 0) {
                    positiveButton.setEnabled(false);
                    tilTitle.setError(getResources().getString(R.string.dialog_error_emrty_title));
                }

                etTitle.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() == 0) {
                            positiveButton.setEnabled(false);
                            tilTitle.setError(getResources().getString(R.string.dialog_error_emrty_title));
                        } else {
                            positiveButton.setEnabled(true);
                            tilTitle.setErrorEnabled(false);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        });

        return alertDialog;
    }
}
