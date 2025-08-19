package tornaco.apps.shortx.core.util

import android.content.Context
import android.content.Intent
import android.net.Uri

// https://github.com/MZCretin/ExternalMapUtils/blob/master/externalmaputilslibrary/src/main/java/com/cretin/www/externalmaputilslibrary/OpenExternalMapAppUtils.java
object MapUtils {
    //************************************************************************
    //*************************    百度专区     *******************************
    //************************************************************************
    /**
     * 调起百度客户端 自定义打点
     * lat,lng (先纬度，后经度)
     * 40.057406655722,116.2964407172
     */
    fun openBaiduMarkerMap(
        context: Context, longitude: String, latitude: String,
        title: String, content: String
    ) {
        val intent = Intent(
            "android.intent.action.VIEW",
            Uri.parse(
                "baidumap://map/marker?location=" + latitude + "," +
                        longitude + "&title=" + title + "&content=" + content + "&traffic=on"
            )
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /**
     * 调起百度客户端 展示地图
     * lat,lng (先纬度，后经度)
     * 40.057406655722,116.2964407172
     * 范围参数
     * lat,lng,lat,lng (先纬度，后经度, 先左下,后右上)
     *
     */
    fun openBaiduCenterMap(
        context: Context,
        cLongitude: String,
        cLatitude: String,
        zoom: String,
        traffic: Boolean,
        lLatitude: String,
        lLongitude: String,
        rLatitude: String,
        rLongitude: String
    ) {
        val intent = Intent(
            "android.intent.action.VIEW",
            Uri.parse(
                ("baidumap://map/show?center=" + cLatitude + "," +
                        cLongitude + "&zoom=" + zoom + "&traffic=" + (if (traffic) "on" else "off") +
                        "&bounds=" + lLatitude + "," + lLongitude + "," + rLatitude + "," + rLongitude)
            )
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /**
     * 调起百度客户端 驾车导航
     * lat,lng (先纬度，后经度)
     * 40.057406655722,116.2964407172
     *
     */
    fun openBaiduNaviMap(context: Context, longitude: String, latitude: String) {
        val intent = Intent(
            "android.intent.action.VIEW",
            Uri.parse("baidumap://map/navi?location=$latitude,$longitude")
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /**
     * 调起百度客户端 路径规划
     * lat,lng (先纬度，后经度)
     * 40.057406655722,116.2964407172
     * lat,lng,lat,lng (先纬度，后经度, 先左下,后右上)
     *
     */
    fun openBaiduiDrectionMap(
        context: Context, sLongitude: String, sLatitude: String, sName: String,
        dLongitude: String, dLatitude: String, dName: String
    ) {
        val intent = Intent(
            "android.intent.action.VIEW",
            Uri.parse(
                ("baidumap://map/direction?origin=name:" +
                        sName + "|latlng:" + sLatitude + "," + sLongitude + "&destination=name:" +
                        dName + "|latlng:" + dLatitude + "," + dLongitude + "&" +
                        "mode=transit&sy=0&index=0&target=0")
            )
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }


    //************************************************************************
    //*************************    高德专区     *******************************
    //************************************************************************
    /**
     * 调起高德客户端 展示标注点
     * lat,lng (先纬度，后经度)
     * 40.057406655722,116.2964407172
     * 根据名称或经纬度，启动高德地图产品展示一个标注点，如分享位置，标注店铺。支持版本V4.1.3起。
     *
     */
    fun openGaodeMarkerMap(
        context: Context,
        longitude: String,
        latitude: String,
        appName: String, poiname: String
    ) {
        val intent = Intent(
            "android.intent.action.VIEW",
            Uri.parse(
                ("androidamap://viewMap?sourceApplication=" +
                        appName + "&poiname=" + poiname + "&lat=" + latitude + "&lon=" + longitude + "&dev=1")
            )
        )
        intent.setPackage("com.autonavi.minimap")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /**
     * 调起高德客户端 路径规划
     * lat,lng (先纬度，后经度)
     * 40.057406655722,116.2964407172
     * 输入起点和终点，搜索公交、驾车或步行的线路。支持版本 V4.2.1 起。
     */
    fun openGaodeRouteMap(
        context: Context, sLongitude: String, sLatitude: String, sName: String,
        dLongitude: String, dLatitude: String, dName: String, appName: String
    ) {
        val intent = Intent(
            "android.intent.action.VIEW",
            Uri.parse(
                ("amapuri://route/plan/?sourceApplication=" + appName +
                        "&sid=&slat=" + sLatitude + "&slon=" +
                        sLongitude + "&sname=" + sName + "&did=&dlat=" +
                        dLatitude + "&dlon=" + dLongitude + "&dname=" + dName + "&dev=1&t=1")
            )
        )
        intent.setPackage("com.autonavi.minimap")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /**
     * 调起高德客户端 我的位置
     * 显示我当前的位置。支持版本V4.2.1 起。
     *
     */
    fun openGaodeMyLocationMap(context: Context, appName: String) {
        val intent = Intent(
            "android.intent.action.VIEW",
            Uri.parse("androidamap://myLocation?sourceApplication=$appName")
        )
        intent.setPackage("com.autonavi.minimap")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /**
     * 调起高德客户端 导航
     * 输入终点，以用户当前位置为起点开始路线导航，提示用户每段行驶路线以到达目的地。支持版本V4.1.3 起。
     * lat,lng (先纬度，后经度)
     * 40.057406655722,116.2964407172
     *
     */
    fun openGaodeNaviMap(
        context: Context,
        appName: String,
        poiName: String,
        latitude: String,
        longitude: String
    ) {
        val intent = Intent(
            "android.intent.action.VIEW",
            Uri.parse(
                ("androidamap://navi?sourceApplication=" + appName + "&poiname=" +
                        poiName + "&lat=" + latitude + "&lon=" + longitude + "&dev=1&style=2")
            )
        )
        intent.setPackage("com.autonavi.minimap")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    // cat=android.intent.action.VIEW
    //dat=amapuri://openFeature?featureName=OnRideNavi&rideType=elebike&sourceApplication=appname&lat=36.547901&lon=104.258354&dev=0
    fun openGaodeRideNaviMap(
        context: Context,
        appName: String,
        latitude: String,
        longitude: String
    ) {
        val intent = Intent(
            "android.intent.action.VIEW",
            Uri.parse(
                ("androidamap://openFeature?featureName=OnRideNavi&sourceApplication=$appName&rideType=elebike&lat=$latitude&lon=$longitude&dev=1")
            )
        )
        intent.setPackage("com.autonavi.minimap")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /**
     * 调起高德客户端 公交线路查询
     * 输入公交线路名称，如 445，搜索该条线路经过的所有公交站点。支持版本 v4.2.1 起。
     *
     */
    fun openGaodeBusMap(
        context: Context,
        appName: String,
        busName: String,
        city: String
    ) {
        val intent = Intent(
            "android.intent.action.VIEW",
            Uri.parse("androidamap://bus?sourceApplication=$appName&busname=$busName&city=$city")
        )
        intent.setPackage("com.autonavi.minimap")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /**
     * 调起高德客户端 地图主图
     * 进入高德地图主图页面。支持版本V4.2.1起。
     *
     */
    fun openGaodeRootmapMap(context: Context, appName: String) {
        val intent = Intent(
            "android.intent.action.VIEW",
            Uri.parse("androidamap://rootmap?sourceApplication=$appName")
        )
        intent.setPackage("com.autonavi.minimap")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}