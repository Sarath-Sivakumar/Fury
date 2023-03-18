package app.personal.fury.UI.Adapters.notificationAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import app.personal.MVVM.Entity.salaryEntity;
import app.personal.fury.R;
import app.personal.fury.UI.Adapters.salaryList.salaryAdapter;
public class notificationAdapter extends RecyclerView.Adapter<notificationAdapter.salHolder>{

    private salaryAdapter.onItemClickListener listener;

    @NonNull
    @Override
    public salHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recycler_item_notification, parent, false);
        return new salHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull salHolder holder, int position) {
        holder.notificationTitle.setText(R.string.welcome_to_fury);
        holder.notificationSubTitle.setText(R.string.noti_ex);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class salHolder extends RecyclerView.ViewHolder {
        private final TextView notificationTitle;
        private final TextView notificationSubTitle;

        public salHolder(@NonNull View itemView) {
            super(itemView);
            notificationTitle = itemView.findViewById(R.id.noti_title);
            notificationSubTitle = itemView.findViewById(R.id.noti_content);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (listener != null && pos != RecyclerView.NO_POSITION) {
//                    listener.onItemClick(salList.get(pos));
                }
            });
        }
    }

    public interface onItemClickListener {
        void onItemClick(salaryEntity exp);
    }

    public void setOnItemClickListener(salaryAdapter.onItemClickListener listener) {
        this.listener = listener;
    }
}
