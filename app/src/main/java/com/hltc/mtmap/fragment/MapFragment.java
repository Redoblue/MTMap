package com.hltc.mtmap.fragment;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.callback.GetFileCallback;
import com.alibaba.sdk.android.oss.model.OSSException;
import com.alibaba.sdk.android.oss.storage.OSSFile;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapException;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amp.apis.libc.Cluster;
import com.amp.apis.libc.ClusterClickListener;
import com.amp.apis.libc.ClusterItem;
import com.amp.apis.libc.ClusterOverlay;
import com.amp.apis.libc.ClusterRender;
import com.capricorn.ArcMenu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hltc.mtmap.MGrain;
import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.MainActivity;
import com.hltc.mtmap.activity.map.GrainInfoDialog;
import com.hltc.mtmap.activity.map.ManyGrainInfoDialog;
import com.hltc.mtmap.activity.map.SearchPositionActivity;
import com.hltc.mtmap.activity.publish.CreateGrainActivity;
import com.hltc.mtmap.activity.start.StartActivity;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.DaoManager;
import com.hltc.mtmap.app.OssManager;
import com.hltc.mtmap.bean.MapInfo;
import com.hltc.mtmap.gmodel.ClusterGrain;
import com.hltc.mtmap.orm.MGrainDao;
import com.hltc.mtmap.util.AMapUtils;
import com.hltc.mtmap.util.ApiUtils;
import com.hltc.mtmap.util.AppUtils;
import com.hltc.mtmap.util.FileUtils;
import com.hltc.mtmap.util.StringUtils;
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
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.dao.query.QueryBuilder;
import de.hdodenhof.circleimageview.CircleImageView;

public class MapFragment extends Fragment implements AMapLocationListener,
        AMap.OnCameraChangeListener,
        AMap.OnMapLoadedListener,
        AMap.OnMapTouchListener,
        LocationSource,
        OfflineMapManager.OfflineMapDownloadListener,
        PoiSearch.OnPoiSearchListener {

    public static final int SEARCH_POSITION_REQUEST_CODE = 0;

    public static final int TYPE_ALL = 2;
    public static final int TYPE_CHIHE = 0;
    public static final int TYPE_WANLE = 1;

    public static final float DEFAULT_ZOOM = 17f;
    public static final float DEFAULT_TILT = 30f;
    public static final float DEFAULT_BEARING = 0f;
    public static final long DEFAULT_DURATION = 1000;

    private static final int[] ITEM_DRAWABLES = {
            R.drawable.btn_map_rad_all,
            R.drawable.transparent,
            R.drawable.btn_map_rad_wanle,
            R.drawable.transparent,
            R.drawable.btn_map_rad_chihe
    };
    private static final String[] ITEM_TEXTS = {
            "附近所有", "", "附近玩乐", "", "附近吃喝"
    };
    public static MapInfo mMapInfo;
    @InjectView(R.id.map)
    MapView mMapView;
    @InjectView(R.id.arc_menu)
    ArcMenu mArcMenu;
    @InjectView(R.id.et_map_search)
    EditText etMapSearch;
    @InjectView(R.id.btn_map_locate)
    ImageButton btnMapLocate;
    @InjectView(R.id.layout_map_search)
    RelativeLayout layoutMapSearch;
    private AMap mAmap;
    private OfflineMapManager offlineMapManager;
    private PoiSearch.Query mQuery;
    private ClusterOverlay overlay;
    private List<ClusterGrain> mGrains;
    //Test by Tab ABC
    private int clusterRadius = 60;
    private int currentCategory = TYPE_ALL;
    private long refreshDistance = 200;
    private float lastZoom = DEFAULT_ZOOM;
    private float currentZoom;
    private LatLng myLocation;
    private LatLng lastLocation;
    private LocationManagerProxy locationManagerProxy;

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
            initView();
            initAmap();
            initArcMenu();
            Log.d("MT", "MapFragment Finished");
            return view;
        }
    }

    private void initData() {
        mMapInfo = AppConfig.getAppConfig().getMapInfo();
    }

    private void initView() {
        btnMapLocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LatLng latLng = new LatLng(StringUtils.toDouble(
                            mMapInfo.getLatitude()), StringUtils.toDouble(mMapInfo.getLongitude()));
                    goSomewhereWithAnimation(latLng);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick({R.id.layout_map_search, R.id.et_map_search})
    public void startSearchActivity() {
        Intent intent = new Intent(getActivity(), SearchPositionActivity.class);
        startActivityForResult(intent, SEARCH_POSITION_REQUEST_CODE);
    }


    private void goSomewhereWithAnimation(LatLng latLng) {
        mAmap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition(latLng, mAmap.getCameraPosition().zoom,
                        DEFAULT_TILT, mAmap.getCameraPosition().bearing)), DEFAULT_DURATION, null);
    }

    private void initAmap() {
        if (mAmap == null) {
            mAmap = mMapView.getMap();

            mAmap.setLocationSource(this);
            mAmap.setMyLocationEnabled(true);
            mAmap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

            mAmap.getUiSettings().setTiltGesturesEnabled(false);

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
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View item = inflater.inflate(R.layout.layout_map_arc, null);
            ImageView iv = (ImageView) item.findViewById(R.id.iv_item_map_arc_pic);
            TextView tv = (TextView) item.findViewById(R.id.tv_item_map_arc_text);
            iv.setImageDrawable(getActivity().getResources().getDrawable(ITEM_DRAWABLES[i]));
            tv.setText(ITEM_TEXTS[i]);
            item.setTag(i);

            mArcMenu.addItem(item, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout view = (LinearLayout) v;
                    int which = (int) view.getTag();
                    switch (which) {
                        case 0:
                            if (currentCategory != TYPE_ALL) {
                                currentCategory = TYPE_ALL;
                                addGrainToOverlay(getGrainFromMem(TYPE_ALL));
                            }
                            break;
                        case 2:
                            if (currentCategory != TYPE_CHIHE) {
                                currentCategory = TYPE_CHIHE;
                                addGrainToOverlay(getGrainFromMem(TYPE_CHIHE));
                            }
                            break;
                        case 4:
                            if (currentCategory != TYPE_WANLE) {
                                currentCategory = TYPE_WANLE;
                                addGrainToOverlay(getGrainFromMem(TYPE_WANLE));
                            }
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
        if (!StringUtils.isEmpty(mMapInfo.getLatitude())) {
            LatLng latLng = new LatLng(StringUtils.toDouble(
                    mMapInfo.getLatitude()), StringUtils.toDouble(mMapInfo.getLongitude()));
            mAmap.moveCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition(latLng, DEFAULT_ZOOM, DEFAULT_TILT, DEFAULT_BEARING)));
            myLocation = latLng;//更新个人位置
        }

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
                    if (!StringUtils.isEmpty(item.getPicUrl()))
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
                    intent.putExtra("grain", (ClusterGrain) clusterItems.get(0));
                    startActivity(intent);
                } else if (clusterItems.size() >= 2) {
                    ArrayList<ClusterGrain> cgs = new ArrayList<>();
                    for (ClusterItem ci : clusterItems) {
                        cgs.add((ClusterGrain) ci);
                    }
                    Intent intent = new Intent(getActivity(), ManyGrainInfoDialog.class);
                    intent.putParcelableArrayListExtra("grains", cgs);
                    startActivity(intent);
                }
            }
        });

        //添加我的位置maker
        addPinToMap();
    }

    private void updateOfflineMap() {
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
    }

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
        if (aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0) {
            myLocation = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            lastLocation = mAmap.getCameraPosition().target;
            currentZoom = mAmap.getCameraPosition().zoom;

            // setup camera
            mAmap.moveCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition(myLocation, DEFAULT_ZOOM, DEFAULT_TILT, DEFAULT_BEARING)));

            //更新位置信息
            mMapInfo.setLatitude(String.valueOf(aMapLocation.getLatitude()));
            mMapInfo.setLongitude(String.valueOf(aMapLocation.getLongitude()));
            mMapInfo.setCityCode(aMapLocation.getCityCode());
            mMapInfo.setProvince(aMapLocation.getProvince());
            mMapInfo.setAdCode(aMapLocation.getAdCode());
            mMapInfo.setDistrict(aMapLocation.getDistrict());
            mMapInfo.setCity(aMapLocation.getCity());

            // if network is available, we load data from internet, otherwise, we load data from db
            if (AppUtils.isNetworkConnected(getActivity())) {
                new AddClusterAsyncTask().execute(0);
            } else {
                mGrains = getGrainsFromDb(TYPE_ALL);
                addGrainToOverlay(mGrains);
            }
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
//        markerOptions.draggable(true);
        Marker marker = mAmap.addMarker(markerOptions);
//        marker.setPositionByPixels(400, 300);
        marker.setPosition(myLocation);
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
//            if (currentCategory != 0) {
//                json.put(ApiUtils.KEY_GRAIN_MCATEID, CreateGrainActivity.mCateId[cateId]);
//            }
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
                                JSONArray array = new JSONObject(result).getJSONArray(ApiUtils.KEY_DATA);
                                Gson gson = new Gson();
                                mGrains = gson.fromJson(array.toString(), new TypeToken<List<ClusterGrain>>() {
                                }.getType());

                                if (mGrains != null && mGrains.size() > 0) {
                                    //保存到数据库
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            saveGrainToDb(mGrains);
                                        }
                                    }).start();

                                    //添加到地图
                                    addGrainToOverlay(mGrains);
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

    // 首次加载到地图，保证所有图片下载好
    private void addGrainToOverlay(final List<ClusterGrain> objects) {
        overlay.clearClusters();
        for (final ClusterGrain cg : objects) {
            try {
                final String to = FileUtils.getAppCache(getActivity(), "portrait")
                        + FileUtils.getFileName(cg.userPortrait);
                final String key = OssManager.getFileKeyByRemoteUrl(cg.userPortrait);
                File file = new File(to);
                if (!file.exists()) {
                    OSSFile ossFile = new OSSFile(OssManager.getOssManager().ossBucket, key);
                    ossFile.downloadToInBackground(to, new GetFileCallback() {
                        @Override
                        public void onSuccess(String s, String s1) {
                            cg.userPortrait = to;
                            overlay.addClusterItem(cg);
                        }

                        @Override
                        public void onProgress(String s, int i, int i1) {

                        }

                        @Override
                        public void onFailure(String s, OSSException e) {

                        }
                    });
                } else {
                    cg.userPortrait = to;
                    overlay.addClusterItem(cg);
                }
            } catch (Exception e) {
                cg.userPortrait = "";
                overlay.addClusterItem(cg);
            }
        }
    }

    private void saveGrainToDb(List<ClusterGrain> objects) {
        for (ClusterGrain cg : objects) {
            MGrain mGrain = new MGrain();
            mGrain.setGrainId(cg.grainId);
            mGrain.setUserId(cg.userId);
            mGrain.setCateId(cg.cateId);
            mGrain.setNickName(cg.nickName);
            mGrain.setRemark(cg.remark);
            mGrain.setText(cg.text);
            mGrain.setUserPortrait(cg.userPortrait);
            mGrain.setSiteId(cg.site.siteId);
            mGrain.setSource(cg.site.source);
            mGrain.setAddress(cg.site.address);
            mGrain.setName(cg.site.name);
            mGrain.setPhone(cg.site.phone);
            mGrain.setGtype(cg.site.gtype);
            mGrain.setMtype(cg.site.mtype);
            mGrain.setLat(cg.site.lat);
            mGrain.setLon(cg.site.lon);

            DaoManager.getManager().daoSession.getMGrainDao().insertOrReplace(mGrain);
        }
    }

    private List<ClusterGrain> getGrainsFromDb(int type) {
        List<MGrain> grains;
        if (type == TYPE_ALL) {
            grains = DaoManager.getManager().daoSession.getMGrainDao().loadAll();
        } else {
            QueryBuilder qb = DaoManager.getManager().daoSession.getMGrainDao().queryBuilder();
            qb.where(MGrainDao.Properties.CateId.eq(CreateGrainActivity.mCateId[type]));
            grains = qb.list();
        }

        List<ClusterGrain> cgs = new ArrayList<>();
        for (MGrain mg : grains) {
            ClusterGrain cg = new ClusterGrain();
            cg.grainId = mg.getGrainId();
            cg.userId = mg.getUserId();
            cg.cateId = mg.getCateId();
            cg.nickName = mg.getNickName();
            cg.remark = mg.getRemark();
            cg.text = mg.getText();
            cg.userPortrait = mg.getUserPortrait();
            cg.site = new ClusterGrain.ClusterSite();
            cg.site.siteId = mg.getSiteId();
            cg.site.source = mg.getSource();
            cg.site.address = mg.getAddress();
            cg.site.name = mg.getName();
            cg.site.phone = mg.getPhone();
            cg.site.gtype = mg.getGtype();
            cg.site.mtype = mg.getMtype();
            cg.site.lat = mg.getLat();
            cg.site.lon = mg.getLon();
            cgs.add(cg);
        }
        return cgs;
    }

    // 通过首次加载后，可以直接从内存加载
    private List<ClusterGrain> getGrainFromMem(int type) {
        List<ClusterGrain> list = new ArrayList<>();
        if (mGrains == null || mGrains.size() < 1) {
            return list;
        }
        if (type == TYPE_ALL) {
            list.addAll(mGrains);
        } else if (mGrains != null && mGrains.size() > 0) {
            for (ClusterGrain c : mGrains) {
                if (c.cateId.equals(CreateGrainActivity.mCateId[type])) {
                    list.add(c);
                }
            }
        }
        return list;
    }

    /**
     * 开始进行poi搜索
     */
    protected void searchPoiByKeyword(String str) {
        mQuery = new PoiSearch.Query(str, "", mMapInfo.getCityCode());
        mQuery.setPageSize(1);// 设置每页最多返回多少条poiitem
        mQuery.setPageNum(10);// 设置查第一页
        mQuery.setLimitDiscount(false);
        mQuery.setLimitGroupbuy(false);

        PoiSearch poiSearch = new PoiSearch(getActivity(), mQuery);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        if (i == 0) {
            // 搜索POI的结果
            if (poiResult != null && poiResult.getQuery() != null) {
                // 是否是同一条
                if (poiResult.getQuery().equals(mQuery)) {
                    // 取得第一页的poiitem数据，页数从数字0开始
                    List<PoiItem> poiItems = poiResult.getPois();
                    // 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

                    if (poiItems != null && poiItems.size() > 0) {
                        PoiItem item = null;
                        for (PoiItem pi : poiItems) {
                            if (pi != null) {
                                item = pi;
                                break;
                            }
                        }
                        if (item == null) {
                            Toast.makeText(getActivity(), "无搜索结果", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        LatLng latLng = AMapUtils.convertToLatlng(item.getLatLonPoint());
                        goSomewhereWithAnimation(latLng);//去那个地方
                    } else {
//                        Toast.makeText(getActivity(), "R.string.no_result:" + R.string.no_result, Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
//                Toast.makeText(getActivity(), "R.string.no_result:" + R.string.no_result, Toast.LENGTH_SHORT).show();
            }
        } else {
//            Toast.makeText(getActivity(), "R.string.error_network:" + R.string.error_network, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPoiItemDetailSearched(PoiItemDetail poiItemDetail, int i) {

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case SEARCH_POSITION_REQUEST_CODE:
                    String keyword = data.getStringExtra("keyword");
                    searchPoiByKeyword(keyword);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class AddClusterAsyncTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            httpQueryGrain(params[params[0]]);
            return null;
        }
    }
}