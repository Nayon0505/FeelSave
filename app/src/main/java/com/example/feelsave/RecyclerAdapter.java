package com.example.feelsave;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


// In der Notfallkontakte Acivity die Liste mit den Kontakten, setllt dies dar
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<ContactModel> contactList;
    private FireBaseHelper fireBaseHelper;

    public RecyclerAdapter(List<ContactModel> contactList) {
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
        ContactModel contact = contactList.get(position);
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
