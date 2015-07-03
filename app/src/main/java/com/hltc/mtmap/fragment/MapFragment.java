package com.hltc.mtmap.fragment;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.VisibleRegion;
import com.amp.apis.libc.Cluster;
import com.amp.apis.libc.ClusterClickListener;
import com.amp.apis.libc.ClusterItem;
import com.amp.apis.libc.ClusterOverlay;
import com.amp.apis.libc.ClusterRender;
import com.capricorn.ArcMenu;
import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.MainActivity;
import com.hltc.mtmap.activity.publish.CreateGrainActivity;
import com.hltc.mtmap.activity.start.StartActivity;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.bean.MapInfo;
import com.hltc.mtmap.bean.RegionItem;
import com.hltc.mtmap.bean.SiteItem;
import com.hltc.mtmap.bean.SwipeGrainItem;
import com.hltc.mtmap.util.AMapUtils;
import com.hltc.mtmap.util.ApiUtils;
import com.hltc.mtmap.util.StringUtils;
import com.hltc.mtmap.util.ToastUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MapFragment extends Fragment implements AMapLocationListener,
        AMap.OnCameraChangeListener, AMap.OnMapLoadedListener, AMap.OnMapTouchListener {

    private static final int[] ITEM_DRAWABLES = {
            R.drawable.arc_all,
            R.drawable.transparent,
            R.drawable.arc_amuse,
            R.drawable.transparent,
            R.drawable.arc_food
    };
    public static float defaultZoom = 17f;
    public static MapInfo mMapInfo;
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
    private long refreshDistance = 200;
    private float lastZoom = defaultZoom;
    private float currentZoom;
    private LatLng myLocation;
    private LatLng lastLocation;
    private LocationManagerProxy locationManagerProxy;
    private List<SwipeGrainItem> grains;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("MapFragment", "onCreateView");
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
            View view = inflater.inflate(R.layout.fragment_map, container, false);
            ButterKnife.inject(this, view);
            mMapView.onCreate(savedInstanceState);
            initData();
            initAmap();
            initArcMenu();
            return view;
        }
    }

    private void initData() {
        mMapInfo = AppConfig.getAppConfig().getMapInfo();
    }

    private void initAmap() {
        if (mAmap == null) {
            mAmap = mMapView.getMap();

            mAmap.setMyLocationEnabled(true);
            mAmap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

            locationManagerProxy = LocationManagerProxy.getInstance(getActivity());
            locationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 15, this);

            mAmap.setOnMapLoadedListener(this);
            mAmap.setOnMapTouchListener(this);
            mAmap.setOnCameraChangeListener(this);

            addPinToMap();
        }
    }

    private void initArcMenu() {
        for (int i = 0; i < ITEM_DRAWABLES.length; i++) {
            ImageView item = new ImageView(getActivity());
            item.setImageResource(ITEM_DRAWABLES[i]);
            item.setTag(i);

//            final int position = i;
            mArcMenu.addItem(item, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    //当前点击了类
//                    currentCategory = position;
//                    //TODO
//                    ToastUtils.showShort(getActivity(), "Positon: " + position);
                    ImageView view = (ImageView) v;
                    int which = (int) view.getTag();
                    Toast.makeText(getActivity(), "which:" + which, Toast.LENGTH_SHORT).show();
                    switch (which) {
                        case 0:
                            if (currentCategory != 0) {
                                httpQueryGrain(0);
                            }
                            currentCategory = 0;
                            break;
                        case 2:
                            if (currentCategory != 1) {
                                httpQueryGrain(1);
                            }
                            currentCategory = 1;
                            break;
                        case 4:
                            if (currentCategory != 2) {
                                httpQueryGrain(2);
                            }
                            currentCategory = 2;
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            currentZoom = mAmap.getCameraPosition().zoom;
            LatLng currentLocation = mAmap.getCameraPosition().target;
            double distance = com.amap.api.maps.AMapUtils.calculateLineDistance(currentLocation, lastLocation);
            Log.d("MapFragment", "distance:" + distance);
            Log.d("MapFragment", "currentZoom:" + currentZoom);
            if (distance > refreshDistance || currentZoom != lastZoom) {//距离大于刷新距离
                Log.d("MapFragment", "you can refresh now");
            }
            lastLocation = currentLocation;
            lastZoom = currentZoom;
        }
    }

    @Override
    public void onMapLoaded() {
        Log.d("MapFragment", "onMapLoaded");

        //加载完地图进入上次最后地点
        if (!StringUtils.isEmpty(mMapInfo.getLatitude())) {
            LatLng latLng = new LatLng(StringUtils.toDouble(
                    mMapInfo.getLatitude()), StringUtils.toDouble(mMapInfo.getLongitude()));
            mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, defaultZoom));
        }

        overlay = new
                ClusterOverlay(mAmap, AMapUtils.dp2px(getActivity(), clusterRadius), getActivity());
        overlay.setClusterRenderer(new ClusterRender() {
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
                    //civ.setImageDrawable(item.getDrawable());
                    ImageLoader.getInstance().displayImage(item.getPicUrl(), civ);
                } else {
                    TextView tv = (TextView) view.findViewById(R.id.tv_cluster);
                    tv.setText(String.valueOf(num));
                    tv.setBackgroundResource(R.drawable.cluster_num_bg);
                }
                return BitmapDescriptorFactory.fromView(layout);
            }
        });
        overlay.setOnClusterClickListener(new ClusterClickListener() {
            @Override
            public void onClick(Marker marker, List<ClusterItem> clusterItems) {
                for (ClusterItem item : clusterItems) {
                    RegionItem regionItem = (RegionItem) item;
                    //TODO
                }
            }
        });

//        fillDataFromDb();
        httpQueryGrain(currentCategory);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        Log.d("MapFragment", "onCameraChange");
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        Log.d("MapFragment", "onCameraChangeFinish");
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        Log.d("MapFragment", "onLocationChanged");
        if (aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0) {
            myLocation = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, defaultZoom));
            lastLocation = mAmap.getCameraPosition().target;
            currentZoom = mAmap.getCameraPosition().zoom;

            //更新位置信息
            mMapInfo.setLatitude(String.valueOf(aMapLocation.getLatitude()));
            mMapInfo.setLongitude(String.valueOf(aMapLocation.getLongitude()));
            mMapInfo.setCityCode(aMapLocation.getCityCode());
            mMapInfo.setProvince(aMapLocation.getProvince());
            mMapInfo.setAdCode(aMapLocation.getAdCode());
            mMapInfo.setDistrict(aMapLocation.getDistrict());
            mMapInfo.setCity(aMapLocation.getCity());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("MapFragment", "onLocationChanged");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

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

    private void httpQueryGrain(int cateId) {
        VisibleRegion visibleRegion = mAmap.getProjection().getVisibleRegion(); // 获取可视区域、
        LatLngBounds latLngBounds = visibleRegion.latLngBounds;// 获取可视区域的Bounds
        float radius = com.amap.api.maps.AMapUtils.calculateLineDistance(
                latLngBounds.northeast, latLngBounds.southwest) / 2;

        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig().getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig().getConfToken());
            if (currentCategory != 0) {
                json.put(ApiUtils.KEY_GRAIN_MCATEID, CreateGrainActivity.mCateId[cateId]);
            }
            json.put(ApiUtils.KEY_GRAIN_CITYCODE, mMapInfo.getCityCode());
            json.put(ApiUtils.KEY_GRAIN_LON, String.valueOf(mAmap.getCameraPosition().target.longitude));
            json.put(ApiUtils.KEY_GRAIN_LAT, String.valueOf(mAmap.getCameraPosition().target.latitude));
            json.put(ApiUtils.KEY_GRAIN_RADIUS, String.valueOf(radius));
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                ApiUtils.getQueryGrainUrl(),
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        if (StringUtils.isEmpty(result))
                            return;
                        try {
                            if (result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                                grains = new ArrayList<>();
                                JSONArray data = new JSONObject(result).getJSONArray(ApiUtils.KEY_DATA);
                                for (int i = 0; i < data.length(); i++) {
                                    SwipeGrainItem swipeGrainItem = new SwipeGrainItem();
                                    JSONObject grain = data.getJSONObject(i);
                                    swipeGrainItem.setGrainId(grain.getLong("grainId"));
                                    swipeGrainItem.setText(grain.getString("text"));
                                    swipeGrainItem.setUserId(grain.getLong("userId"));
                                    swipeGrainItem.setPortrait(grain.getString("userSmallPortait"));

                                    SiteItem siteItem = new SiteItem();
                                    JSONObject site = grain.getJSONObject("site");
                                    siteItem.setSiteId(site.getString("siteId"));
                                    siteItem.setLon(site.getDouble("lon"));
                                    siteItem.setLat(site.getDouble("lat"));
                                    siteItem.setName(site.getString("name"));
                                    siteItem.setAddress(site.getString("address"));
                                    siteItem.setPhone(site.getString("phone"));

                                    swipeGrainItem.setSite(siteItem);
                                    grains.add(swipeGrainItem);

                                    //显示到overlay
                                    for (SwipeGrainItem g : grains) {
                                        LatLng latlng = new LatLng(g.getSite().getLat(), g.getSite().getLon());
                                        RegionItem item = new RegionItem(latlng, g.getPortrait());
                                        overlay.addClusterItem(item);
                                    }

                                    //保存到数据库
                                    for (SwipeGrainItem g : grains) {
                                        //TODO
                                    }
                                }
                            } else {
                                JSONObject girl = new JSONObject(result);
                                String errorMsg = girl.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 发送验证码失败
                                    // TODO 没有验证错误码
                                    ToastUtils.showShort(getActivity(), errorMsg);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {

                    }
                });
    }

    /**
     * ************************** Lifecycle ***************************
     */

    @Override
    public void onResume() {
        super.onResume();
        Log.d("MapFragment", "onResume");
        if (mMapView != null)
            mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("MapFragment", "onPause");
        if (mMapView != null)
            mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMapView != null)
            mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null)
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