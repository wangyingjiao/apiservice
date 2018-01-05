package com.thinkgem.jeesite.common.utils.map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.utils.PropertiesLoader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * 高德地图
 *
 */
public class GaoDeMapUtil {

    // 骑行路径规划
    //个人开发者 日配额 2000（次/日）50 QPS（次/秒）
    //企业开发者 日配额 300000（次/日）200 QPS（次/秒）
    private static String map_codeurl_bicycling = "http://restapi.amap.com/v4/direction/bicycling?parameters";

    /**
     * 骑行路径规划
     * @param origin 出发点经纬度 填入规则：X,Y，采用","分隔，例如“ 117.500244, 40.417801 ” 小数点后不得超过6位
     * @param destination 目的地经纬度 填入规则：X,Y，采用","分隔，例如“ 117.500244, 40.417801 ” 小数点后不得超过6位
     * @return 起终点的骑行时间  单位：秒
     */
    public static String directionBicycling(String origin,String destination) {
        try {
            PropertiesLoader loader = new PropertiesLoader("jeesite.properties");
            String key = loader.getProperty("gaoDeMapKey");//读取配置文件 获得高德地图Key

            String duration=null;//起终点的骑行时间
            String url = map_codeurl_bicycling.replace("parameters", "");
            String params = "key=" + key + "&origin=" + origin+"&destination="+destination;

            URL myURL = null;
            URLConnection httpsConn = null;
            try {
                myURL = new URL(url+params);
            } catch (Exception e) {
                e.printStackTrace();
            }
            InputStreamReader insr = null;
            BufferedReader br = null;
            httpsConn = (URLConnection) myURL.openConnection();// 不使用代理
            if (httpsConn != null) {
                insr = new InputStreamReader(httpsConn.getInputStream(), "UTF-8");
                br = new BufferedReader(insr);
                String data = "";
                String line = null;
                while ((line = br.readLine()) != null) {
                    data += line;
                }
                //得到API返回数据
                JSONObject jsonObject = JSONObject.parseObject(data);
                // 解析json
                String errcode = jsonObject.getString("errcode");//返回结果码 0，表示成功
                String errdetail = jsonObject.getString("errdetail");//具体错误原因 此字段会详细说明错误原因
                String errmsg = jsonObject.getString("errmsg");//返回状态说明 OK代表成功
                JSONObject dataJson = jsonObject.getJSONObject("data");//里面包含具体内容 业务数据字段
                // 遍历解析出来的结果
                if ((dataJson != null) && (dataJson.size() > 0)) {
                    JSONArray paths = dataJson.getJSONArray("paths");//骑行方案列表信息
                    if (paths != null && paths.size() > 0) {
                        JSONObject path = (JSONObject) paths.get(0);
                        String distance = path.getString("distance");//起终点的骑行距离 单位：米
                        duration = path.getString("duration");//起终点的骑行时间 单位：秒
                        return duration;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

}

