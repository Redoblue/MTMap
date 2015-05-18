package com.hltc.mtmap.activity;

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
import android.view.animation.AnimationUtils;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import com.hltc.mtmap.helper.PhotoHelper;
import com.hltc.mtmap.util.AMapUtils;
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

/**
 * Created by redoblue on 15-5-10.
 */
public class CreateGrainActivity extends Activity implements AMap.OnMapLoadedListener,
        AMap.OnCameraChangeListener, AMapLocationListener, PoiSearch.OnPoiSearchListener {

    private static final int TAKE_PICTURE = 1;
    private static final int AUTO_COMPLETE = 2;

    @InjectView(R.id.layout_create_grain_root)
    LinearLayout rootView;
    @InjectView(R.id.sv_create_grain)
    ScrollView scrollView;
    @InjectView(R.id.bar_create_grain_green)
    RelativeLayout greenBar;
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
    Switch publicSwitch;
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
    private String[] types = {
            "餐饮", "体育", ""
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_grain);
        ButterKnife.inject(this);
        intentType = getIntent().getIntExtra("create_type", 0);
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
    public void execAction(View v) {
        if (!isMapZoomedIn) {
            if (v.getId() == R.id.btn_bar_left) {
                AppManager.getAppManager().finishActivity(this);
            } else {
                if (StringUtils.isEmpty(addressButton.getText().toString())) {
                    Toast.makeText(this, "还没有设置地址哦！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtils.isEmpty(commentEditText.getText().toString())) {
                    Toast.makeText(this, "还是说点儿什么吧！", Toast.LENGTH_SHORT).show();
                    return;
                }

                ParcelableGrain grain = new ParcelableGrain();
                grain.userId = AppConfig.getAppConfig(this).getUsrId();
                grain.token = AppConfig.getAppConfig(this).getToken();
                grain.mcateId = types[intentType];

                if (poiTitles.contains(returnedValue)) {
                    PoiItem selectedItem = new PoiItem("", AMapUtils.convertToLatLonPoint(targetLocation), "", "");
                    for (PoiItem item : poiItems) {
                        if (item.getTitle().equals(returnedValue)) {
                            selectedItem = item;
                            break;
                        }
                    }
                    grain.siteSource = 1;
                    grain.siteId = selectedItem.getPoiId();
                    grain.siteName = selectedItem.getTitle();
                    grain.siteAddress = selectedItem.getAdName();
                    grain.sitePhone = selectedItem.getTel();
                    grain.siteType = selectedItem.getTypeDes();
                    grain.latitude = selectedItem.getLatLonPoint().getLatitude();
                    grain.longitude = selectedItem.getLatLonPoint().getLongitude();
                    grain.cityCode = selectedItem.getCityCode();
                    grain.isPublic = 1;
                    grain.text = commentEditText.getText().toString().trim();
                } else if (poiItems.size() > 0) {
                    PoiItem tempPoi = poiItems.get(0);
                    grain.siteSource = 0;
                    grain.siteId = "";
                    grain.siteName = returnedValue;
                    grain.siteAddress = tempPoi.getAdName();
                    grain.sitePhone = "";
                    grain.latitude = tempPoi.getLatLonPoint().getLatitude();
                    grain.longitude = tempPoi.getLatLonPoint().getLongitude();
                    grain.cityCode = tempPoi.getCityCode();
                    grain.isPublic = 1;
                    grain.text = commentEditText.getText().toString().trim();
                } else {
                    grain.siteSource = 0;
                    grain.siteId = "";
                    grain.siteName = returnedValue;
                    grain.siteAddress = returnedValue;
                    grain.sitePhone = "";
                    grain.latitude = myLocation.latitude;
                    grain.longitude = myLocation.longitude;
                    grain.cityCode = "";
                    grain.isPublic = 1;
                    grain.text = commentEditText.getText().toString().trim();
                }

                Intent publishIntent = new Intent(this, DonePublishActivity.class);
                publishIntent.putExtra("GRAIN", grain);
                startActivity(publishIntent);

                //TODO 上传到阿里云
                // 高清的压缩图片全部就在  list 路径里面了
                // 高清的压缩过的 bitmaps 对象  都在 PhotoHelper.bmp里面
                // 完成上传服务器后 .........
//                FileUtils.deleteDir();
            }
        } else {
            targetLocation = mAmap.getCameraPosition().target;
            zoomInMap(false);
            doSearchQuery();
        }
    }

    private void initView() {
        barTitle.setText("创建" + createTypes[intentType]);
        cancelAction.setBackgroundResource(R.drawable.ic_action_cancel);
        doneAction.setBackgroundResource(R.drawable.ic_action_done);
        cancelAction.setWidth(AMapUtils.dp2px(this, 25));
        cancelAction.setHeight(AMapUtils.dp2px(this, 25));
        doneAction.setWidth(AMapUtils.dp2px(this, 25));
        doneAction.setHeight(AMapUtils.dp2px(this, 25));

        photosGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        adapter.update();
        photosGridView.setAdapter(adapter);
        photosGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == PhotoHelper.bitmaps.size()) {
                    new MyPopupWindow(CreateGrainActivity.this, photosGridView);
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
                Intent autoCompleteIntent = new Intent(CreateGrainActivity.this, CompleteTextActivity.class);
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
        query = new PoiSearch.Query("", types[intentType], poiCity);
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
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        Log.d("CreateGrainActivity", "cameraPosition.target:" + cameraPosition.target);
    }

    // 获取操作结束后的位置
    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {

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
                        Log.d("Publish", item.getTitle());
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
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
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
//                                String name = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
//                                FileUtils.saveBitmap(bitmap, "" + name);
                                String uuid = StringUtils.getUUID();
                                FileUtils.saveBitmap(bitmap, uuid); //重命名照片
                                PhotoHelper.larges.add(AppConfig.DEFAULT_APP_ROOT_PATH + "photo/" + uuid + ".jpeg");
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

    public class MyPopupWindow extends PopupWindow {

        public MyPopupWindow(Context mContext, View parent) {

            View view = View.inflate(mContext, R.layout.item_popup_window, null);
            view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_ins));
            LinearLayout layout = (LinearLayout) view.findViewById(R.id.ll_popup);
            layout.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_bottom_in_2));

            setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
//            setBackgroundDrawable(new BitmapDrawable());
            setBackgroundDrawable(new ColorDrawable(R.color.half_transparent));
            setBackgroundAlpha(0.5f);
            setOnDismissListener(new MyOnDismissListener());
            setFocusable(true);
            setOutsideTouchable(true);
            setContentView(view);
            showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            update();

            Button camera = (Button) view.findViewById(R.id.item_popupwindows_camera);
            Button gallery = (Button) view.findViewById(R.id.item_popupwindows_Photo);
            Button cancel = (Button) view.findViewById(R.id.item_popupwindows_cancel);
            camera.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    takePhoto();
                    dismiss();
                }
            });
            gallery.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(CreateGrainActivity.this,
                            GalleryActivity.class);
                    startActivity(intent);
                    dismiss();
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dismiss();
                }
            });
        }

        /**
         * 设置添加屏幕的背景透明度
         *
         * @param bgAlpha
         */
        private void setBackgroundAlpha(float bgAlpha) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = bgAlpha; //0.0-1.0
            getWindow().setAttributes(lp);
        }

        /**
         * 将背景透明度改回来
         *
         * @author cg
         */
        class MyOnDismissListener implements PopupWindow.OnDismissListener {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1f);
            }
        }
    }
}
