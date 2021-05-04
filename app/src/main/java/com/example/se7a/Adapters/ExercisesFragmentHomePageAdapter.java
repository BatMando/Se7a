package com.example.se7a.Adapters;


import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se7a.Model.Exercise;
import com.example.se7a.R;

import java.util.ArrayList;

public class ExercisesFragmentHomePageAdapter extends RecyclerView.Adapter<ExercisesFragmentHomePageAdapter.ViewHolder>  {
    Context context;
    ArrayList<Exercise> exercises;
    OnItemClickListener mListener;
    public ExercisesFragmentHomePageAdapter(Context c , ArrayList<Exercise> e){
        context=c;
        exercises=e;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view= LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_item_exercise_fragment_home_page,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.exercise_title.setText(exercises.get(position).getExercise_title());
        viewHolder.exercise_type.setText(exercises.get(position).getExercise_type());


    }

    @Override
    public int getItemCount() {
        if (exercises==null)
            return 0;
        return exercises.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener , MenuItem.OnMenuItemClickListener {
        TextView exercise_title;
        TextView exercise_type;

        public ViewHolder(View view){
            super(view);
            exercise_title=view.findViewById(R.id.exercise_title);
            exercise_type=view.findViewById(R.id.exercise_type);
            view.setOnClickListener(this);
            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener!=null){
                int pos=getAdapterPosition();
                if(pos!=RecyclerView.NO_POSITION){
                    mListener.onItemClick(pos);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("اختر العملية");
            MenuItem delete=menu.add(Menu.NONE,1,1,"حذف الرياضة");
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener!=null){
                int pos=getAdapterPosition();
                if(pos!=RecyclerView.NO_POSITION){
                    switch (item.getItemId())
                    {
                        case 1:
                            mListener.onDeleteClick(pos);
                            return true;
                    }
                }
            }
            return false;
        }

    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener=listener;
    }

}

