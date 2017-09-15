package com.amap.naviquickstart.overlay;

import android.content.Context;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkStep;
import com.amap.naviquickstart.util.AMapUtil;

import java.util.List;

/**
 * Created by xiaokun on 2017/9/11.
 */

public class WalkRouteOverlay extends RouteOverlay
{
    private PolylineOptions mPolylineOptions;

    private BitmapDescriptor walkStationDescriptor = null;

    private WalkPath walkPath;

    public WalkRouteOverlay(Context context, AMap amap, WalkPath path,
                            LatLonPoint start, LatLonPoint end)
    {
        super(context);
        this.mAMap = amap;
        this.walkPath = path;
        startPoint = AMapUtil.convertToLatLng(start);
        endPoint = AMapUtil.convertToLatLng(end);
    }

    public void addToMap()
    {
        initPolylineOptions();
        try
        {
            List<WalkStep> walkPaths = walkPath.getSteps();
            mPolylineOptions.add(startPoint);
            for (int i = 0; i < walkPaths.size(); i++)
            {
                WalkStep walkStep = walkPaths.get(i);
                LatLng latLng = AMapUtil.convertToLatLng(walkStep.getPolyline().get(0));

                addWalkStationMarkers(walkStep, latLng);
                addWalkPolyLines(walkStep);

            }
            mPolylineOptions.add(endPoint);
            addStartAndEndMarker();

            showPolyline();
        } catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 初始化线段属性
     */
    private void initPolylineOptions()
    {

        if (walkStationDescriptor == null)
        {
            walkStationDescriptor = getWalkBitmapDescriptor();
        }

        mPolylineOptions = null;

        mPolylineOptions = new PolylineOptions();
        mPolylineOptions.color(getWalkColor()).width(getRouteWidth());
    }

    /**
     * @param walkStep
     * @param position
     */
    private void addWalkStationMarkers(WalkStep walkStep, LatLng position)
    {
        addStationMarker(new MarkerOptions()
                .position(position)
                .title("\u65B9\u5411:" + walkStep.getAction()
                        + "\n\u9053\u8DEF:" + walkStep.getRoad())
                .snippet(walkStep.getInstruction()).visible(nodeIconVisible)
                .anchor(0.5f, 0.5f).icon(walkStationDescriptor));
    }

    /**
     * @param walkStep
     */
    private void addWalkPolyLines(WalkStep walkStep) {
        mPolylineOptions.addAll(AMapUtil.convertArrList(walkStep.getPolyline()));
    }

    private void showPolyline() {
        addPolyLine(mPolylineOptions);
    }
}
