package com.hltc.mtmap.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Marker;
import com.amp.apis.libc.Cluster;
import com.amp.apis.libc.ClusterClickListener;
import com.amp.apis.libc.ClusterItem;
import com.amp.apis.libc.ClusterOverlay;

import com.amp.apis.libc.ClusterRender;
import com.capricorn.ArcMenu;
import com.hltc.mtmap.R;
import com.hltc.mtmap.bean.ConstantUtils;
import com.hltc.mtmap.bean.RegionItem;
import com.hltc.mtmap.util.AMapUtils;
import com.hltc.mtmap.util.ToastUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    private static final int[] ITEM_DRAWABLES = {
            R.drawable.composer_camera,
            R.drawable.composer_music,
            R.drawable.composer_place,
            R.drawable.composer_sleep,
            R.drawable.composer_thought,
            R.drawable.composer_with
    };

    private static final int ARC_ITEM_NUM = 6;
    private static final String TABLE_ID = "55214201e4b098078fdb272d";

    private AMap mAmap;
    private MapView mMapView;
    private ArcMenu mArcMenu;

    //Test by Tab ABC
    private int clusterRadius = 80;
    private int currentCategory = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        findViewById(view);
        mMapView.onCreate(savedInstanceState);

        initAmap();
        initArcMenu();

        return view;
    }

    private void findViewById(View view) {
        mMapView = (MapView) view.findViewById(R.id.map);
        mArcMenu = (ArcMenu) view.findViewById(R.id.arc_menu);
    }

    private void initAmap() {
        if (mAmap == null) {
            mAmap = mMapView.getMap();
            mAmap.setOnMapLoadedListener(mapLoadedListener);
        }
    }

    private void initArcMenu() {
        for (int i = 0; i < ARC_ITEM_NUM; i++) {
            ImageView item = new ImageView(getActivity());
            item.setImageResource(ITEM_DRAWABLES[i]);

            final int position = i;
            mArcMenu.addItem(item, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //当前点击了类
                    currentCategory = position;
                    //TODO
                    ToastUtils.showShort(getActivity(), "Positon: " + position);
                }
            });
        }
    }


    /**
     * ************************** Lifecycle ***************************
     */

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    /**
     * *************************** interfaces ***************************
     */
    private AMap.OnMapLoadedListener mapLoadedListener = new AMap.OnMapLoadedListener() {
        @Override
        public void onMapLoaded() {
            ClusterOverlay clusterOverlay = new
                    ClusterOverlay(mAmap, AMapUtils.dp2px(getActivity(), clusterRadius), getActivity());
            clusterOverlay.setClusterRenderer(clusterRender);
            clusterOverlay.setOnClusterClickListener(clusterClickListener);

            //TODO
            for (int i = 0; i < ConstantUtils.latlngs.length; i++) {
                Drawable drawable = getResources().getDrawable(R.drawable.cluster_pic);
                RegionItem regionItem = new RegionItem(ConstantUtils.latlngs[i], drawable);
                clusterOverlay.addClusterItem(regionItem);
            }
        }
    };

    private ClusterRender clusterRender = new ClusterRender() {
        @Override
        public BitmapDescriptor getBitmapDescriptor(Cluster cluster) {
            LayoutInflater inflater = (LayoutInflater)
                    getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.cluster, null);
            RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.layout_cluster);
            int num = cluster.getClusterCount();
            if (num == 1) {
                CircleImageView civ = (CircleImageView) view.findViewById(R.id.iv_cluster);
                ClusterItem item = cluster.getClusterItems().get(0);
                civ.setImageDrawable(item.getDrawable());
            } else {
                TextView tv = (TextView) view.findViewById(R.id.tv_cluster);
                tv.setText(String.valueOf(num));
                tv.setBackgroundResource(R.drawable.cluster_num_bg);
            }
            return BitmapDescriptorFactory.fromView(layout);
        }
    };

    private ClusterClickListener clusterClickListener = new ClusterClickListener() {
        @Override
        public void onClick(Marker marker, List<ClusterItem> clusterItems) {
            for (ClusterItem item : clusterItems) {
                RegionItem regionItem = (RegionItem) item;
                //TODO
            }
        }
    };

}