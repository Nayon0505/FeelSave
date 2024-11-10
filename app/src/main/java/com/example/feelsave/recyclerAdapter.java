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

    // ViewHolder class that holds references to item views
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
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contactlistlayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        contactModel contact = contactList.get(position);
        holder.textViewName.setText(contact.getName());
        holder.textViewPhone.setText(contact.getNumber());

        // Set up click listener for the remove button
        holder.buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();

                // Ensure the current position is valid and within bounds
                if (currentPosition != RecyclerView.NO_POSITION && currentPosition < contactList.size()) {
                    // Retrieve key from the contact at this position
                    String key = contactList.get(currentPosition).getKey();

                    // Delete from Firebase
                    fireBaseHelper.deleteContactFromDB(key);

                    // Remove from local list and update RecyclerView
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
