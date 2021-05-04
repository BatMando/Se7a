package com.example.se7a.Adapters;


import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.se7a.Model.Pill;
import com.example.se7a.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class PillsFragmentHomePageAdapter extends RecyclerView.Adapter<PillsFragmentHomePageAdapter.ViewHolder>  {
    Context context;
    ArrayList<Pill> pills;
    OnItemClickListener mListener;
    public PillsFragmentHomePageAdapter(Context c ,ArrayList<Pill> p){
        context=c;
        pills=p;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view=LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_item_pills_fragment_home_page,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.pill_name.setText(pills.get(position).getPill_name());
        viewHolder.pill_dose.setText(pills.get(position).getPill_dose());
        viewHolder.pill_type.setText(pills.get(position).getPill_type());
        Picasso.get().load(pills.get(position).getPill_image()).fit().centerInside().transform(new CropCircleTransformation()).into(viewHolder.pill_image);
    }

    @Override
    public int getItemCount() {
        return pills.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener , MenuItem.OnMenuItemClickListener {
        TextView pill_name;
        TextView pill_dose;
        TextView pill_type;
        ImageView pill_image;
        public ViewHolder(View view){
            super(view);
            pill_name=view.findViewById(R.id.pill_name);
            pill_dose=view.findViewById(R.id.Pill_dose);
            pill_type=view.findViewById(R.id.Pill_type);
            pill_image=view.findViewById(R.id.pill_type_image);
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
            MenuItem delete=menu.add(Menu.NONE,1,1,"حذف الدواء");
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
    public interface OnItemClickListener{
        void onItemClick(int position);
        void onDeleteClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener=listener;
    }
}
