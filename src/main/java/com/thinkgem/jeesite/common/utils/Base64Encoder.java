package com.thinkgem.jeesite.common.utils;


import java.io.*;

public class Base64Encoder extends FilterOutputStream {

    private static final char[] chars = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
            'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
            'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', '+', '/'
    };

    private int charCount;
    private int carryOver;

    /**
     * Constructs a new Base64 encoder that writes output to the given
     * OutputStream.
     *
     * @param out the output stream
     */
    public Base64Encoder(OutputStream out) {
        super(out);
    }

    /**
     * Writes the given byte to the output stream in an encoded form.
     *
     * @throws IOException if an I/O error occurs
     */
    public void write(int b) throws IOException {
        // Take 24-bits from three octets, translate into four encoded chars
        // Break lines at 76 chars
        // If necessary, pad with 0 bits on the right at the end
        // Use = signs as padding at the end to ensure encodedLength % 4 == 0

        // Remove the sign bit,
        // thanks to Christian Schweingruber <chrigu@lorraine.ch>
        if (b < 0) {
            b += 256;
        }

        // First byte use first six bits, save last two bits
        if (charCount % 3 == 0) {
            int lookup = b >> 2;
            carryOver = b & 3;        // last two bits
            out.write(chars[lookup]);
        }
        // Second byte use previous two bits and first four new bits,
        // save last four bits
        else if (charCount % 3 == 1) {
            int lookup = ((carryOver << 4) + (b >> 4)) & 63;
            carryOver = b & 15;       // last four bits
            out.write(chars[lookup]);
        }
        // Third byte use previous four bits and first two new bits,
        // then use last six new bits
        else if (charCount % 3 == 2) {
            int lookup = ((carryOver << 2) + (b >> 6)) & 63;
            out.write(chars[lookup]);
            lookup = b & 63;          // last six bits
            out.write(chars[lookup]);
            carryOver = 0;
        }
        charCount++;

        // Add newline every 76 output chars (that's 57 input chars)
        if (charCount % 57 == 0) {
            out.write('\n');
        }
    }

    /**
     * Writes the given byte array to the output stream in an
     * encoded form.
     *
     * @param buf the data to be written
     * @param off the start offset of the data
     * @param len the length of the data
     * @throws IOException if an I/O error occurs
     */
    public void write(byte[] buf, int off, int len) throws IOException {
        // This could of course be optimized
        for (int i = 0; i < len; i++) {
            write(buf[off + i]);
        }
    }

    /**
     * Closes the stream, this MUST be called to ensure proper padding is
     * written to the end of the output stream.
     *
     * @throws IOException if an I/O error occurs
     */
    public void close() throws IOException {
        // Handle leftover bytes
        if (charCount % 3 == 1) {  // one leftover
            int lookup = (carryOver << 4) & 63;
            out.write(chars[lookup]);
            out.write('=');
            out.write('=');
        } else if (charCount % 3 == 2) {  // two leftovers
            int lookup = (carryOver << 2) & 63;
            out.write(chars[lookup]);
            out.write('=');
        }
        super.close();
    }

    /**
     * Returns the encoded form of the given unencoded string.  The encoder
     * uses the ISO-8859-1 (Latin-1) encoding to convert the string to bytes.
     * For greater control over the encoding, encode the string to bytes
     * yourself and use encode(byte[]).
     *
     * @param unencoded the string to encode
     * @return the encoded form of the unencoded string
     */
    public static String encode(String unencoded) {
        byte[] bytes = null;
        try {
            bytes = unencoded.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ignored) {
        }
        return encode(bytes);
    }

    /**
     * Returns the encoded form of the given unencoded string.
     *
     * @param bytes the bytes to encode
     * @return the encoded form of the unencoded string
     */
    public static String encode(byte[] bytes) {
        ByteArrayOutputStream out = new ByteArrayOutputStream((int) (bytes.length * 1.37));
        Base64Encoder encodedOut = new Base64Encoder(out);

        try {
            encodedOut.write(bytes);
            encodedOut.close();

            return out.toString("UTF-8").replace("\r", "").replace("\n", "");
        } catch (IOException ignored) {
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        //选择服务时间
        String time =
                "{\"store_id\":\"00000000000000000000000000000034\"," +
                "\"eshop_code\":\"E172493508099668134\"," +
                "\"latitude\":\"114.0909\"," +
                "\"longitude\":\"42.0808\"," +
                "\"service_info\": [{" +
                    "\"cate_goods_id\":\"5bb6d03fd80b4df0b558083b9a927fef_00d0a4c1a5d44fcba5832a5aecc0fa75\"," +
                    "\"gasq_product_id\":\"bda23cc2b60a1fe9ae19a67af537cc6e\"," +
                    "\"pay_price\":\"200\"," +
                    "\"buy_num\":\"3\"" +
                "}]," +
                "\"platform\":\"gasq\"" +
                "}";
        // 订单创建
        String order=
                "{\"store_id\":\"00000000000000000000000000000034\"," +
                        "\"eshop_code\":\"E172493508099668134\"," +

                        "\"phone\":\"13520174926\"," +
                        "\"remark\":\"\"," +
                        "\"gasq_order_id\":\"20180130151157572109\"," +

                        "\"province_code\":\"110000\"," +
                        "\"city_code\":\"010\"," +
                        "\"area_code\":\"110101\"," +
                        "\"address\":\"北京市北京市朝阳区关东店北街11号楼热的天\"," +

                        "\"service_time\":\"2018-01-31 11:00:00\"," +
                        "\"latitude\":\"116.461\"," +
                        "\"longitude\":\"39.9221\"," +

                        "\"sum_price\":\"595\"," +
                        "\"order_type\":\"common\"," +

                        "\"service_info\": [{" +
                        "\"cate_goods_id\":\"5bb6d03fd80b4df0b558083b9a927fef_00d0a4c1a5d44fcba5832a5aecc0fa75\"," +
                        "\"pay_price\":\"200\"," +
                        "\"buy_num\":\"3\"" +
                        "}]," +
                        "\"platform\":\"gasq\"" +
                        "}";

        //订单状态更新
        String flag =
                "{\"platform\":\"gasq\"," +
                        "\"service_order_id\":\"100\"," +
                        "\"comment\":\"\"," +
                        "\"status\":\"finish\"," + //cancel 取消；finish 已签收；success 完成
                        "\"gasq_order_id\":\"gasq001\"" +

                        "}";
        //订单状态更新   gasq_order_sn  gasq_order_id"
        String flag1 =
                "{\"platform\":\"gasq\"," +
                        "\"service_order_id\":\"201806011424010172956934\"," +
                        "\"comment\":\"\"," +
                        "\"status\":\"signed\"," + //cancel 取消；finish 已签收；success 完成
                        "\"gasq_order_sn\":\"20180601142251246060\""+
                        "}";

        //更新订单商品信息及门店、商家端、国安侠、用户备注
        String goods =
                "{\"platform\":\"gasq\"," +
                        "\"service_order_id\":\"666601\"," +

                        "\"service_info\": [{" +
                        "\"cate_goods_id\":\"5a9bd93981f146e3b0755f068e60b630\"," +
                        "\"pay_price\":\"100\"," +
                        "\"buy_num\":\"10\"" +
                        "}]," +

                        "\"guoanxia_info\": {" +
                            "\"name\":\"国安侠\"," +
                            "\"phone\":\"13600000001\"," +
                            "\"remark\":\"国安侠备注\"," +
                            "\"remark_pic\":[]" +
                        "}," +

                        "\"costomer_info\": {" +
                            "\"remark\":\"用户备注\"," +
                            "\"remark_pic\":[]" +
                        "}," +

                        "\"store_info\": {" +
                            "\"name\":\"门店\"," +
                            "\"telephone\":\"13800000001\"," +
                            "\"remark\":\"门店备注\"," +
                            "\"remark_pic\":[\"src/pic/a\",\"src/pic/b\"]" +
                        "}" +

                        "}";
        String goods2 =
                "{\"platform\":\"gasq\"," +
                        "\"service_order_id\":\"666601\"," +


                        "\"guoanxia_info\": {" +
                        "\"name\":\"国安侠\"," +
                        "\"phone\":\"136000000011360000000113600000001\"," +
                        "\"remark\":\"国安侠备注\"," +
                        "\"remark_pic\":[]" +
                        "}," +

                        "\"store_info\": {" +
                        "\"name\":\"门店\"," +
                        "\"telephone\":\"13800000001\"," +
                        "\"remark\":\"门店备注\"," +
                        "\"remark_pic\":[\"src/pic/a\",\"src/pic/b\"]" +
                        "}" +

                        "}";


//String bb = "{\"store_id\":\"00000000000000000000000000000034\",\"service_time\":\"2018-02-05 14:30:00\",\"remark_pic\":[],\"address\":\"北京市北京市朝阳区向军南里二巷赤果果春天\",\"area_code\":\"110105\",\"latitude\":\"39.921715\",\"city_code\":\"010\",\"service_info\":[{\"buy_num\":3,\"pay_price\":234.00,\"cate_goods_id\":\"1fe8abda97bc4ea98f26308b968cb0bf_2f7b589fb373408bbf071093f69dd2bd\"}],\"remark\":\"咯嘿嘿买哦安破坏和各地八婆去年破 boss 毛孔呢，pop，破烂/这，出差。x/打个车咯胖龙敏LOL你咯哦破 ins logo 病了，破，破，破烂，哈来陪姥姥家去看看去啦啦啦啦啦啦啦啦啦啪啦啦啦啦啦啦离开了斯里兰卡时间恐怕来来回回。爸爸啊。在一起就好了！你是怎么回事！我在外面玩手\",\"province_code\":\"\",\"platform\":\"gasq\",\"phone\":\"13520174925\",\"gasq_order_id\":\"20180205093554104578\",\"eshop_code\":\"E180314975778109598\",\"order_type\":\"common\",\"longitude\":\"116.46048\",\"sum_price\":\"702.00\"}";
String bb = "{\"store_id\":\"00000000000000000000000000000034\",\"service_time\":\"2018-02-05 12:30:00\",\"remark_pic\":[\"ios/2018/02/05/DA5FF87B-AD0E-4A01-A319-8A6DF0EBC5DC.jpg\",\"ios/2018/02/05/70E39FF3-1884-4F84-9EEF-35F877D0D417.jpg\",\"ios/2018/02/05/56E7914A-1085-471C-A0F2-4CA9DABF3015.jpg\",\"ios/2018/02/05/97C4E912-1133-4DE5-B78C-F1A9FCDCAA16.jpg\",\"ios/2018/02/05/055A34C5-002E-4E86-9E84-FDB8B3E59B8A.jpg\",\"ios/2018/02/05/E239E0BB-6054-445F-9EA2-9A0391806DAC.jpg\",\"ios/2018/02/05/FA2A9ADF-32D5-4307-8216-8968DA0A71DF.jpg\",\"ios/2018/02/05/4358CA6D-7937-49B1-98B1-7A3DCE7B2A4D.jpg\",\"ios/2018/02/05/FE206109-2A1A-430E-9F3F-B412A381C61C.jpg\"],\"address\":\"北京市北京市朝阳区向军南里二巷赤果果春天\",\"area_code\":\"110105\",\"latitude\":\"39.921715\",\"city_code\":\"010\",\"service_info\":[{\"buy_num\":1,\"pay_price\":234.00,\"cate_goods_id\":\"1fe8abda97bc4ea98f26308b968cb0bf_2f7b589fb373408bbf071093f69dd2bd\"}],\"remark\":\"\",\"province_code\":\"\",\"platform\":\"gasq\",\"phone\":\"13520174925\",\"gasq_order_id\":\"20180205102020594221\",\"eshop_code\":\"E180314975778109598\",\"order_type\":\"common\",\"longitude\":\"116.46048\",\"sum_price\":\"234.00\"}";

String aa = "{\"store_id\":\"8ac2d18658faf9650158fc95be90001e\",\"eshop_code\":\"E180924095431792460\",\"latitude\":\"39.930767\",\"longitude\":\"116.675674\",\"service_info\":[{\"cate_goods_id\":\"1_d48aa2d1ca0f4f56a346e0a564558b6a\",\"gasq_product_id\":\"1f41e9811c5b29972f1f43503cab3551\",\"buy_num\":\"1\"}],\"platform\":\"gasq\"}";

String cc = "{\"area_name\":\"朝阳区\",\"latitude\":\"39.9221\",\"service_info\":[{\"buy_num\":1,\"pay_price\":23.00,\"cate_goods_id\":\"452f5b2db7e14b3f9765cd332a335bf1_5d814ab700da48948394f9916c4b7d85\"}],\"city_code\":\"010\",\"remark\":\"\",\"province_code\":\"110000\",\"placename\":\"关东店北街11号楼\",\"login_mobile\":\"13520174926\",\"platform\":\"gasq\",\"store_phone\":\"010-33636634\",\"city_name\":\"北京市\",\"store_name\":\"呼家楼门店\",\"eshop_code\":\"E180924095431792460\",\"order_type\":\"common\",\"longitude\":\"116.461\",\"store_id\":\"00000000000000000000000000000034\",\"service_time\":\"2018-05-22 09:00:00\",\"remark_pic\":[],\"store_addr\":\"北京市朝阳区呼家楼宾馆写字楼\",\"detail_address\":\"恶徒夺目\",\"area_code\":\"110105\",\"gasq_order_status\":\"waitConfirm\",\"province_name\":\"北京市\",\"opt\":\"createOrder\",\"login_name\":\"哈喽去\",\"phone\":\"13520174926\",\"group_id\":\"577b055ad5ba4a34bea5ef99693c5811\",\"gasq_order_id\":\"58c0d50c5ac441a6aa0f2e592a4ba58e\",\"gasq_order_sn\":[\"20180521193137348425\"],\"name\":\"据他\",\"sum_price\":\"23.00\"}";

String aaaa ="{\"opt\":\"cancelAppoint\",\"group_id\":\"92d650efdfe94193b8ef6f9708f7b9e4\",\"gasq_order_sn\":\"20180601145140964542\",\"platform\":\"gasq\"}";

String encode = Base64Encoder.encode(aaaa);
        System.out.println(encode);
        System.out.println(MD5Util.getStringMD5(encode+"7e1c77ac-29c4-40f3-ad2f-1027dc75713c"));
    }
}
