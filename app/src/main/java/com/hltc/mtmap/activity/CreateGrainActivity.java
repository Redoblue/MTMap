package com.hltc.mtmap.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.hltc.mtmap.R;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.helper.PhotoHelper;
import com.hltc.mtmap.util.AMapUtils;
import com.hltc.mtmap.util.FileUtils;
import com.hltc.mtmap.widget.AddPhotoGridView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by redoblue on 15-5-10.
 */
public class CreateGrainActivity extends Activity {

    @InjectView(R.id.layout_create_grain_green)
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
    @InjectView(R.id.layout_create_grain_adjust_map)
    RelativeLayout adjustMapRelativeLayout;
    @InjectView(R.id.create_grain_btn_fullscreen)
    Button mapFullscreenButton;
    @InjectView(R.id.create_grain_et_address)
    EditText addressEditText;
    @InjectView(R.id.create_grain_st_public)
    Switch publicSwitch;
    @InjectView(R.id.create_grain_et_comment)
    EditText commentEditText;
    @InjectView(R.id.create_grain_gv_photos)
    AddPhotoGridView photosGridView;
    @InjectView(R.id.create_grain_lv_results)
    ListView resultListView;

    //    private GridAdapter adapter;
    private AMap mAmap;
    private GridAdapter adapter;

    private boolean isMapZoomedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_grain);
        ButterKnife.inject(this);
        mMapView.onCreate(savedInstanceState);
        initAmap();
        initView();
    }

    private void initAmap() {
        if (mAmap == null) {
            mAmap = mMapView.getMap();
//            mAmap.setOnMapLoadedListener(mapLoadedListener);
        }
    }

    @OnClick({R.id.btn_bar_left, R.id.btn_bar_right})
    public void execAction(View v) {
        if (!isMapZoomedIn) {
            if (v.getId() == R.id.btn_bar_left) {
                AppManager.getAppManager().finishActivity(this);
            } else {

                //TODO
                List<String> list = new ArrayList<>();
                for (int i = 0; i < PhotoHelper.addresses.size(); i++) {
                    String Str = PhotoHelper.addresses.get(i).substring(
                            PhotoHelper.addresses.get(i).lastIndexOf("/") + 1,
                            PhotoHelper.addresses.get(i).lastIndexOf("."));
                    list.add(AppConfig.DEFAULT_APP_ROOT_PATH + "photo/" + Str + ".jpeg");
                }
                // 高清的压缩图片全部就在  list 路径里面了
                // 高清的压缩过的 bitmaps 对象  都在 PhotoHelper.bmp里面
                // 完成上传服务器后 .........
                FileUtils.deleteDir();
            }
        } else {
            zoomInMap(false);
        }
    }

    private void initView() {
        barTitle.setText("创建吃喝");
        cancelAction.setBackgroundResource(R.drawable.ic_action_cancel);
        doneAction.setBackgroundResource(R.drawable.ic_action_done);
        cancelAction.setWidth(AMapUtils.dp2px(this, 25));
        cancelAction.setHeight(AMapUtils.dp2px(this, 25));
        doneAction.setWidth(AMapUtils.dp2px(this, 25));
        doneAction.setHeight(AMapUtils.dp2px(this, 25));

        adjustMapRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayMetrics localDisplayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
                int mScreenHeight = localDisplayMetrics.heightPixels;
                int mScreenWidth = localDisplayMetrics.widthPixels;

                if (!isMapZoomedIn) {
                    zoomInMap(true);
                } else {
                    zoomInMap(false);
                }
            }
        });

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
                            PhotoActivity.class);
                    intent.putExtra("ID", arg2);
                    startActivity(intent);
                }
            }
        });
    }

    private void zoomInMap(boolean yes) {
        LinearLayout.LayoutParams params = new
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                yes ? ViewGroup.LayoutParams.MATCH_PARENT :
                        AMapUtils.dp2px(CreateGrainActivity.this, 160));
        mapLayout.setLayoutParams(params);
        barTitle.setText(yes ? "标记位置" : "创建吃喝");
        cancelAction.setBackgroundResource(
                yes ? R.drawable.ic_action_arrow_left :
                        R.drawable.ic_action_cancel);
        isMapZoomedIn = yes;
    }


    @SuppressLint("HandlerLeak")
    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater; // 视图容器
        private int selectedPosition = -1;// 选中的位置
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

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

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final int coord = position;
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_create_grain_photo,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.create_grain_grid_img_photo);
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

        public class ViewHolder {
            public ImageView image;
        }

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
                                System.out.println(path);
                                Bitmap bm = PhotoHelper.resizeBitmapFromPath(path);
                                PhotoHelper.bitmaps.add(bm);
                                String newStr = path.substring(
                                        path.lastIndexOf("/") + 1,
                                        path.lastIndexOf("."));
                                FileUtils.saveBitmap(bm, "" + newStr);
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
    }

    public String getString(String s) {
        String path = null;
        if (s == null)
            return "";
        for (int i = s.length() - 1; i > 0; i++) {
            s.charAt(i);
        }
        return path;
    }

    protected void onRestart() {
        adapter.update();
        super.onRestart();
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
                    photo();
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
         * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
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

    private static final int TAKE_PICTURE = 0x000000;
    private String path = "";

    public void photo() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory()
                + "/myimage/", String.valueOf(System.currentTimeMillis())
                + ".jpg");
        path = file.getPath();
        Uri imageUri = Uri.fromFile(file);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (PhotoHelper.addresses.size() < 9 && resultCode == -1) {
                    PhotoHelper.addresses.add(path);
                }
                break;
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
        mMapView.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }
}
