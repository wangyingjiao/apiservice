package com.thinkgem.jeesite.modules.oss;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.thinkgem.jeesite.common.utils.PropertiesLoader;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "oss")
public class OssApi {

    private Log logger = LogFactory.getLog(getClass());

    @ResponseBody
    @RequestMapping(value = "getSign",method = RequestMethod.GET)
    public Object getSign() {
        PropertiesLoader loader = new PropertiesLoader("oss.properties");
        String accessId = loader.getProperty("OSS_ACCESS_ID");
        String endpoint = loader.getProperty("OSS_ENDPOINT");
        String accessKey = loader.getProperty("OSS_ACCESS_KEY");
        String bucket = loader.getProperty("OSS_TEST_BUCKET");
        String host =  loader.getProperty("OSS_THUMB_HOST");

        String dir = "openservice";
        //String dir = bucket;
        String host1 = "https://" + bucket + "." + endpoint;
        OSSClient client = new OSSClient(endpoint, accessId, accessKey);

        long expireTime = 30;
        long expireEndTime = System.currentTimeMillis() + expireTime * 60 *1000;
        Date expiration = new Date(expireEndTime);
        PolicyConditions policyConds = new PolicyConditions();
        policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
        policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

        try {

            String postPolicy = client.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = client.calculatePostSignature(postPolicy);

            Map<String, String> respMap = new LinkedHashMap<String, String>();
            respMap.put("accessid", accessId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            //respMap.put("expire", formatISO8601Date(expiration));
            respMap.put("dir", dir);
            respMap.put("host", host);
            respMap.put("host1", host1);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));
            JSONObject ja1 = JSONObject.fromObject(respMap);
            return ja1;
        } catch (Exception e) {
            logger.error("异常：", e);
        }
        return null;
    }
}
