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

import com.example.quentinclayton.databaseoftask.R;
import com.example.quentinclayton.databaseoftask.Utils;
import com.example.quentinclayton.databaseoftask.fragment.DoneTaskFragment;
import com.example.quentinclayton.databaseoftask.model.Item;
import com.example.quentinclayton.databaseoftask.model.ModelTask;
import com.example.quentinclayton.databaseoftask.Util.AnimationEndListener;
import com.example.quentinclayton.databaseoftask.Util.AnimationEndListenerAdapter;

public class DoneTaskAdapter extends TaskAdapter {

    public DoneTaskAdapter(DoneTaskFragment taskFragment) {
        super(taskFragment);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.model_task, viewGroup, false);
        return new DoneTaskViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Item item = items.get(position);

        if (item.isTask()) {
            viewHolder.itemView.setEnabled(true);
            final ModelTask task = (ModelTask) item;
            final TaskViewHolder taskViewHolder = (TaskViewHolder) viewHolder;
            taskViewHolder.task = task;

            final View itemView = taskViewHolder.itemView;
            final Resources resources = itemView.getResources();

            taskViewHolder.title.setText(task.getTitle());
            if (task.getDate() != 0) {
                taskViewHolder.date.setText(Utils.getFullDate(task.getDate()));
            } else {
                taskViewHolder.date.setText(null);
            }

            itemView.setVisibility(View.VISIBLE);

            itemView.setBackgroundColor(resources.getColor(R.color.gray_200));

            taskViewHolder.title.setTextColor(resources.getColor(R.color.primary_text_disabled_material_light));
            taskViewHolder.date.setTextColor(resources.getColor(R.color.secondary_text_disabled_material_light));
            taskViewHolder.priority.setColorFilter(resources.getColor(task.getPriorityColor()));
            taskViewHolder.priority.setImageResource(R.drawable.ic_check_circle_white_48dp);

        }

    }

    private class DoneTaskViewHolder extends TaskViewHolder implements AnimationEndListener {

        public DoneTaskViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void handlePriorityClick(View v) {
            Context context = v.getContext();

            task.setStatus(ModelTask.STATUS_CURRENT);
            getTaskFragment().getActivityForTaskFragment().getDbHelper().update().status(task.getTimeStamp(), ModelTask.STATUS_CURRENT);

            itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.gray_50));

            title.setTextColor(ContextCompat.getColor(context, R.color.primary_text_default_material_light));
            date.setTextColor(ContextCompat.getColor(context, R.color.secondary_text_default_material_light));
            priority.setColorFilter(ContextCompat.getColor(context, task.getPriorityColor()));

            ObjectAnimator flipIn = ObjectAnimator.ofFloat(priority, "rotationY", 180f, 0f);
            priority.setImageResource(R.drawable.ic_checkbox_blank_circle_white_48dp);
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
            if (task.getStatus() != ModelTask.STATUS_DONE) {

                ObjectAnimator translationX = ObjectAnimator.ofFloat(itemView,
                        "translationX", 0f, -itemView.getWidth());

                ObjectAnimator translationXBack = ObjectAnimator.ofFloat(itemView,
                        "translationX", -itemView.getWidth(), 0f);


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
