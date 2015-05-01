package com.amp.apis.libc;

import android.graphics.drawable.Drawable;
import com.amap.api.maps.model.LatLng;

/**
 * @author yiyi.qi 每个聚合元素类的接口
 */
public interface ClusterItem {

	/**
	 * 返回聚合元素的地理位置
	 * 
	 * @return
	 */
	public LatLng getPosition();

//	public Drawable getDrawable();
    public String getPicUrl();
}

