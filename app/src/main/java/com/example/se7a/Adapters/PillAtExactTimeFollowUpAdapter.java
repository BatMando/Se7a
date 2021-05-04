package com.example.se7a.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
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

public class PillAtExactTimeFollowUpAdapter extends RecyclerView.Adapter<PillAtExactTimeFollowUpAdapter.ViewHolder> {
    Context context;
    ArrayList<Pill> pills;
    OnItemClickListener onYesClickListener;

    public void setOnYesClickListener(OnItemClickListener onYesClickListener) {
        this.onYesClickListener = onYesClickListener;
    }

    public void setOnNoClickListener(OnItemClickListener onNoClickListener) {
        this.onNoClickListener = onNoClickListener;
    }

    OnItemClickListener onNoClickListener;
    public PillAtExactTimeFollowUpAdapter(Context c , ArrayList<Pill> p){
        context=c;
        pills=p;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item_pills_check_fragment,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        final Pill pill= pills.get(position);
        viewHolder.pill_name.setText(pill.getPill_name());
        viewHolder.pill_dose.setText(pill.getPill_dose());
        viewHolder.pill_type.setText(pill.getPill_type());
        Picasso.get().load(pill.getPill_image()).fit().centerInside().transform(new CropCircleTransformation()).into(viewHolder.pill_image);
        if (pills.get(position).isPill_status()){
            viewHolder.pill_status.setText("مأخوذ");
            viewHolder.yes.setVisibility(View.GONE);
            viewHolder.no.setVisibility(View.GONE);
        }
        else{
            viewHolder.pill_status.setText(" ");

            viewHolder.yes.setVisibility(View.VISIBLE);
            viewHolder.no.setVisibility(View.VISIBLE);
        }

        if(onYesClickListener!=null){
            viewHolder.yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onYesClickListener.onItemClick(position,pill);
                }
            });
        }
        if (onNoClickListener!=null){
            viewHolder.no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onNoClickListener.onItemClick(position,pill);
                }
            });
        }
        //todo


    }

    @Override
    public int getItemCount() {
        return pills.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView pill_name;
        TextView pill_dose;
        TextView pill_type;
        ImageView pill_image;
        ImageView yes;
        ImageView no;
        TextView pill_status;
        public ViewHolder(View view){
            super(view);
            pill_name=view.findViewById(R.id.pill_name);
            pill_dose=view.findViewById(R.id.Pill_dose);
            pill_type=view.findViewById(R.id.Pill_type);
            pill_image=view.findViewById(R.id.pill_type_image);
            yes=view.findViewById(R.id.yes_check);
            no=view.findViewById(R.id.no_check);
            pill_status=view.findViewById(R.id.pill_status);

        }
/*
        @Override
        public void onClick(View v) {
            if (mListener!=null){
                int pos=getAdapterPosition();
                if(pos!=RecyclerView.NO_POSITION){
                    mListener.onItemClick(pos);
                }
            }
        }*/
    }
    public interface OnItemClickListener{
        void onItemClick(int position, Pill pill);
    }

}
