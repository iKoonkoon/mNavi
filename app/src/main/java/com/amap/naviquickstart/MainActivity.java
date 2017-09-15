package com.amap.naviquickstart;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.naviquickstart.overlay.DrivingRouteOverlay;
import com.amap.naviquickstart.overlay.TestOverlay;
import com.amap.naviquickstart.overlay.WalkRouteOverlay;
import com.amap.naviquickstart.util.AMapHelper;
import com.amap.naviquickstart.util.AMapUtil;
import com.amap.naviquickstart.util.KeyBoardUtils;

public class MainActivity extends AppCompatActivity implements AMapLocationListener, PoiSearch.OnPoiSearchListener,
        RouteSearch.OnRouteSearchListener, View.OnClickListener
{
    private static final int ROUTE_TYPE_DRIVE = 2;
    private final int ROUTE_TYPE_WALK = 3;
    private AMap mMap;
    private PoiSearch mPoiSearch;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private Marker mLocationMarker;
    private Circle mLocationCircle;
    private TestOverlay mPoiOverlay;
    private AMapLocation mCurrentLocation;
    private LatLng curLatLng;
    private EditText editText;
    private TextView search;
    public static final String CURRENT_CITY = "武汉";
    private LinearLayout linearType;
    private RouteSearch mRouteSearch;
    private TextView driveSselecter;
    private TextView walkSelecter;
    private DriveRouteResult mDriveRouteResult;
    private WalkRouteResult mWalkRouteResult;
    private Context mContext;
    private TextView mRotueTimeDes;
    private LinearLayout bottomRootview;
    private Button startNavi;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mContext = this;
        editText = (EditText) findViewById(R.id.search_text);
        search = (TextView) findViewById(R.id.search_cancel);
        linearType = (LinearLayout) findViewById(R.id.selecters_rootview);
        driveSselecter = (TextView) findViewById(R.id.drive_selecter);
        walkSelecter = (TextView) findViewById(R.id.walk_selecter);
        mRotueTimeDes = (TextView) findViewById(R.id.distance);
        bottomRootview = (LinearLayout) findViewById(R.id.bottom_rootview);
        startNavi = (Button) findViewById(R.id.start_navi);
        setListener();
        setUpMapIfNeeded();
        initLocation();
    }

    private void setListener()
    {
        driveSselecter.setOnClickListener(this);
        walkSelecter.setOnClickListener(this);
        startNavi.setOnClickListener(this);
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        destroyLocation();
    }

    private void setUpMapIfNeeded()
    {
        if (mMap == null)
        {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            mRouteSearch = new RouteSearch(this);
            mRouteSearch.setRouteSearchListener(this);
            mMap = new AMapHelper(mMap)
                    .setMapType(AMap.MAP_TYPE_NORMAL)
                    .setZoomControlsEnabled(false)
                    .setMyLocationButtonEnabled(false)
                    .setRotateGesturesEnabled(false)
                    .setTiltGesturesEnabled(false)
                    .setTrafficEnabled(false)
                    .create();
        }
    }

    /**
     * 进行poi搜索
     *
     * @param lat
     * @param lon
     * @param address
     */
    private void initPoiSearch(double lat, double lon, String address)
    {
        mPoiSearch = null;
        if (mPoiOverlay != null)
        {
            mPoiOverlay.removeMarkerFromMap();
        }
        if (mPoiSearch == null)
        {
            PoiSearch.Query poiQuery = new PoiSearch.Query(address, "", CURRENT_CITY);
            poiQuery.setPageSize(1);
            mPoiSearch = new PoiSearch(this.getApplicationContext(), poiQuery);
            mPoiSearch.setOnPoiSearchListener(this);
            mPoiSearch.searchPOIAsyn();
            KeyBoardUtils.closeKeybord(editText, getApplicationContext());
        }
    }

    private void destroyLocation()
    {
        if (mLocationClient != null)
        {
            mLocationClient.unRegisterLocationListener(this);
            mLocationClient.onDestroy();
        }
    }

    /**
     * 初始化定位
     */
    private void initLocation()
    {
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        mLocationClient = new AMapLocationClient(this.getApplicationContext());
        mLocationClient.setLocationListener(this);
        mLocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(final AMapLocation aMapLocation)
    {
        if (aMapLocation == null || aMapLocation.getErrorCode() != AMapLocation.LOCATION_SUCCESS)
        {
            return;
        }
        mCurrentLocation = aMapLocation;
        curLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
        if (mLocationMarker == null)
        {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(curLatLng);
            markerOptions.anchor(0.5f, 0.5f);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.bubble_start));
            mLocationMarker = mMap.addMarker(markerOptions);
            CameraUpdateFactory.changeLatLng(curLatLng);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLatLng, 18));
        }
        search.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String address = editText.getText().toString();
                initPoiSearch(aMapLocation.getLatitude(), aMapLocation.getLongitude(), address);
            }
        });
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i)
    {
        if (i != AMapException.CODE_AMAP_SUCCESS || poiResult == null)
        {
            return;
        }
        if (mPoiOverlay != null)
        {
            mPoiOverlay.removeMarkerFromMap();
        }
        mPoiOverlay = new TestOverlay(mMap, poiResult.getPois());
        mPoiOverlay.addMarkerToMap();
        mPoiOverlay.moveMap();
        linearType.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i)
    {

    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i)
    {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode)
    {//自驾车路线规划回调
        mMap.clear();
        if (errorCode == AMapException.CODE_AMAP_SUCCESS)
        {
            if (result != null && result.getPaths() != null)
            {
                if (result.getPaths().size() > 0)
                {
                    mDriveRouteResult = result;
                    final DrivePath drivePath = mDriveRouteResult.getPaths().get(0);
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(mContext, mMap, drivePath,
                            mDriveRouteResult.getStartPos(), mDriveRouteResult.getTargetPos(), null);
                    drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                    drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();
                    bottomRootview.setVisibility(View.VISIBLE);
                    int dis = (int) drivePath.getDistance();
                    int dur = (int) drivePath.getDuration();
                    String des = "时间大约:" + AMapUtil.getFriendlyTime(dur) + "(里程:" + AMapUtil.getFriendlyLength(dis) + ")";
                    mRotueTimeDes.setText(des);

                } else if (result != null && result.getPaths() == null)
                {
                    Toast.makeText(getApplicationContext(), "对不起没有搜索到相关数据", Toast.LENGTH_SHORT).show();
                }
            } else
            {
                Toast.makeText(getApplicationContext(), "对不起没有搜索到相关数据", Toast.LENGTH_SHORT).show();
            }
        } else
        {
            Toast.makeText(getApplicationContext(), "错误类型:" + errorCode, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int errorCode)
    {//步行路线
        mMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS)
        {
            if (result != null && result.getPaths() != null)
            {
                if (result.getPaths().size() > 0)
                {
                    mWalkRouteResult = result;
                    final WalkPath walkPath = mWalkRouteResult.getPaths().get(0);
                    WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(
                            this, mMap, walkPath,
                            mWalkRouteResult.getStartPos(),
                            mWalkRouteResult.getTargetPos());
                    walkRouteOverlay.removeFromMap();
                    walkRouteOverlay.addToMap();
                    walkRouteOverlay.zoomToSpan();
                    bottomRootview.setVisibility(View.VISIBLE);
                    int dis = (int) walkPath.getDistance();
                    int dur = (int) walkPath.getDuration();
                    String des = "时间大约:" + AMapUtil.getFriendlyTime(dur) + "(里程:" + AMapUtil.getFriendlyLength(dis) + ")";
                    mRotueTimeDes.setText(des);
                } else if (result != null && result.getPaths() == null)
                {
                    Toast.makeText(getApplicationContext(), "对不起没有搜索到相关数据", Toast.LENGTH_SHORT).show();
                }
            } else
            {
                Toast.makeText(getApplicationContext(), "对不起没有搜索到相关数据", Toast.LENGTH_SHORT).show();
            }
        } else
        {
            Toast.makeText(getApplicationContext(), "错误类型:" + errorCode, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i)
    {//骑行路线

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.start_navi:
                Intent intent = new Intent(this, RouteNaviActivity.class);
                intent.putExtra("gps", false);//false表示模拟速度60的车速进行导航
                //intent.putExtra("gps", true);
                intent.putExtra("start", new NaviLatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
                LatLonPoint latLonPoint = mPoiOverlay.getItem(0).getLatLonPoint();
                intent.putExtra("end", new NaviLatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude()));
                startActivity(intent);
                break;
            case R.id.drive_selecter:
                if (driveSselecter.getVisibility() == View.VISIBLE)
                {
                    searchRouteResult(ROUTE_TYPE_DRIVE, RouteSearch.DrivingDefault);
                }
                break;
            case R.id.walk_selecter:
                if (walkSelecter.getVisibility() == View.VISIBLE)
                {
                    searchRouteResult(ROUTE_TYPE_WALK, RouteSearch.WalkDefault);
                }
                break;
            default:

                break;
        }
    }

    /**
     * 规划路线函数
     *
     * @param routeType
     * @param mode
     */
    private void searchRouteResult(int routeType, int mode)
    {
        if (curLatLng == null)
        {
            return;
        }
        if (mPoiOverlay == null)
        {
            return;
        }

        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(new LatLonPoint(curLatLng.latitude, curLatLng.longitude),
                mPoiOverlay.getItem(0).getLatLonPoint());
        if (routeType == ROUTE_TYPE_DRIVE)
        {
            driveSselecter.setTextColor(getResources().getColor(R.color.color_00c78d));
            // 驾车路径规划
            // 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mode, null, null, "");
            mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        }
        if (routeType == ROUTE_TYPE_WALK)
        {
            walkSelecter.setTextColor(getResources().getColor(R.color.color_00c78d));
            // 步行路径规划
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, mode);
            mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
        }
    }
}

