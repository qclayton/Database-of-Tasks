package com.example.quentinclayton.databaseoftask.adapter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.quentinclayton.databaseoftask.R;
import com.example.quentinclayton.databaseoftask.Utils;
import com.example.quentinclayton.databaseoftask.fragment.CurrentTaskFragment;
import com.example.quentinclayton.databaseoftask.model.Item;
import com.example.quentinclayton.databaseoftask.model.ModelSeparator;
import com.example.quentinclayton.databaseoftask.model.ModelTask;
import com.example.quentinclayton.databaseoftask.Util.AnimationEndListener;
import com.example.quentinclayton.databaseoftask.Util.AnimationEndListenerAdapter;

import java.util.Calendar;

public class CurrentTasksAdapter extends TaskAdapter {

    private static final int TYPE_TASK = 0;
    private static final int TYPE_SEPARATOR = 1;

    public CurrentTasksAdapter(CurrentTaskFragment taskFragment) {
        super(taskFragment);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        switch (viewType) {
            case TYPE_TASK:
                View v = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.model_task, viewGroup, false);
                return new CurrentTaskViewHolder(v);
            case TYPE_SEPARATOR:
                View separator = LayoutInflater.from(viewGroup.getContext()).
                        inflate(R.layout.model_separator, viewGroup, false);
                TextView type = (TextView) separator.findViewById(R.id.tvSeparatorName);

                return new SeparatorViewHolder(separator, type);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Item item = items.get(position);

        final Resources resources = viewHolder.itemView.getResources();
        if (item.isTask()) {
            viewHolder.itemView.setEnabled(true);
            final ModelTask task = (ModelTask) item;
            final TaskViewHolder taskViewHolder = (TaskViewHolder) viewHolder;
            taskViewHolder.task = task;

            final View itemView = taskViewHolder.itemView;

            taskViewHolder.title.setText(task.getTitle());
            if (task.getDate() != 0) {
                taskViewHolder.date.setText(Utils.getFullDate(task.getDate()));
            } else {
                taskViewHolder.date.setText(null);
            }

            itemView.setVisibility(View.VISIBLE);
            taskViewHolder.priority.setEnabled(true);

            if (task.getDate() != 0 && task.getDate() < Calendar.getInstance().getTimeInMillis()) {
                itemView.setBackgroundColor(resources.getColor(R.color.gray_200));
            } else {
                itemView.setBackgroundColor(resources.getColor(R.color.gray_50));
            }

            taskViewHolder.title.setTextColor(resources.getColor(R.color.primary_text_default_material_light));
            taskViewHolder.date.setTextColor(resources.getColor(R.color.secondary_text_default_material_light));
            taskViewHolder.priority.setColorFilter(resources.getColor(task.getPriorityColor()));
            taskViewHolder.priority.setImageResource(R.drawable.ic_checkbox_blank_circle_white_48dp);

        } else {

            ModelSeparator separator = (ModelSeparator) item;
            SeparatorViewHolder separatorViewHolder = (SeparatorViewHolder) viewHolder;

            separatorViewHolder.type.setText(resources.getString(separator.getType()));
        }

    }
    //Returns tasks or separators
    @Override
    public int getItemViewType(int position) {
        if (getItem(position).isTask()) {
            return TYPE_TASK;
        } else {
            return TYPE_SEPARATOR;
        }
    }

    private class CurrentTaskViewHolder extends TaskViewHolder implements AnimationEndListener {

        public CurrentTaskViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void handleItemClick(View v) {
            getTaskFragment().showTaskEditDialog(task);
        }

        @Override
        protected void handlePriorityClick(View v) {
            Context context = v.getContext();

            task.setStatus(ModelTask.STATUS_DONE);
            getTaskFragment().getActivityForTaskFragment().getDbHelper().update().status(task.getTimeStamp(), ModelTask.STATUS_DONE);

            itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.gray_200));
            title.setTextColor(ContextCompat.getColor(context, R.color.primary_text_disabled_material_light));
            date.setTextColor(ContextCompat.getColor(context, R.color.secondary_text_disabled_material_light));
            priority.setColorFilter(ContextCompat.getColor(context, task.getPriorityColor()));

            ObjectAnimator flipIn = ObjectAnimator.ofFloat(priority, "rotationY", -180f, 0f);
            flipIn.addListener(new AnimationEndListenerAdapter(this));
            flipIn.start();
        }

        @Override
        protected boolean handleItemLongClick(View v) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getTaskFragment().removeTaskDialog(getLayoutPosition());
                }
            }, 1000);
            return true;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (task.getStatus() == ModelTask.STATUS_DONE) {
                priority.setImageResource(R.drawable.ic_check_circle_white_48dp);

                ObjectAnimator translationX = ObjectAnimator.ofFloat(itemView,
                        "translationX", 0f, itemView.getWidth());

                ObjectAnimator translationXBack = ObjectAnimator.ofFloat(itemView,
                        "translationX", itemView.getWidth(), 0f);


                translationX.addListener(new AnimationEndListenerAdapter(new AnimationEndListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        itemView.setVisibility(View.GONE);
                        getTaskFragment().moveTask(task);
                        removeItem(getLayoutPosition());
                    }
                }));

                AnimatorSet translationSet = new AnimatorSet();
                translationSet.play(translationX).before(translationXBack);
                translationSet.start();
            }
        }
    }

}
