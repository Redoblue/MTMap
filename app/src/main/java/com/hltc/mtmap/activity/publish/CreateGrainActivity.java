package com.hltc.mtmap.activity.publish;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.hltc.mtmap.R;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.bean.ParcelableGrain;
import com.hltc.mtmap.fragment.MapFragment;
import com.hltc.mtmap.helper.PhotoHelper;
import com.hltc.mtmap.util.AMapUtils;
import com.hltc.mtmap.util.AppUtils;
import com.hltc.mtmap.util.FileUtils;
import com.hltc.mtmap.util.StringUtils;
import com.hltc.mtmap.util.ViewUtils;
import com.hltc.mtmap.widget.AddPhotoGridView;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class CreateGrainActivity extends Activity implements AMap.OnMapLoadedListener,
        AMap.OnCameraChangeListener, AMapLocationListener, PoiSearch.OnPoiSearchListener {

    public static final String[] M_CATE_ID = {
            "010000", "020000", "990000"
    };
    private static final int TAKE_PHOTO = 1;
    private static final int AUTO_COMPLETE = 2;
    @InjectView(R.id.layout_create_grain_root)
    LinearLayout rootView;
    @InjectView(R.id.sv_create_grain)
    ScrollView scrollView;
    @InjectView(R.id.tv_bar_title)
    TextView barTitle;
    @InjectView(R.id.btn_bar_left)
    Button cancelAction;
    @InjectView(R.id.btn_bar_right)
    Button doneAction;
    @InjectView(R.id.create_grain_view_map)
    MapView mMapView;
    @InjectView(R.id.layout_create_grain_map)
    RelativeLayout mapLayout;
    @InjectView(R.id.tv_create_grain_map_hint)
    TextView mapHintTextView;
    @InjectView(R.id.btn_create_grain_address)
    Button addressButton;
    @InjectView(R.id.create_grain_st_public)
    ToggleButton publicToggle;
    @InjectView(R.id.create_grain_et_comment)
    EditText commentEditText;
    @InjectView(R.id.create_grain_gv_photos)
    AddPhotoGridView photosGridView;
    //    private GridAdapter adapter;
    private AMap mAmap;
    private GridAdapter adapter;
    private LatLng targetLocation;
    private LatLng myLocation;
    private LocationManagerProxy locationManagerProxy;
    private PoiSearch poiSearch;
    private PoiSearch.Query query;
    private List<PoiItem> poiItems = new ArrayList<>();// poi数据
    private List<String> poiTitles = new ArrayList<>();
    private boolean isMapZoomedIn;
    // 拍照生成的地址
    private int intentType;
    private String poiCity;
    private String path = "";
    private String oldString = "";
    private String returnedValue = "";
    private String[] createTypes = {
            "吃喝", "玩乐", "其他"
    };
    private String[] guideCate = {
            "050000", "080000", ""
    };

    private boolean isFromFavourite = false;

    private PopupWindow photoWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_grain);
        ButterKnife.inject(this);

        intentType = getIntent().getIntExtra("create_type", 0);
        isFromFavourite = getIntent().getBooleanExtra("from_favourite", false);
        if (isFromFavourite) {

        }

        mMapView.onCreate(savedInstanceState);
        initAmap();
        initView();
    }

    private void initAmap() {
        if (mAmap == null) {
            mAmap = mMapView.getMap();
        }

        mAmap.setMyLocationEnabled(true);
        mAmap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        mAmap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (!isMapZoomedIn) {
                    zoomInMap(true);
                }
            }
        });
        locationManagerProxy = LocationManagerProxy.getInstance(this);
        locationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 15, this);

        UiSettings uiSettings = mAmap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);
    }

    @OnClick({R.id.btn_bar_left, R.id.btn_bar_right})
    public void onClick(View v) {
        if (!isMapZoomedIn) {
            if (v.getId() == R.id.btn_bar_left) {
                AppManager.getAppManager().finishActivity(this);
            } else {
                if (!AppUtils.isNetworkConnected(this)) {
                    Toast.makeText(this, "请检查您的网络", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtils.isEmpty(addressButton.getText().toString())) {
                    Toast.makeText(this, "还没有设置地址哦！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtils.isEmpty(commentEditText.getText().toString())) {
                    Toast.makeText(this, "还是说点儿什么吧！", Toast.LENGTH_SHORT).show();
                    return;
                }

                ParcelableGrain grain = new ParcelableGrain();
                grain.userId = AppConfig.getAppConfig().getConfUsrUserId();
                grain.token = AppConfig.getAppConfig().getConfToken();
                grain.mcateId = M_CATE_ID[intentType];

                if (poiTitles.contains(returnedValue)) {
                    PoiItem selectedItem = new PoiItem("", AMapUtils.convertToLatLonPoint(targetLocation), "", "");
                    for (PoiItem item : poiItems) {
                        if (item.getTitle().equals(returnedValue)) {
                            selectedItem = item;
                            break;
                        }
                    }
                    grain.siteSource = "1";
                    grain.siteId = selectedItem.getPoiId();
                    grain.siteName = selectedItem.getTitle();
                    grain.siteAddress = selectedItem.getAdName();
                    grain.sitePhone = selectedItem.getTel();
                    grain.siteType = selectedItem.getTypeDes();
                    grain.latitude = String.valueOf(selectedItem.getLatLonPoint().getLatitude());
                    grain.longitude = String.valueOf(selectedItem.getLatLonPoint().getLongitude());
                    grain.cityCode = selectedItem.getCityCode();
                    grain.isPublic = publicToggle.isChecked() ? "1" : "0";
                    grain.text = commentEditText.getText().toString().trim();
                } else if (poiItems.size() > 0) {
                    PoiItem tempPoi = poiItems.get(0);
                    grain.siteSource = "0";
                    grain.siteId = "";
                    grain.siteName = returnedValue;
                    grain.siteAddress = tempPoi.getAdName();
                    grain.sitePhone = "";
                    grain.latitude = String.valueOf(tempPoi.getLatLonPoint().getLatitude());
                    grain.longitude = String.valueOf(tempPoi.getLatLonPoint().getLongitude());
                    grain.cityCode = tempPoi.getCityCode();
                    grain.isPublic = publicToggle.isChecked() ? "1" : "0";
                    grain.text = commentEditText.getText().toString().trim();
                } else {
                    // 未获取到位置
                    //TODO
                }

                Intent publishIntent = new Intent(this, DonePublishDialog.class);
                publishIntent.putExtra("GRAIN", grain);
                startActivity(publishIntent);
            }
        } else {
            targetLocation = mAmap.getCameraPosition().target;
            zoomInMap(false);
            doSearchQuery();
        }
    }

    private void initView() {
        barTitle.setText("创建" + createTypes[intentType]);

        photosGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        adapter.update();
        photosGridView.setAdapter(adapter);
        photosGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == PhotoHelper.bitmaps.size()) {
//                    new MyPopupWindow(CreateGrainActivity.this, photosGridView);
                    showPopwindow();
                } else {
                    Intent intent = new Intent(CreateGrainActivity.this,
                            DeletePhotoActivity.class);
                    intent.putExtra("ID", arg2);
                    startActivity(intent);
                }
            }
        });

        addressButton.setHint(ViewUtils.getHint("请输入地址", 20));
        addressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (poiItems.isEmpty()) {
                    Toast.makeText(CreateGrainActivity.this, "再等一秒", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent autoCompleteIntent = new Intent(CreateGrainActivity.this, CompleteAddressActivity.class);
                autoCompleteIntent.putExtra("TITLE_LIST", (Serializable) poiTitles);
                autoCompleteIntent.putExtra("OLD_CONTENT", addressButton.getText().toString());
                startActivityForResult(autoCompleteIntent, AUTO_COMPLETE);
            }
        });

        //监听软键盘
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    scrollView.smoothScrollTo(0, AMapUtils.dp2px(CreateGrainActivity.this, 102));
                } else {
                    scrollView.smoothScrollTo(0, 0);
                }
            }
        });

        commentEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm.isActive()) {
                            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                        break;
                }
                return true;
            }
        });
    }

    private void zoomInMap(boolean yes) {
        LinearLayout.LayoutParams params = new
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                yes ? ViewGroup.LayoutParams.MATCH_PARENT :
                        AMapUtils.dp2px(CreateGrainActivity.this, 185));
        mapLayout.setLayoutParams(params);
        barTitle.setText(yes ? "标记位置" : "创建" + createTypes[intentType]);
        cancelAction.setBackgroundResource(
                yes ? R.drawable.ic_action_arrow_left :
                        R.drawable.ic_action_cancel);
        isMapZoomedIn = yes;
        mapHintTextView.setVisibility(yes ? View.INVISIBLE : View.VISIBLE);
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        query = new PoiSearch.Query("", guideCate[intentType], poiCity);
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(1);// 设置查第一页
        query.setLimitDiscount(false);
        query.setLimitGroupbuy(false);

        if (targetLocation != null) {
            poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new PoiSearch.SearchBound(
                    AMapUtils.convertToLatLonPoint(targetLocation), 2000, true));
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }

    @Override
    public void onMapLoaded() {
        Log.d("Publish", "onMapLoader");
        //加载完地图进入上次最后地点
        if (!StringUtils.isEmpty(MapFragment.mMapInfo.getLatitude())) {
            LatLng latLng = new LatLng(StringUtils.toDouble(
                    MapFragment.mMapInfo.getLatitude()),
                    StringUtils.toDouble(MapFragment.mMapInfo.getLongitude()));
            mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, MapFragment.DEFAULT_ZOOM));
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        Log.d("Publish", "cameraPosition.target:" + cameraPosition.target);
    }

    // 获取操作结束后的位置
    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        Log.d("Publish", "onCameraChangeFinish");
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0) {
            myLocation = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f));
            poiCity = aMapLocation.getCity();
            targetLocation = myLocation;
            doSearchQuery(); // 第一次搜索
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
//        AMapUtils.toggleGPS(this);
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        if (i == 0) {
            if (poiResult != null && poiResult.getQuery() != null) {// 搜索poi的结果
                if (poiResult.getQuery().equals(query)) {// 是否是同一条
                    poiItems.clear();//清空原来的数据
                    poiTitles.clear();
                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    for (PoiItem item : poiItems) {
                        poiTitles.add(item.getTitle());
                    }
                }
            }
        } else if (i == 27) {
            // error network
        } else if (i == 32) {
            // error key
        } else {
            // error other
        }
    }

    @Override
    public void onPoiItemDetailSearched(PoiItemDetail poiItemDetail, int i) {

    }

    protected void onRestart() {
        adapter.update();
        super.onRestart();
    }

    public void takePhoto() {

        File file = FileUtils.createFile(AppConfig.DEFAULT_APP_ROOT_PATH
                + "photo/", StringUtils.getUUID() + ".jpeg");
        path = file.getPath();

        Uri imageUri = Uri.fromFile(file);
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(openCameraIntent, TAKE_PHOTO);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (PhotoHelper.addresses.size() < 9) {
                        PhotoHelper.addresses.add(path);
                    }
                }
                break;
            case AUTO_COMPLETE:
                if (resultCode == RESULT_OK) {
                    returnedValue = data.getStringExtra("SELECTED_POI");
                    addressButton.setText(returnedValue);

                    //关掉输入法
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
        }
    }

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
        FileUtils.deleteDir();
        mMapView.onDestroy();
        locationManagerProxy.destroy();
        AppManager.getAppManager().finishActivity(this);
    }

    private void showPopwindow() {
        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.window_publish_choose_photo, null);
        // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
        photoWindow = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        photoWindow.setFocusable(true);
        photoWindow.setOutsideTouchable(true);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f; //0.0-1.0
        getWindow().setAttributes(lp);
//        photoWindow.setBackgroundDrawable(new ColorDrawable(R.color.half_transparent));
        // 实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0xb0000000);
//        photoWindow.setBackgroundDrawable(dw);
        // 设置popWindow的显示和消失动画
        photoWindow.setAnimationStyle(android.R.style.Widget_PopupWindow);
        // 在底部显示
        photoWindow.showAtLocation(this.findViewById(R.id.create_grain_gv_photos),
                Gravity.BOTTOM, 0, 0);
        photoWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f; //0.0-1.0
                getWindow().setAttributes(lp);
            }
        });

        Button camera = (Button) view.findViewById(R.id.btn_window_publish_camera);
        Button photo = (Button) view.findViewById(R.id.btn_window_publish_photo);
        Button cancel = (Button) view.findViewById(R.id.btn_window_publish_cancel);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
                photoWindow.dismiss();
            }
        });
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateGrainActivity.this,
                        GalleryActivity.class);
                startActivity(intent);
                photoWindow.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoWindow.dismiss();
            }
        });
    }

    @SuppressLint("HandlerLeak")
    public class GridAdapter extends BaseAdapter {
        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                }
                super.handleMessage(msg);
            }
        };
        private LayoutInflater inflater; // 视图容器

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update() {
            loading();
        }

        public int getCount() {
            return (PhotoHelper.bitmaps.size() + 1);
        }

        public Object getItem(int arg0) {

            return null;
        }

        public long getItemId(int arg0) {

            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_create_grain_photo, parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.create_grain_grid_img_photo);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (position == PhotoHelper.bitmaps.size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.icon_addpic_unfocused));
                if (position == 9) {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                holder.image.setImageBitmap(PhotoHelper.bitmaps.get(position));
            }

            return convertView;
        }

        public void loading() {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        if (PhotoHelper.max == PhotoHelper.addresses.size()) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        } else {
                            try {
                                String path = PhotoHelper.addresses.get(PhotoHelper.max);
                                Bitmap bitmap = PhotoHelper.resizeBitmapFromPath(path);
                                PhotoHelper.bitmaps.add(bitmap);
                                String where = FileUtils.saveBitmap(bitmap, StringUtils.getUUID()); //重命名照片
                                PhotoHelper.larges.add(where);
                                PhotoHelper.max += 1;
                                Message message = new Message();
                                message.what = 1;
                                handler.sendMessage(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }).start();
        }

        public class ViewHolder {
            public ImageView image;
        }
    }
}
