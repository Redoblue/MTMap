package com.hltc.mtmap.fragment;

import android.content.Context;
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
import com.amap.api.maps.model.MarkerOptions;
import com.amp.apis.libc.Cluster;
import com.amp.apis.libc.ClusterClickListener;
import com.amp.apis.libc.ClusterItem;
import com.amp.apis.libc.ClusterOverlay;
import com.amp.apis.libc.ClusterRender;
import com.capricorn.ArcMenu;
import com.hltc.mtmap.R;
import com.hltc.mtmap.app.DaoManager;
import com.hltc.mtmap.bean.RegionItem;
import com.hltc.mtmap.util.AMapUtils;
import com.hltc.mtmap.util.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MapFragment extends Fragment {

    private static final int[] ITEM_DRAWABLES = {
            R.drawable.arc_all,
            R.drawable.transparent,
            R.drawable.arc_amuse,
            R.drawable.transparent,
            R.drawable.arc_food
    };
    @InjectView(R.id.map)
    MapView mMapView;
    @InjectView(R.id.arc_menu)
    ArcMenu mArcMenu;

    private AMap mAmap;

    //    private DaoManager daoManager;
    private ClusterOverlay overlay;
    //Test by Tab ABC
    private int clusterRadius = 80;
    private int currentCategory = 0;
    private ClusterRender clusterRender = new ClusterRender() {
        @Override
        public BitmapDescriptor getBitmapDescriptor(Cluster cluster) {
            LayoutInflater inflater = (LayoutInflater)
                    getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.map_cluster_view, null);
            RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.layout_cluster);
            int num = cluster.getClusterCount();
            if (num == 1) {
                CircleImageView civ = (CircleImageView) view.findViewById(R.id.iv_cluster);
                ClusterItem item = cluster.getClusterItems().get(0);
//                civ.setImageDrawable(item.getDrawable());
                ImageLoader.getInstance().displayImage(item.getPicUrl(), civ);
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
    /**
     * *************************** interfaces ***************************
     */
    private AMap.OnMapLoadedListener mapLoadedListener = new AMap.OnMapLoadedListener() {
        @Override
        public void onMapLoaded() {
            overlay = new
                    ClusterOverlay(mAmap, AMapUtils.dp2px(getActivity(), clusterRadius), getActivity());
            overlay.setClusterRenderer(clusterRender);
            overlay.setOnClusterClickListener(clusterClickListener);

            fillDataFromDb();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.inject(this, view);

        mMapView.onCreate(savedInstanceState);
//        daoManager = DaoManager.getDaoManager(getActivity());

        initAmap();
        initArcMenu();

        return view;
    }

    private void initAmap() {
        if (mAmap == null) {
            mAmap = mMapView.getMap();
        }
        mAmap.setOnMapLoadedListener(mapLoadedListener);
        addPinToMap();
    }

    private void initArcMenu() {
        for (int i = 0; i < ITEM_DRAWABLES.length; i++) {
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

    private void fillDataFromDb() {
//        List<MTGrain> grains = daoManager.getAllVisibleGrains();
//        for (MTGrain grain : grains) {
//            long siteId = grain.getSiteId();
//            MTSite site = daoManager.getDaoSession().getMTSiteDao().load(siteId);
//            LatLng latLng = new LatLng(site.getLatitude(), site.getLongitude());
//
//            long userId = grain.getUserId();
//            MTUser user = daoManager.getDaoSession().getMTUserDao().load(userId);
//            String url = user.getAvatarURL();
//
//            RegionItem item = new RegionItem(latLng, url);
//            overlay.addClusterItem(item);
//        }
    }

    private void addPinToMap() {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_location));
        markerOptions.draggable(true);
        Marker marker = mAmap.addMarker(markerOptions);
        marker.setPositionByPixels(400, 300);
//        mMarker.setPosition(latlng);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


    /*********************************** CursorLoader *********************************/

//    public static class MTGrainCursorLoader extends CursorLoader {
//
//        public MTGrainCursorLoader(Context context) {
//            super(context);
//        }
//
//        @Override
//        public Cursor loadInBackground() {
//            return super.loadInBackground();
//        }
//    }

}