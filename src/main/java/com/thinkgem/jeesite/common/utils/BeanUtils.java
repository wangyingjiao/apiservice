package com.thinkgem.jeesite.common.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BeanUtils extends org.springframework.beans.BeanUtils {
    private static final String SETTER_PREFIX = "set";

    private static final String GETTER_PREFIX = "get";

    public static void cloneProperties(Object src ,Object dest){
        Method[] methods = src.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().indexOf(GETTER_PREFIX) != -1) {
                try {
                    Object val = method.invoke(src);
                    if(null != val){
                        dest.getClass()
                                .getMethod(method.getName()
                                        .replace(GETTER_PREFIX,SETTER_PREFIX),val.getClass())
                                .invoke(dest,val);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
