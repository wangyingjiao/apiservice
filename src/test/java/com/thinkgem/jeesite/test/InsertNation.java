package com.thinkgem.jeesite.test;

import com.thinkgem.jeesite.common.utils.IdGen;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertNation {
    public static void main(String[] args) throws SQLException {
        Connection conn = getConn();
        String n = "01：汉族，02：蒙古族，03：回族，04：藏族，05：维吾尔族，06：苗族，07：彝族，08：壮族，09：布依族，10：朝鲜族，11：满族，12：侗族，13：瑶族，14：白族，15：土家族，16：哈尼族，17：哈萨克族，18：傣族，19：黎族，20：僳僳族，21：佤族，22：畲族，23：高山族，24：拉祜族，25：水族，26：东乡族，27：纳西族，28：景颇族，29：柯尔克孜族，30：土族，31：达斡尔族，32：仫佬族，33：羌族，34：布朗族，35：撒拉族，36：毛难族，37：仡佬族，38：锡伯族，39：阿昌族，40：普米族，41：塔吉克族，42：怒族，43：乌孜别克族，44：俄罗斯族，45：鄂温克族，46：崩龙族，47：保安族，48：裕固族，49：京族，50：塔塔尔族，51：独龙族，52：鄂伦春族，53：赫哲族，54：门巴族，55：珞巴族，56：基诺族";
        String sql = "INSERT INTO `api_service`.`sys_dict`(`id`, `value`, `label`, `type`, `description`, `sort`, `parent_id`, `create_by`, `create_date`, `update_by`, `update_date`, `remarks`, `del_flag`)" +
                " VALUES (?, ?, ?, 'ethnic', '民族', 10, '0', '1', '2017-11-22 10:18:09', '1', '2017-11-22 10:18:09', '', '0')";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        String[] split = n.split("，");
        for (String s : split) {
            String[] ss = s.split("：");
            preparedStatement.setString(1,IdGen.uuid());
            preparedStatement.setString(2,ss[0]);
            preparedStatement.setString(3,ss[1]);
            preparedStatement.execute();
        }
    }
    public static Connection getConn(){
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://data.ciss.xin:3306/api_service?useUnicode=true&characterEncoding=utf-8";
        String username = "api-service";
        String password = "LXO32Mh95^lN";
        Connection conn = null;
        try {
            Class.forName(driver); //classLoader,加载对应驱动     
            conn =  DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
