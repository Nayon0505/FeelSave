package com.example.feelsave;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feelsave.R;
import com.example.feelsave.contactModel;

import java.util.List;

public class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.ViewHolder> {

    private List<contactModel> contactList;
    private FireBaseHelper fireBaseHelper;

    public recyclerAdapter(List<contactModel> contactList) {
        this.contactList = contactList;
        this.fireBaseHelper = new FireBaseHelper();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public TextView textViewPhone;
        public ImageButton buttonRemove;

        public ViewHolder(View view) {
            super(view);
            textViewName = view.findViewById(R.id.textViewName);
            textViewPhone = view.findViewById(R.id.textViewPhone);
            buttonRemove = view.findViewById(R.id.buttonRemove);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contactlistlayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        contactModel contact = contactList.get(position);
        holder.textViewName.setText(contact.getName());
        holder.textViewPhone.setText(contact.getNumber());

        holder.buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();

                if (currentPosition != RecyclerView.NO_POSITION && currentPosition < contactList.size()) {
                    String key = contactList.get(currentPosition).getKey();

                    fireBaseHelper.deleteContact(key);

                    contactList.remove(currentPosition);
                    notifyItemRemoved(currentPosition);
                    notifyItemRangeChanged(currentPosition, contactList.size());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }
}
