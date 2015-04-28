package com.amp.apis.libc;

import com.amap.api.maps.model.BitmapDescriptor;

/**
 * @author yiyi.qi 聚合点的渲染规则
 */
public interface ClusterRender {
	/**
	 * @return 自定义的maker布局
	 */
//	public ViewGroup getClusterLayout();

	BitmapDescriptor getBitmapDescriptor(Cluster cluster);
}
