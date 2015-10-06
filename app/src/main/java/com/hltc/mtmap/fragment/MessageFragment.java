package com.hltc.mtmap.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hltc.mtmap.MTMessage;
import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.MainActivity;
import com.hltc.mtmap.activity.start.StartActivity;
import com.hltc.mtmap.app.DaoManager;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.event.MessageEvent;
import com.hltc.mtmap.helper.ApiHelper;
import com.hltc.mtmap.util.DateUtils;
import com.hltc.mtmap.util.StringUtils;
import com.lidroid.xutils.HttpUtils;
import com.markupartist.android.widget.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageFragment extends Fragment {

    @InjectView(R.id.lv_message)
    PullToRefreshListView pullToRefreshListView;

    private List<MTMessage> mMessageList;
    private MessageAdapter mMessageAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (MainActivity.isVisitor) {
            View view = inflater.inflate(R.layout.window_remind_login, container, false);
            ImageView iv = (ImageView) view.findViewById(R.id.btn_remind_login);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), StartActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
            return view;
        } else {
            View view = inflater.inflate(R.layout.fragment_message, container, false);
            ButterKnife.inject(this, view);
            EventBus.getDefault().register(this);
            initView();
            return view;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    private void initView() {
        mMessageList = DaoManager.getManager().getAllMessage();
        mMessageAdapter = new MessageAdapter();
        pullToRefreshListView.setAdapter(mMessageAdapter);
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });
        pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ApiHelper.httpGetGrainDetail(MessageFragment.this.getActivity(),mMessageList.get(position).getGrainId());
            }
        });
    }

    public void onEvent(MessageEvent event) {
        refreshList();
    }

    private void refreshList() {
        mMessageList = DaoManager.getManager().getAllMessage();
        mMessageAdapter.notifyDataSetChanged();
        pullToRefreshListView.onRefreshComplete();
    }

    private class MessageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mMessageList.size();
        }

        @Override
        public MTMessage getItem(int position) {
            return mMessageList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_message, null);
                holder.portrait = (CircleImageView) convertView.findViewById(R.id.civ_item_message_portrait);
                holder.name = (TextView) convertView.findViewById(R.id.tv_item_message_nickname);
                holder.time = (TextView) convertView.findViewById(R.id.tv_item_message_time);
                holder.text = (TextView) convertView.findViewById(R.id.tv_item_message_text);
                holder.image = (ImageView) convertView.findViewById(R.id.iv_item_message_image);
                holder.comment = (TextView) convertView.findViewById(R.id.tv_item_message_comment);
                holder.address = (TextView) convertView.findViewById(R.id.tv_item_message_address);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            String type = getItem(position).getType();
            String name = StringUtils.isEmpty(getItem(position).getRemark()) ?
                    getItem(position).getNickName() : getItem(position).getRemark();
            if (type.equals("praise")) {
                holder.name.setText(Html.fromHtml("<font color=green>" + name + "</font> 赞了我"));
                holder.text.setVisibility(View.GONE);
            } else if (type.equals("comment")) {
                holder.name.setText(name);
                holder.text.setText(getItem(position).getCommentTxt());
            }
            ImageLoader.getInstance().displayImage(getItem(position).getPortrait(),
                    holder.portrait, MyApplication.displayImageOptions);
            holder.time.setText(DateUtils.getFriendlyTime(getItem(position).getCreateTime()));
            ImageLoader.getInstance().displayImage(getItem(position).getImage(),
                    holder.image, MyApplication.displayImageOptions);
            holder.comment.setText(getItem(position).getText());
            holder.address.setText(getItem(position).getAddress());

            return convertView;
        }

        private class ViewHolder {
            CircleImageView portrait;
            TextView name;
            TextView time;
            TextView text;
            ImageView image;
            TextView comment;
            TextView address;
        }
    }
}
