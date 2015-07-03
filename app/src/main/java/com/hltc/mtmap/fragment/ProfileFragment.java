package com.hltc.mtmap.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import com.hltc.mtmap.activity.profile.SettingsActivity;
import com.hltc.mtmap.activity.start.StartActivity;
import com.hltc.mtmap.app.AppConfig;
import com.hltc.mtmap.app.MyApplication;
import com.hltc.mtmap.app.OssManager;
import com.hltc.mtmap.util.AMapUtils;
import com.hltc.mtmap.util.ApiUtils;
import com.hltc.mtmap.util.FileUtils;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    /**
     * 请求码
     */
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;

    @InjectView(R.id.sv_profile)
    PullToZoomScrollViewEx scrollView;

    private String[] ways = new String[]{"选择本地图片", "拍照"};

    private CircleImageView portraitCiv;

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
            View view = inflater.inflate(R.layout.fragment_profile, container, false);
            ButterKnife.inject(this, view);
            initView();
            return view;
        }
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

        //编辑头像
        portraitCiv = (CircleImageView) scrollView.getPullRootView().findViewById(R.id.civ_profile_header_pic);
        ImageLoader.getInstance().displayImage(AppConfig.getAppConfig()
                .getConfUsrPortrait(), portraitCiv, MyApplication.displayImageOptions);
        portraitCiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

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
                ToastUtils.showShort(getActivity(), "maitian");
                break;
            case R.id.btn_profile_favourite:
                break;
            case R.id.btn_profile_friend:
                toClass = FriendListActivity.class;
                break;
            default:
                break;
        }
        Intent intent = new Intent(getActivity(), toClass);
        startActivity(intent);
    }

    private void showDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("设置头像")
                .setItems(ways, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intentFromGallery = new Intent();
                                intentFromGallery.setType("image/*"); // 设置文件类型
                                intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intentFromGallery,
                                        IMAGE_REQUEST_CODE);
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
                    startPhotoZoom(data.getData());
                    break;
                case CAMERA_REQUEST_CODE:
                    // 判断存储卡是否可以用，可用进行存储
                    String state = Environment.getExternalStorageState();
                    if (state.equals(Environment.MEDIA_MOUNTED)) {
                        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                        File tempFile = new File(path, "avatar.jpg");
                        startPhotoZoom(Uri.fromFile(tempFile));
                    } else {
                        Toast.makeText(getActivity(), "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case RESULT_REQUEST_CODE: // 图片缩放完成后
                    if (data != null) {
                        Bitmap bitmap = data.getExtras().getParcelable("data");
                        String where = FileUtils.saveBitmap(bitmap, StringUtils.getUUID());
                        getImageToView(bitmap);
                        String remotePath = "http://" + OssManager.bucketName + "." + OssManager.ossHost + "/"
                                + OssManager.getRemotePath(where);
                        httpUpdatePortrait(where, remotePath);
                        //删除头像
//                        File file = new File(path);
//                        FileUtils.delFile(file);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
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
     * 保存裁剪之后的图片数据
     *
     * @param
     */
    private void getImageToView(Bitmap bitmap) {
        if (bitmap != null) {
            Drawable drawable = new BitmapDrawable(this.getResources(), bitmap);
            portraitCiv.setImageDrawable(drawable);
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
                        if (StringUtils.isEmpty(result))
                            return;
                        try {
                            if (result.contains(ApiUtils.KEY_SUCCESS)) {  //验证成功
                                AppConfig.getAppConfig().setConfUsrPortrait(remote);
                                Toast.makeText(getActivity(), "头像更新成功", Toast.LENGTH_SHORT).show();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        OssManager.getOssManager().uploadImage(path, OssManager.getRemotePath(path));
                                    }
                                }).start();
                            } else {
                                JSONObject girl = new JSONObject(result);
                                String errorMsg = girl.getString(ApiUtils.KEY_ERROR_MESSAGE);
                                if (errorMsg != null) {
                                    // 发送验证码失败
                                    // TODO 没有验证错误码
                                    Toast.makeText(getActivity(), "头像更新失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Toast.makeText(getActivity(), "头像更新失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
