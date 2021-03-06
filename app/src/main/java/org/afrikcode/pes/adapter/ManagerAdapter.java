package org.afrikcode.pes.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Toast;

import org.afrikcode.pes.R;
import org.afrikcode.pes.base.BaseAdapter;
import org.afrikcode.pes.base.BaseFilter;
import org.afrikcode.pes.impl.ManagerImpl;
import org.afrikcode.pes.listeners.OnitemClickListener;
import org.afrikcode.pes.listeners.SetBranchForManagerListener;
import org.afrikcode.pes.models.Manager;
import org.afrikcode.pes.viewholder.ManagerVH;

import java.util.ArrayList;
import java.util.List;

public class ManagerAdapter extends BaseAdapter<Manager, OnitemClickListener<Manager>, ManagerVH> {

    private ManagerImpl managerImpl;
    private Context mContext;
    private SetBranchForManagerListener setBranchForManagerListener;

    public ManagerAdapter(Context mContext, ManagerImpl managerImpl, SetBranchForManagerListener listener) {
        this.mContext = mContext;
        this.managerImpl = managerImpl;
        this.setBranchForManagerListener = listener;
    }

    @NonNull
    @Override
    public ManagerVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.manager_item, parent, false);
        return new ManagerVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ManagerVH holder, int position) {
        final Manager manager = getFilteredList().get(position);
        if (manager.getBranchName() == null || manager.getBranchID() == null) {
            holder.getManagerBranchText().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setBranchForManagerListener.setBranchfor(manager.getUserID());
                }
            });
        }

        holder.render(manager);

        if (getOnclick() != null){
            holder.getParent().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getOnclick().onClick(manager);
                }
            });
        }

        holder.getStatusSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (manager.getBranchID() != null || manager.getBranchName() != null){
                        managerImpl.updateManagerStatus(manager.getUserID(), b);
                    }else{
                        compoundButton.setChecked(false);
                        Toast.makeText(mContext, "Branch has not been set for this Manager", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    @Override
    public Filter getFilter() {
        return new BaseFilter<Manager, ManagerAdapter>(this) {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString();

                List<Manager> filtered = new ArrayList<>();

                if (query.isEmpty()) {
                    filtered = getItemList();
                } else {
                    for (Manager b : getItemList()) {
                        if (b.getUsername().toLowerCase().contains(query.toLowerCase())) {
                            filtered.add(b);
                        }
                    }
                }

                Filter.FilterResults results = new Filter.FilterResults();
                results.count = filtered.size();
                results.values = filtered;
                return results;
            }
        };
    }
}
