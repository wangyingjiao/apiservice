package com.thinkgem.jeesite.generate.code;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.DbType;

public class GlblConfig {

    public static void DataSourceConfig(AutoGenerator mpg) {
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.MYSQL);
        dsc.setTypeConvert(new MySqlTypeConvert() {
            // 自定义数据库表字段类型转换【可选】
            @Override
            public DbColumnType processTypeConvert(String fieldType) {
                System.out.println("转换类型：" + fieldType);
                // 注意！！processTypeConvert 存在默认类型转换，如果不是你要的效果请自定义返回、非如下直接返回。
                return super.processTypeConvert(fieldType);
            }
        });
        dsc.setDriverName("com.mysql.jdbc.Driver");
        dsc.setUsername("api-service");
        dsc.setPassword("LXO32Mh95^lN");
        dsc.setUrl("jdbc:mysql://data.ciss.xin:3306/apiservice?useUnicode=true&characterEncoding=utf-8");
        mpg.setDataSource(dsc);
    }

    public static void GlobalConfig(AutoGenerator mpg) {
        GlobalConfig gc = new GlobalConfig();
        gc.setOutputDir("/Users/x/Projects/backService/src/main/java");
        gc.setFileOverride(true);
        gc.setActiveRecord(true);// 不需要ActiveRecord特性的请改为false
        gc.setEnableCache(false);// XML 二级缓存
        gc.setBaseResultMap(true);// XML ResultMap
        gc.setBaseColumnList(false);// XML columList
        //.setKotlin(true) 是否生成 kotlin 代码
        gc.setAuthor("X");


        // 自定义文件命名，注意 %s 会自动填充表实体属性！
        gc.setMapperName("%sDao");
        gc.setXmlName("%sDao");
        gc.setServiceName("%sService");
        gc.setServiceImplName("%sServiceImpl");
        gc.setControllerName("%sController");
        mpg.setGlobalConfig(gc);
    }

    public static void StrategyConfig(StrategyConfig strategy) {
        strategy.setLogicDeleteFieldName("del_flag");
        // strategy.setExclude(new String[]{"test"}); // 排除生成的表
        // 自定义实体父类
        strategy.setSuperEntityClass("com.thinkgem.jeesite.modules.BaseEntity");
        // 自定义实体，公共字段
        strategy.setSuperEntityColumns(new String[] { "id", "create_by","create_date","update_by","update_date","del_flag" });
        // 自定义 controller 父类
        strategy.setSuperControllerClass("com.thinkgem.jeesite.common.web.BaseController");

    }
}
