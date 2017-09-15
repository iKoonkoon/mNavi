package com.amap.naviquickstart.overlay;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.naviquickstart.R;

import java.util.List;

/**
 * Created by xiaokun on 2017/9/11.
 */

public class TestOverlay extends SuperMarkerOverlay<PoiItem>
{
    /**
     * 构建所有图层类的构造函数
     *
     * @param mAMap
     * @param mDatas
     */
    public TestOverlay(AMap mAMap, List<PoiItem> mDatas)
    {
        super(mAMap, mDatas);
    }

    @Override
    protected MarkerOptions getMarkerOptions(int index)
    {
        return new MarkerOptions()
                .position(new LatLng(mDatas.get(index).getLatLonPoint()
                        .getLatitude(), mDatas.get(index)
                        .getLatLonPoint().getLongitude()))
                .title(getTitle(index)).snippet(getSnippet(index))
                .icon(getBitmapDescriptor(index));
    }

    /**
     * 获取详情
     *
     * @param index
     * @return
     */
    private String getSnippet(int index)
    {
        return mDatas.get(index).getSnippet();
    }

    /**
     * 获取标题
     *
     * @param index
     * @return
     */
    private String getTitle(int index)
    {
        return mDatas.get(index).getTitle();
    }

    /**
     * 获取图片源
     *
     * @param index
     * @return
     */
    private BitmapDescriptor getBitmapDescriptor(int index)
    {
        return BitmapDescriptorFactory.fromResource(R.mipmap.bubble_end);
    }

    public void moveMap()
    {
        LatLonPoint latLonPoint = mDatas.get(0).getLatLonPoint();
        LatLng latLng = new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
        mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom((latLng), 14));
    }
}
