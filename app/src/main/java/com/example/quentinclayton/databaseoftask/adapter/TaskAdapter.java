package com.example.quentinclayton.databaseoftask.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.quentinclayton.databaseoftask.R;
import com.example.quentinclayton.databaseoftask.fragment.TaskFragment;
import com.example.quentinclayton.databaseoftask.model.Item;
import com.example.quentinclayton.databaseoftask.model.ModelSeparator;
import com.example.quentinclayton.databaseoftask.model.ModelTask;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public abstract class TaskAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected List<Item> items;

    protected TaskFragment mTaskFragment;

    protected boolean mContainsSeparatorOverdue;
    protected boolean mContainsSeparatorToday;
    protected boolean mContainsSeparatorTomorrow;
    protected boolean mContainsSeparatorFuture;

    public boolean isContainsSeparatorOverdue() {
        return mContainsSeparatorOverdue;
    }

    public void setContainsSeparatorOverdue(boolean containsSeparatorOverdue) {
        this.mContainsSeparatorOverdue = containsSeparatorOverdue;
    }

    public boolean isContainsSeparatorToday() {
        return mContainsSeparatorToday;
    }

    public void setContainsSeparatorToday(boolean containsSeparatorToday) {
        this.mContainsSeparatorToday = containsSeparatorToday;
    }

    public boolean isContainsSeparatorTomorrow() {
        return mContainsSeparatorTomorrow;
    }

    public void setContainsSeparatorTomorrow(boolean containsSeparatorTomorrow) {
        this.mContainsSeparatorTomorrow = containsSeparatorTomorrow;
    }

    public boolean isContainsSeparatorFuture() {
        return mContainsSeparatorFuture;
    }

    public void setContainsSeparatorFuture(boolean containsSeparatorFuture) {
        this.mContainsSeparatorFuture = containsSeparatorFuture;
    }

    public TaskAdapter(TaskFragment taskFragment) {
        this.mTaskFragment = taskFragment;
        items = new ArrayList<>();
    }
    // returns position of list item
    public Item getItem(int position) {
        return items.get(position);
    }
    // Adds an item of list
    public void addItem(Item item) {
        items.add(item);
        notifyItemInserted(getItemCount() - 1);
    }
    // adds an element to a certain position
    public void addItem(int location, Item item) {
        items.add(location, item);
        notifyItemInserted(location);
    }

    public void updateTask(ModelTask newTask) {
        for (int i = 0; i < getItemCount(); i++) {
            if (getItem(i).isTask()) {
                ModelTask task = (ModelTask) getItem(i);
                if (newTask.getTimeStamp() == task.getTimeStamp()) {
                    removeItem(i);
                    getTaskFragment().addTask(newTask, false);
                }
            }
        }
    }
    // removes Item
    public void removeItem(int location) {
        if (location >= 0 && location <= getItemCount() - 1) {
            items.remove(location);
            notifyItemRemoved(location);
            if (location - 1 >= 0 && location <= getItemCount() - 1) {
                if (!getItem(location).isTask() && !getItem(location - 1).isTask()) {
                    ModelSeparator separator = (ModelSeparator) getItem(location - 1);
                    checkSeparators(separator.getType());
                    items.remove(location - 1);
                    notifyItemRemoved(location - 1);
                }
            } else if (getItemCount() - 1 >= 0 && !getItem(getItemCount() - 1).isTask()) {
                ModelSeparator separator = (ModelSeparator) getItem(getItemCount() - 1);
                checkSeparators(separator.getType());

                int locationTemp = getItemCount() - 1;
                items.remove(locationTemp);
                notifyItemRemoved(locationTemp);
            }
        }
    }

    public void checkSeparators(int type) {
        switch (type) {
            case ModelSeparator.TYPE_OVERDUE:
                mContainsSeparatorOverdue = false;
                break;
            case ModelSeparator.TYPE_TODAY:
                mContainsSeparatorToday = false;
                break;
            case ModelSeparator.TYPE_TOMORROW:
                mContainsSeparatorTomorrow = false;
                break;
            case ModelSeparator.TYPE_FUTURE:
                mContainsSeparatorFuture = false;
                break;
        }
    }

    public void removeAllItems() {
        if (getItemCount() != 0) {
            items = new ArrayList<>();
            notifyDataSetChanged();
            mContainsSeparatorOverdue = false;
            mContainsSeparatorToday = false;
            mContainsSeparatorTomorrow = false;
            mContainsSeparatorFuture = false;

        }
    }

    //returns size of List
    @Override
    public int getItemCount() {
        return items.size();
    }

    protected static abstract class TaskViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, View.OnLongClickListener {
        protected ModelTask task;

        protected TextView title;
        protected TextView date;
        protected CircleImageView priority;

        public TaskViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tvTaskTitle);
            date = (TextView) itemView.findViewById(R.id.tvTaskDate);
            priority = (CircleImageView) itemView.findViewById(R.id.cvTaskPriority);

            itemView.setOnClickListener(this);
            priority.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cvTaskPriority:
                    handlePriorityClick(v);
                    break;
                default:
                    handleItemClick(v);
                    break;
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return handleItemLongClick(v);
        }

        protected abstract void handlePriorityClick(View v);

        protected abstract boolean handleItemLongClick(View v);

        protected void handleItemClick(View v) {
        }
    }

    protected class SeparatorViewHolder extends RecyclerView.ViewHolder {

        protected TextView type;

        public SeparatorViewHolder(View itemView, TextView type) {
            super(itemView);
            this.type = type;
        }
    }

    public TaskFragment getTaskFragment() {
        return mTaskFragment;
    }
}
