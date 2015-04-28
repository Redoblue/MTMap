package com.amp.apis.libc;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;

public class Cluster {
	private Point mPoint;
	private LatLng mLatLng;
	private List<ClusterItem> mClusterItems;
	private Marker mMarker;

	Cluster(Point point, LatLng latLng) {
		mPoint = point;
		mLatLng = latLng;
		mClusterItems = new ArrayList<ClusterItem>();
	}

	void addClusterItem(ClusterItem clusterItem) {
		mClusterItems.add(clusterItem);
	}

	public int getClusterCount() {
		return mClusterItems.size();
	}

	Point getCenterPoint() {
		return mPoint;
	}

	LatLng getCenterLatLng() {
		return mLatLng;
	}

	void setMarker(Marker marker) {
		mMarker = marker;
	}

	Marker getMarker() {
		return mMarker;
	}

	public List<ClusterItem> getClusterItems() {
		return mClusterItems;
	}
}
