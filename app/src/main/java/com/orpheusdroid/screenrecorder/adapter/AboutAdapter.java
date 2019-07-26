package com.orpheusdroid.screenrecorder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orpheusdroid.screenrecorder.R;
import com.orpheusdroid.screenrecorder.adapter.models.AboutModel;

import java.util.ArrayList;
import java.util.Calendar;

public class AboutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<AboutModel> abouts;
    private Context context;

    public AboutAdapter(Context context, ArrayList<AboutModel> FAQs) {
        this.context = context;
        this.abouts = FAQs;
    }

    private RecyclerView recyclerView;

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =null;
        RecyclerView.ViewHolder viewHolder = null;
        AboutModel.TYPE type = AboutModel.TYPE.getStatusFromInt(viewType);
        switch (type) {
            case INFO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.about_info_view, parent,false);
                viewHolder = new InfoViewHolder(view);
                break;
            case HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.about_header, parent,false);
                viewHolder = new HeaderViewHolder(view);
                break;
            case DATA:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.about_list_item, parent,false);
                viewHolder= new DataViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AboutModel faq = abouts.get(position);
        AboutModel.TYPE type = AboutModel.TYPE.getStatusFromInt(holder.getItemViewType());
        switch (type) {
            case HEADER:
                ((HeaderViewHolder) holder).header.setText(faq.getText());
                break;
            case DATA:
                ((DataViewHolder) holder).text.setText(faq.getText());
                break;
            case INFO:
                InfoViewHolder infoView = ((InfoViewHolder)holder);
                infoView.version_tv.setText(faq.getVersion());
                infoView.magisk_tv.setText(faq.isMagisk()+"");
                infoView.root_tv.setText(faq.isHasRoot()+"");
                infoView.buildType_tv.setText(faq.getBuildType());
                infoView.footer.setText(context.getString(R.string.about_info_footer, Calendar.getInstance().get(Calendar.YEAR)));
                break;

        }
    }

    @Override
    public int getItemCount() {
        return abouts.size();
    }

    @Override
    public int getItemViewType(int position) {
        return abouts.get(position).getType().getValue();
    }

    static class DataViewHolder extends RecyclerView.ViewHolder {
        TextView text;

        DataViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.about_text);
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView header;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.about_header);
        }
    }

    static class InfoViewHolder extends RecyclerView.ViewHolder {
        private TextView version_tv;
        private TextView magisk_tv;
        private TextView root_tv;
        private TextView buildType_tv;
        private TextView footer;

        InfoViewHolder(@NonNull View itemView) {
            super(itemView);
            version_tv = itemView.findViewById(R.id.info_version_tv);
            magisk_tv = itemView.findViewById(R.id.info_magisk_tv);
            root_tv = itemView.findViewById(R.id.info_root_tv);
            buildType_tv = itemView.findViewById(R.id.info_build_type_tv);
            footer = itemView.findViewById(R.id.footer);
        }
    }
}
