package com.hltc.mtmap.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amp.apis.libc.Cluster;
import com.amp.apis.libc.ClusterClickListener;
import com.amp.apis.libc.ClusterItem;
import com.amp.apis.libc.ClusterOverlay;
import com.amp.apis.libc.ClusterRender;
import com.capricorn.ArcMenu;
import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.MainActivity;
import com.hltc.mtmap.activity.map.GrainInfoDialog;
import com.hltc.mtmap.activity.publish.CreateGrainActivity;
import com.hltc.mtmap.activity.start.StartActivity;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.OssManager;
import com.hltc.mtmap.bean.GrainItem;
import com.hltc.mtmap.bean.MapInfo;
import com.hltc.mtmap.bean.SiteItem;
import com.hltc.mtmap.util.AMapUtils;
import com.hltc.mtmap.util.ApiUtils;
import com.hltc.mtmap.util.FileUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MapFragment extends Fragment implements AMapLocationListener,
        AMap.OnCameraChangeListener,
        AMap.OnMapLoadedListener,
        AMap.OnMapTouchListener,
        LocationSource,
        OfflineMapManager.OfflineMapDownloadListener {

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
    private OfflineMapManager offlineMapManager;
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
//    private List<GrainItem> grains = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("MT", "MapFragment");

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
            Log.d("MT", "MapFragment Finished");
            return view;
        }
    }

    private void initData() {
        mMapInfo = AppConfig.getAppConfig().getMapInfo();
    }

    private void initAmap() {
        if (mAmap == null) {
            mAmap = mMapView.getMap();

            mAmap.setLocationSource(this);
            mAmap.getUiSettings().setMyLocationButtonEnabled(true);
            mAmap.setMyLocationEnabled(true);
            mAmap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

            mAmap.getUiSettings().setTiltGesturesEnabled(false);

            locationManagerProxy = LocationManagerProxy.getInstance(getActivity());
            locationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 15, this);

            mAmap.setOnMapLoadedListener(this);
            mAmap.setOnMapTouchListener(this);
            mAmap.setOnCameraChangeListener(this);

//            addPinToMap();
            //下载离线地图
           /* new Thread(new Runnable() {
                @Override
                public void run() {
                    updateOfflineMap();
                }
            }).start();*/
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

            Log.d("MapFragment", "mAmap.getCameraPosition().zoom:" + mAmap.getCameraPosition().zoom);
            Log.d("MapFragment", "mAmap.getCameraPosition().tilt:" + mAmap.getCameraPosition().tilt);
        }
    }

    @Override
    public void onMapLoaded() {
        Log.d("MT", "MapFragment onMapLoaded");

        //加载完地图进入上次最后地点
        /*if (!StringUtils.isEmpty(mMapInfo.getLatitude())) {
            LatLng latLng = new LatLng(StringUtils.toDouble(
                    mMapInfo.getLatitude()), StringUtils.toDouble(mMapInfo.getLongitude()));
            mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, defaultZoom));
        }*/

        overlay = new
                ClusterOverlay(mAmap, AMapUtils.dp2px(getActivity(), clusterRadius), getActivity());
        overlay.setClusterRenderer(new ClusterRender() {
            @Override
            public BitmapDescriptor getBitmapDescriptor(Cluster cluster) {
                LayoutInflater inflater = (LayoutInflater)
                        getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.view_map_cluster, null);
                int num = cluster.getClusterCount();
                if (num == 1) {
                    CircleImageView civ = (CircleImageView) view.findViewById(R.id.iv_cluster);
                    ClusterItem item = cluster.getClusterItems().get(0);
//                    ImageLoader.getInstance().displayImage(item.getPicUrl(), civ, MyApplication.displayImageOptions);
                    civ.setImageDrawable(Drawable.createFromPath(item.getPicUrl()));
                } else {
                    TextView tv = (TextView) view.findViewById(R.id.tv_cluster);
                    tv.setText(String.valueOf(num));
                    tv.setBackgroundResource(R.drawable.cluster_num_bg);
                }
                return BitmapDescriptorFactory.fromView(view);
            }
        });
        overlay.setOnClusterClickListener(new ClusterClickListener() {
            @Override
            public void onClick(Marker marker, List<ClusterItem> clusterItems) {
                if (clusterItems.size() == 1) {
                    Intent intent = new Intent(getActivity(), GrainInfoDialog.class);
                    intent.putExtra("grain", (GrainItem) clusterItems.get(0));
                    startActivity(intent);
                }
                //TODO for many grain

            }
        });

        Log.d("MT", "MapFragment onMapLoaded Finished");
        //离线地图

    }

    /*private void updateOfflineMap() {
        offlineMapManager = new OfflineMapManager(getActivity(), this);
        List<OfflineMapCity> offlineMapCities = offlineMapManager.getDownloadingCityList();
        try {
            if (offlineMapCities.size() > 0) {
                for (OfflineMapCity omc : offlineMapCities) {
                    if (omc.getCode().equals(mMapInfo.getCityCode())) {
                        if (offlineMapManager.updateOfflineCityByCode(mMapInfo.getCityCode())) {
                            offlineMapManager.downloadByCityCode(mMapInfo.getCityCode());
                        }
                        break;
                    }
                    offlineMapManager.downloadByCityCode(mMapInfo.getCityCode());
                }
            } else {
                offlineMapManager.downloadByCityCode(mMapInfo.getCityCode());
            }
        } catch (AMapException e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {

    }

    @Override
    public void deactivate() {

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        Log.d("MT", "onCameraChange");
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        Log.d("MT", "onCameraChangeFinish");
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        Log.d("MT", "onLocationChanged");
        if (aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0) {
            myLocation = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            lastLocation = mAmap.getCameraPosition().target;
            currentZoom = mAmap.getCameraPosition().zoom;

            // setup camera
//            mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, defaultZoom));
//            mAmap.moveCamera(CameraUpdateFactory.changeTilt(35f));
            mAmap.moveCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition(myLocation, defaultZoom, 35f, 0f)));

            //更新位置信息
            mMapInfo.setLatitude(String.valueOf(aMapLocation.getLatitude()));
            mMapInfo.setLongitude(String.valueOf(aMapLocation.getLongitude()));
            mMapInfo.setCityCode(aMapLocation.getCityCode());
            mMapInfo.setProvince(aMapLocation.getProvince());
            mMapInfo.setAdCode(aMapLocation.getAdCode());
            mMapInfo.setDistrict(aMapLocation.getDistrict());
            mMapInfo.setCity(aMapLocation.getCity());

            new AddClusterAsyncTask().execute(currentCategory);
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

    @Override
    public void onDownload(int i, int i1, String s) {

    }

    private void addPinToMap() {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pic_location));
        markerOptions.draggable(true);
        Marker marker = mAmap.addMarker(markerOptions);
        marker.setPositionByPixels(400, 300);
//        mMarker.setPosition(latlng);
    }

    private void httpQueryGrain(int cateId) {
        /*VisibleRegion visibleRegion = mAmap.getProjection().getVisibleRegion(); // 获取可视区域、
            LatLngBounds latLngBounds = visibleRegion.latLngBounds;// 获取可视区域的Bounds
            float radius = com.amap.api.maps.AMapUtils.calculateLineDistance(
                    latLngBounds.northeast, latLngBounds.southwest) / 2;*/

        RequestParams params1 = new RequestParams();
        params1.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig().getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig().getConfToken());
            if (currentCategory != 0) {
                json.put(ApiUtils.KEY_GRAIN_MCATEID, CreateGrainActivity.mCateId[cateId]);
            }
            json.put(ApiUtils.KEY_GRAIN_CITYCODE, mMapInfo.getCityCode());
            json.put(ApiUtils.KEY_GRAIN_LON, mMapInfo.getLongitude());
            json.put(ApiUtils.KEY_GRAIN_LAT, mMapInfo.getLatitude());
            json.put(ApiUtils.KEY_GRAIN_RADIUS, "90000");
            params1.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                ApiUtils.getQueryGrainUrl(),
                params1,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        Log.d("MapFragment", result);
                        try {
                            if (result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                                JSONArray data = new JSONObject(result).getJSONArray(ApiUtils.KEY_DATA);
                                for (int i = 0; i < data.length(); i++) {
                                    final GrainItem gi = new GrainItem();
                                    JSONObject grain = data.getJSONObject(i);
                                    gi.setGrainId(grain.getLong("grainId"));
                                    gi.setText(grain.getString("text"));
                                    gi.setUserId(grain.getLong("userId"));
                                    gi.setNickName(grain.getString("nickName"));
                                    try {
                                        gi.setRemark(grain.getString("remark"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    gi.setPortrait(grain.getString("userPortrait"));

                                    SiteItem s = new SiteItem();
                                    JSONObject site = grain.getJSONObject("site");
                                    s.setSiteId(site.getString("siteId"));
                                    s.setLon(site.getDouble("lon"));
                                    s.setLat(site.getDouble("lat"));
                                    s.setName(site.getString("name"));
                                    s.setAddress(site.getString("address"));
                                    s.setPhone(site.getString("phone"));

                                    gi.setSite(s);
//                                    grains.add(gi);
                                    // 执行LoadClusterAsyncTask后，gi中的portrait会发生变化，所以最好在这里保存到数据库
                                    final String to = FileUtils.getAppCache(getActivity(), "portrait")
                                            + FileUtils.getFileName(gi.getPortrait());
                                    final String key = OssManager.getFileKeyByRemoteUrl(gi.getPortrait());
                                    File file = new File(to);
                                    if (!file.exists()) {
                                        OssManager.getOssManager().downloadImage(to, key);
                                    }
                                    gi.setPortrait(to);
                                    overlay.addClusterItem(gi);
                                }
                            } else {
                                JSONObject girl = new JSONObject(result);
                                String errorMsg = girl.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 发送验证码失败
                                    // TODO 没有验证错误码
//                                    ToastUtils.showShort(getActivity(), errorMsg);
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

    class AddClusterAsyncTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            httpQueryGrain(params[0]);
            return null;
        }
    }

}