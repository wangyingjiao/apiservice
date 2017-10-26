package com.thinkgem.jeesite.common.filter;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author x
 */
public class DefaultHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private String body;
    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request
     * @throws IllegalArgumentException if the request is null
     */
    public DefaultHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);

        StringBuffer jsonStr = new StringBuffer();
        try (BufferedReader bufferedReader = request.getReader())
        {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                jsonStr.append(line);
            }
        }catch (Exception ex){

        }
        //获取到提交测json，将密码解密后重新复制给requestBody
        JSONObject json = JSONObject.parseObject(jsonStr.toString());
        body = json.toJSONString();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException
    {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
        return new ServletInputStream()
        {
            @Override
            public int read() throws IOException
            {
                return byteArrayInputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException
    {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

}
