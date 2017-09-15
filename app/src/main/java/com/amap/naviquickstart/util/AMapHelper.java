package com.amap.naviquickstart.util;

import com.amap.api.maps.AMap;

/**
 * Created by xiaokun on 2017/9/11.
 * AMap帮助类，链式调用
 */

public class AMapHelper
{
    private AMap amap;

    /**
     * 构造函数
     *
     * @param amap
     * @return
     */
    public AMapHelper(AMap amap)
    {
        this.amap = amap;
    }

    /**
     * 检测amap是否为空
     */
    public void checkNull()
    {
        if (amap == null)
        {
            throw new RuntimeException("必须首先调用AMapHelper(AMap amap)构造方法");
        }
    }

    /**
     * 设置地图类型，比如夜间模式、标准模式、卫星模式
     *
     * @param mapType
     * @return
     */
    public AMapHelper setMapType(int mapType)
    {
        checkNull();
        amap.setMapType(mapType);
        return this;
    }

    /**
     * 设置是否保留地图缩放按钮
     *
     * @param b
     * @return
     */
    public AMapHelper setZoomControlsEnabled(boolean b)
    {
        checkNull();
        amap.getUiSettings().setZoomControlsEnabled(b);
        return this;
    }

    /**
     * 设置是否保留地图旋转手势
     *
     * @param b
     * @return
     */
    public AMapHelper setRotateGesturesEnabled(boolean b)
    {
        checkNull();
        amap.getUiSettings().setRotateGesturesEnabled(b);
        return this;
    }

    /**
     * 设置是否保留地图倾斜手势
     *
     * @param b
     * @return
     */
    public AMapHelper setTiltGesturesEnabled(boolean b)
    {
        checkNull();
        amap.getUiSettings().setTiltGesturesEnabled(b);
        return this;
    }

    /**
     * 设置是否保留地图默认定位按钮
     *
     * @param b
     * @return
     */
    public AMapHelper setMyLocationButtonEnabled(boolean b)
    {
        checkNull();
        amap.getUiSettings().setMyLocationButtonEnabled(b);
        return this;
    }

    /**
     * 设置是否显示交通路线状况
     *
     * @param b
     * @return
     */
    public AMapHelper setTrafficEnabled(boolean b)
    {
        checkNull();
        amap.setTrafficEnabled(b);
        return this;
    }

    public AMap create()
    {
        checkNull();
        return this.amap;
    }
}
