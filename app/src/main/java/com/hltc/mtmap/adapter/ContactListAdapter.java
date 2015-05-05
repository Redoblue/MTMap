package com.hltc.mtmap.adapter;

import android.content.Context;

import com.hltc.mtmap.R;
import com.hltc.mtmap.bean.ContactStruct;

import java.util.List;

/**
 * Created by Redoblue on 2015/4/21.
 */
public class ContactListAdapter extends CommonAdapter<ContactStruct> {

    public ContactListAdapter(Context context, List<ContactStruct> list, int viewId) {
        super(context, list, viewId);
    }

    @Override
    public void convert(CommonViewHolder holder, ContactStruct struct) {
        holder.setText(R.id.tv_item_title, struct.getTitle())
                .setText(R.id.tv_item_desc, struct.getDesc())
                .setText(R.id.tv_item_time, struct.getTime())
                .setText(R.id.tv_item_phone, struct.getPhone());
    }
}
