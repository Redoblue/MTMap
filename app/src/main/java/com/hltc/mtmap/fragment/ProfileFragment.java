package com.hltc.mtmap.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ecloud.pulltozoomview.PullToZoomScrollViewEx;
import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.MainActivity;
import com.hltc.mtmap.activity.profile.FriendListActivity;
import com.hltc.mtmap.activity.profile.MyFavouritesActivity;
import com.hltc.mtmap.activity.profile.MyGrainActivity;
import com.hltc.mtmap.activity.profile.SettingsActivity;
import com.hltc.mtmap.activity.start.StartActivity;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.AppManager;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.app.OssManager;
import com.hltc.mtmap.event.BaseMessageEvent;
import com.hltc.mtmap.util.AMapUtils;
import com.hltc.mtmap.util.ApiUtils;
import com.hltc.mtmap.util.FileUtils;
import com.hltc.mtmap.util.StringUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    /**
     * 请求码
     */
    public static final int IMAGE_REQUEST_CODE = 0;
    public static final int CAMERA_REQUEST_CODE = 1;
    public static final int RESULT_REQUEST_CODE = 2;


    @InjectView(R.id.sv_profile)
    PullToZoomScrollViewEx scrollView;

    private String[] ways = new String[]{"选择本地图片", "拍照"};
    private String type = "portrait";

    private CircleImageView portraitCiv;
    private ImageView cover;

    private ImageView ivRedTip;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        EventBus.getDefault().register(this);

        if (MainActivity.isVisitor) {
            View view = inflater.inflate(R.layout.window_remind_login, container, false);
            ImageView iv = (ImageView) view.findViewById(R.id.btn_remind_login);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), StartActivity.class);
                    startActivity(intent);
                    AppManager.getAppManager().finishActivity(MainActivity.class);
                }
            });
            return view;
        } else {
            View view = inflater.inflate(R.layout.fragment_profile, container, false);
            ButterKnife.inject(this, view);
            initView();
            return view;
        }
    }

    public void onEvent(BaseMessageEvent event) {
        switch (event.action) {
            case BaseMessageEvent.EVENT_FRIENTLIST_RED_ROT_SHOW:
                ivRedTip.setVisibility(View.VISIBLE);
                break;
            case BaseMessageEvent.EVENT_FRIENTLIST_RED_ROT_HIDE:
                ivRedTip.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!MainActivity.isVisitor)
            initView();
    }

    private void initView() {
        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_profile_header_view, null, false);
        View zoomView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_profile_zoom_view, null, false);
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_profile_content_view, null, false);
        scrollView.setHeaderView(headerView);
        scrollView.setZoomView(zoomView);
        scrollView.setScrollContentView(contentView);

        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenHeight = localDisplayMetrics.heightPixels;
        int mScreenWidth = localDisplayMetrics.widthPixels;
//        LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(mScreenWidth, (int) (9.0F * (mScreenHeight / 16.0F)));
        LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(mScreenWidth, AMapUtils.dp2px(getActivity(), 270));
        scrollView.setHeaderLayoutParams(localObject);

        scrollView.getPullRootView().findViewById(R.id.btn_profile_settings).setOnClickListener(this);
        scrollView.getPullRootView().findViewById(R.id.btn_profile_maitian).setOnClickListener(this);
        scrollView.getPullRootView().findViewById(R.id.btn_profile_favourite).setOnClickListener(this);
        scrollView.getPullRootView().findViewById(R.id.btn_profile_friend).setOnClickListener(this);

        ivRedTip = (ImageView) scrollView.getPullRootView().findViewById(R.id.iv_red_tip_pro);

        if (MyApplication.isShowRedTipPro) {
            ivRedTip.setVisibility(View.VISIBLE);
        } else {
            ivRedTip.setVisibility(View.INVISIBLE);
        }

        //编辑头像
        portraitCiv = (CircleImageView) scrollView.getPullRootView().findViewById(R.id.civ_profile_header_pic);
        ImageLoader.getInstance().displayImage(AppConfig.getAppConfig()
                .getConfUsrPortrait(), portraitCiv, MyApplication.displayImageOptions);
        portraitCiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "portrait";
                showDialog();
            }
        });

        String imageUrl = AppConfig.getAppConfig()
                .getConfUsrCoverImg();

        cover = (ImageView) scrollView.getPullRootView().findViewById(R.id.iv_profile_header_cover);
        ImageLoader.getInstance().displayImage(imageUrl, cover, MyApplication.displayImageOptions, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                ((ImageView)view).setBackgroundResource(R.drawable. pic_profile_cover);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                ((ImageView)view).setImageDrawable(null);
                ((ImageView)view).setBackgroundDrawable(new BitmapDrawable(loadedImage));
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

        cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "cover";
                showDialog();
            }
        });

        //昵称和签名
        TextView nickName = (TextView) scrollView.getPullRootView().findViewById(R.id.tv_profile_header_nickname);
        nickName.setText(AppConfig.getAppConfig().getConfUsrNickName());

        //更新麦粒数量
        ((TextView) scrollView.getPullRootView().findViewById(R.id.tv_profile_chihe))
                .setText(AppConfig.getAppConfig().get(AppConfig.CONFIG_GRAIN, AppConfig.CONF_GRAIN_CHIHE));
        ((TextView) scrollView.getPullRootView().findViewById(R.id.tv_profile_wanle))
                .setText(AppConfig.getAppConfig().get(AppConfig.CONFIG_GRAIN, AppConfig.CONF_GRAIN_WANLE));
        ((TextView) scrollView.getPullRootView().findViewById(R.id.tv_profile_other))
                .setText(AppConfig.getAppConfig().get(AppConfig.CONFIG_GRAIN, AppConfig.CONF_GRAIN_OTHER));
    }

    @Override
    public void onClick(View v) {
        Class toClass = null;
        switch (v.getId()) {
            case R.id.btn_profile_settings:
                toClass = SettingsActivity.class;
                break;
            case R.id.btn_profile_maitian:
                toClass = MyGrainActivity.class;
                break;
            case R.id.btn_profile_favourite:
                toClass = MyFavouritesActivity.class;
                break;
            case R.id.btn_profile_friend:
                toClass = FriendListActivity.class;
                break;
        }
        Intent intent = new Intent(getActivity(), toClass);
        startActivity(intent);
    }

    private void showDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle(type.equals("portrait") ? "设置头像" : "设置背景")
                .setItems(ways, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intentFromGallery = new Intent();
                                intentFromGallery.setType("image/*"); // 设置文件类型
                                intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
                                break;
                            case 1:
                                Intent intentFromCapture = new Intent(
                                        MediaStore.ACTION_IMAGE_CAPTURE);
                                // 判断存储卡是否可以用，可用进行存储
                                String state = Environment.getExternalStorageState();
                                if (state.equals(Environment.MEDIA_MOUNTED)) {
                                    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                                    File file = new File(path, "avatar.jpg");
                                    intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                                }

                                startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
                                break;
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
// 结果码不等于取消时候
        if (resultCode != Activity.RESULT_CANCELED) {
            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    if (type.equals("portrait"))
                        cropPortrait(data.getData());
                    else cropCover(data.getData());
                    break;
                case CAMERA_REQUEST_CODE:
                    // 判断存储卡是否可以用，可用进行存储
                    String state = Environment.getExternalStorageState();
                    if (state.equals(Environment.MEDIA_MOUNTED)) {
                        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                        File tempFile = new File(path, "avatar.jpg");
                        if (type.equals("portrait"))
                            cropPortrait(Uri.fromFile(tempFile));
                        else cropCover(Uri.fromFile(tempFile));
                    } else {
                        Toast.makeText(getActivity(), "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case RESULT_REQUEST_CODE: // 图片缩放完成后
                    if (data != null) {
                        Bitmap bitmap = data.getExtras().getParcelable("data");
                        String where = FileUtils.saveBitmap(bitmap, StringUtils.getUUID());
                        String remotePath = OssManager.getRemoteFileUrl(where);

                        if (type.equals("portrait")) {
                            httpUpdatePortrait(where, remotePath);
                        } else {
                            httpUpdateCoverImage(where, remotePath);
                        }
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪头像
     *
     * @param uri
     */
    public void cropPortrait(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 340);
        intent.putExtra("outputY", 340);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }

    /**
     * 裁剪背景
     *
     * @param uri
     */
    public void cropCover(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 720);
        intent.putExtra("outputY", 540);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }


    /**
     * 保存裁剪之后的图片数据
     *
     * @param
     */
    private void getImageToView(ImageView iv, Bitmap bitmap) {
        if (bitmap != null) {
            Drawable drawable = new BitmapDrawable(this.getResources(), bitmap);
            iv.setImageDrawable(drawable);
        }
    }

    private void httpUpdatePortrait(final String path, final String remote) {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_SOURCE, "Android");
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig().getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig().getConfToken());
            json.put(ApiUtils.KEY_PORTRAIT, remote);
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                ApiUtils.URL_ROOT + ApiUtils.URL_UPDATE_PORTRAIT,
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        if (result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                            AppConfig.getAppConfig().setConfUsrPortrait(remote);
                            portraitCiv.setImageDrawable(Drawable.createFromPath(path));
                            Toast.makeText(getActivity(), "头像更新成功", Toast.LENGTH_SHORT).show();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    OssManager.getOssManager().uploadImage(path, OssManager.getFileKeyByLocalUrl(path));
                                }
                            }).start();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Toast.makeText(getActivity(), "头像更新失败", Toast.LENGTH_SHORT).show();
                    }
                }

        );
    }

    private void httpUpdateCoverImage(final String path, final String remote) {
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        try {
            json.put(ApiUtils.KEY_SOURCE, "Android");
            json.put(ApiUtils.KEY_USER_ID, AppConfig.getAppConfig().getConfUsrUserId());
            json.put(ApiUtils.KEY_TOKEN, AppConfig.getAppConfig().getConfToken());
            json.put(ApiUtils.KEY_USR_COVER_IMG, remote);
            params.setBodyEntity(new StringEntity(json.toString(), HTTP.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                ApiUtils.URL_ROOT + "my/coverImg.json",
                params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        if (result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                            AppConfig.getAppConfig().setConfUsrCoverImg(remote);
                            cover.setImageDrawable(null);
                            cover.setBackgroundDrawable(Drawable.createFromPath(path));
                            //cover.setBackground(Drawable.createFromPath(path));
                            Toast.makeText(getActivity(), "背景更新成功", Toast.LENGTH_SHORT).show();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    OssManager.getOssManager().uploadImage(path, OssManager.getFileKeyByLocalUrl(path));
                                }
                            }).start();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        cover.setBackgroundResource(R.drawable.pic_profile_cover);
                        Toast.makeText(getActivity(), "背景更新失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
