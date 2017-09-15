package com.amap.naviquickstart.overlay;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaokun on 2017/9/11.
 * 此抽象父类是所有marker图层的父类
 */

public abstract class SuperMarkerOverlay<T>
{
    /**
     * marker数据源，可能是工地、国控点、消纳点等
     */
    public List<T> mDatas;
    /**
     * 接受传递进来的AMap参数
     */
    public AMap mAMap;
    /**
     * marker集合，方便对marker进行统一管理
     */
    public ArrayList<Marker> mSuperMarkers = new ArrayList<>();

    /**
     * 构建所有图层类的构造函数
     *
     * @param mDatas
     * @param mAMap
     */
    public SuperMarkerOverlay(AMap mAMap, List<T> mDatas)
    {
        this.mDatas = mDatas;
        this.mAMap = mAMap;
    }

    /**
     * 添加marker到地图上
     */
    public void addMarkerToMap()
    {
        try
        {
            for (int i = 0; i < mDatas.size(); i++)
            {
                Marker marker = mAMap.addMarker(getMarkerOptions(i));
                marker.setObject(i);
                mSuperMarkers.add(marker);
            }
        } catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 根据传递进来的集合数据来获得相对应的MarkerOptions
     * 具体情况在子类进行重写
     *
     * @param i
     * @return
     */
    protected abstract MarkerOptions getMarkerOptions(int i);

    /**
     * 移除marker
     */
    public void removeMarkerFromMap()
    {
        for (Marker marker : mSuperMarkers)
        {
            marker.remove();
        }
    }

    /**
     * 根据传进来的参数marker获得data在list中的位置
     *
     * @param marker
     * @return
     */
    public int getMarkerIndex(Marker marker)
    {
        int size = mSuperMarkers.size();
        for (int i = 0; i < size; i++)
        {
            if (mSuperMarkers.get(i).equals(marker))
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取对应index的item数据
     *
     * @param index
     * @return
     */
    public T getItem(int index)
    {
        if (index < 0 || index >= mDatas.size())
        {
            return null;
        }
        return mDatas.get(index);
    }


}
