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


String cc = "{\"area_name\":\"朝阳区\",\"latitude\":\"39.9221\",\"service_info\":[{\"buy_num\":1,\"pay_price\":23.00,\"cate_goods_id\":\"452f5b2db7e14b3f9765cd332a335bf1_33a4397da6a6426bb1d6a997e3042875\"}],\"city_code\":\"010\",\"remark\":\"\",\"province_code\":\"110000\",\"placename\":\"关东店北街11号楼\",\"login_mobile\":\"13520174926\",\"platform\":\"gasq\",\"store_phone\":\"010-33636634\",\"city_name\":\"北京市\",\"store_name\":\"烧饼门店\",\"eshop_code\":\"E161943692063860498\",\"order_type\":\"group_split_yes\",\"longitude\":\"116.461\",\"store_id\":\"12345678901234567890\",\"service_time\":\"2018-05-22 09:00:00\",\"remark_pic\":[],\"store_addr\":\"北京市朝阳区呼家楼宾馆写字楼\",\"detail_address\":\"恶徒夺目\",\"area_code\":\"110105\",\"gasq_order_status\":\"waitConfirm\",\"province_name\":\"北京市\",\"opt\":\"createOrder\",\"login_name\":\"哈喽去\",\"phone\":\"13520174926\",\"group_id\":\"577b055ad5ba4a34bea5ef9999999999\",\"gasq_order_id\":\"58c0d50c5ac441a6aa0f2e592a4ba58e\",\"gasq_order_sn\":[\"20170521193137348111\",\"20170521193137348222\",\"20170521193137348333\",\"20170521193137348444\",\"20170521193137348555\"],\"name\":\"据他\",\"sum_price\":\"23.00\"}";

String qh ="{\n" +
        "\t\"area_name\": \"朝阳区\",\n" +
        "\t\"latitude\": \"59.9221\",\n" +
        "\t\"service_info\": [{\n" +
        "\t\t\"buy_num\": 1,\n" +
        "\t\t\"pay_price\": 100.00,\n" +
        "\t\t\"cate_goods_id\": \"0abf778fc2e64db785df4f64f3e6d888_b338c4acbff441439e79a008e0771cc3\"\n" +
        "\t}],\n" +
        "\t\"city_code\": \"010\",\n" +
        "\t\"remark\": \"\",\n" +
        "\t\"province_code\": \"110000\",\n" +
        
        "\t\"platform\": \"gasq\",\n" +
        "\t\"store_phone\": \"010-33636634\",\n" +
        "\t\"city_name\": \"北京市\",\n" +
        "\t\"store_name\": \"烧饼门店\",\n" +
        "\t\"eshop_code\": \"E180924095431792460\",\n" +
        "\t\"order_type\": \"group_split_yes\",\n" +
        "\t\"longitude\": \"156.461\",\n" +
        "\t\"store_id\": \"00000000000000000000000000000034\",\n" +
        "\t\"service_time\": \"2018-05-22 09:00:00\",\n" +
        "\t\"remark_pic\": [],\n" +
        "\t\"store_addr\": \"北京市朝阳区呼家楼宾馆写字楼\",\n" +
        
        "\t\"area_code\": \"110105\",\n" +
        "\t\"gasq_order_status\": \"waitConfirm\",\n" +
        "\t\"province_name\": \"北京市\",\n" +
        "\t\"opt\": \"createOrder\",\n" +
        "\t\"placename\": \"关东店北街11号楼\",\n" +
        "\t\"detail_address\": \"详细地址\",\n" +


        "\t\"login_name\": \"用户2051\",\n" +
        "\t\"login_mobile\": \"13520172051\",\n" +
        "\t\"phone\": \"13520172051\",\n" +
        "\t\"name\": \"用户2051\",\n" +


        "\t\"gasq_order_id\": \"58c0d50c5ac441a6aa0f2e592a4ba58e\",\n" +

        "\t\"group_id\": \"577b055ad5ba4a34bea5ef996932051\",\n" +
        "\t\"gasq_order_sn\": [\"20510521193137348001\", " +
        "\"20510521193137348002\", " +
        "\"20510521193137348003\", " +
        "\"20510521193137348004\", " +
        "\"20510521193137348005\", " +
        "\"20510521193137348006\", " +
        "\"20510521193137348007\", " +
        "\"20510521193137348008\", " +
        "\"20510521193137348009\", " +
        "\"20510521193137348010\", " +
        "\"20510521193137348011\", " +
        "\"20510521193137348012\", " +
        "\"20510521193137348013\", " +
        "\"20510521193137348014\", " +
        "\"20510521193137348015\", " +
        "\"20510521193137348016\", " +
        "\"20510521193137348017\", " +
        "\"20510521193137348018\", " +
        "\"20510521193137348019\", " +
        "\"20510521193137348020\"],\n" +


        "\t\"sum_price\": \"23.00\"\n" +
        "}";

//String encode = Base64Encoder.encode(dd);

        String time2 =
                "{\"store_id\":\"00000000000000000000000000000034\"," +
                        "\"eshop_code\":\"E180924095431792460\"," +
                        "\"group_id\":\"577b055ad5ba4a34bea5ef996932028\"," +
                        "\"gasq_order_num\":\"1\"," +
                        "\"service_info\": [{" +
                        "\"cate_goods_id\":\"3ccfcd9e8c5a4ce3aad0d0a434d020b0_1fad4ec32c8640248d038d94756e6c29\"," +
                        "\"gasq_product_id\":\"bda23cc2b60a1fe9ae19a67af537cc6e\"," +
                        "\"pay_price\":\"200\"," +
                        "\"buy_num\":\"3\"" +
                        "}]," +
                        "\"platform\":\"gasq\"" +
                        "}";
        String ee = "{\n" +
                "\t\"area_name\": \"朝阳区\",\n" +
                "\t\"latitude\": \"59.9221\",\n" +
                "\t\"service_info\": [{\n" +
                "\t\t\"buy_num\": 1,\n" +
                "\t\t\"pay_price\": 23.00,\n" +
                "\t\t\"cate_goods_id\": \"452f5b2db7e14b3f9765cd332a335bf1_33a4397da6a6426bb1d6a997e3042875\"\n" +
                "\t}],\n" +
                "\t\"city_code\": \"010\",\n" +
                "\t\"remark\": \"\",\n" +
                "\t\"province_code\": \"110000\",\n" +
                "\t\"placename\": \"关东店北街11号楼\",\n" +
                "\t\"login_mobile\": \"13520171111\",\n" +
                "\t\"platform\": \"gasq\",\n" +
                "\t\"store_phone\": \"010-33636634\",\n" +
                "\t\"city_name\": \"北京市\",\n" +
                "\t\"store_name\": \"烧饼门店\",\n" +
                "\t\"eshop_code\": \"E161943692063860498\",\n" +
                "\t\"order_type\": \"group_split_yes\",\n" +
                "\t\"longitude\": \"156.461\",\n" +
                "\t\"store_id\": \"12345678901234567890\",\n" +
                "\t\"service_time\": \"2018-05-25 11:00:00\",\n" +
                "\t\"remark_pic\": [],\n" +
                "\t\"store_addr\": \"北京市朝阳区呼家楼宾馆写字楼\",\n" +
                "\t\"detail_address\": \"详细地址\",\n" +
                "\t\"area_code\": \"110105\",\n" +
                "\t\"gasq_order_status\": \"waitConfirm\",\n" +
                "\t\"province_name\": \"北京市\",\n" +
                "\t\"opt\": \"createOrder\",\n" +
                "\t\"login_name\": \"用户AAA\",\n" +
                "\t\"phone\": \"13520171111\",\n" +
                "\t\"group_id\": \"577b055ad5ba4a34bea5ef99693c8888\",\n" +
                "\t\"gasq_order_id\": \"58c0d50c5ac441a6aa0f2e592a4ba58e\",\n" +
                "\t\"gasq_order_sn\": [\"20150521193137348111\"],\n" +
                "\t\"name\": \"用户AAA\",\n" +
                "\t\"sum_price\": \"23.00\"\n" +
                "}";


        String dd ="{\n" +
                "    \"area_name\":\"朝阳区\",\n" +
                "    \"latitude\":\"39.9221\",\n" +
                "    \"service_info\":[\n" +
                "        {\n" +
                "            \"buy_num\":1,\n" +
                "            \"pay_price\":23,\n" +
                "            \"cate_goods_id\":\"452f5b2db7e14b3f9765cd332a335bf1_33a4397da6a6426bb1d6a997e3042875\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"city_code\":\"010\",\n" +
                "    \"remark\":\"\",\n" +
                "    \"province_code\":\"110000\",\n" +
                "    \"placename\":\"关东店北街11号楼\",\n" +
                "    \"login_mobile\":\"13520174926\",\n" +
                "    \"platform\":\"gasq\",\n" +
                "    \"store_phone\":\"010-33636634\",\n" +
                "    \"city_name\":\"北京市\",\n" +
                "    \"store_name\":\"烧饼门店\",\n" +
                "    \"eshop_code\":\"E161943692063860498\",\n" +
                "    \"order_type\":\"group_split_yes\",\n" +
                "    \"longitude\":\"116.461\",\n" +
                "    \"store_id\":\"12345678901234567890\",\n" +
                "    \"service_time\":\"2018-05-22 09:00:00\",\n" +
                "    \"remark_pic\":[\n" +
                "\n" +
                "    ],\n" +
                "    \"store_addr\":\"北京市朝阳区呼家楼宾馆写字楼\",\n" +
                "    \"detail_address\":\"恶徒夺目\",\n" +
                "    \"area_code\":\"110105\",\n" +
                "    \"gasq_order_status\":\"waitConfirm\",\n" +
                "    \"province_name\":\"北京市\",\n" +
                "    \"opt\":\"createOrder\",\n" +
                "    \"login_name\":\"哈喽去\",\n" +
                "    \"phone\":\"13520174926\",\n" +
                "    \"group_id\":\"577b055ad5ba4a34bea5ef99693c5811\",\n" +
                "    \"gasq_order_id\":\"58c0d50c5ac441a6aa0f2e592a4ba58e\",\n" +
                "    \"gasq_order_sn\":[\n" +
                "        \"21170521193137348111\",\n" +
                "        \"21170521193137348222\",\n" +
                "        \"21170521193137348333\",\n" +
                "        \"21170521193137348444\",\n" +
                "        \"21170521193137348555\"\n" +
                "    ],\n" +
                "    \"name\":\"据他\",\n" +
                "    \"sum_price\":\"23.00\"\n" +
                "}";

        String xx = "{\"area_name\":\"朝阳区\",\"latitude\":\"39.9214\",\"service_info\":[{\"buy_num\":2,\"pay_price\":750.00,\"cate_goods_id\":\"0abf778fc2e64db785df4f64f3e6d888_f74e1c1698594d36a4a98a0301487e01\"}],\"city_code\":\"010\",\"remark\":\"\",\"province_code\":\"110000\",\"placename\":\"北京国际中心\",\"login_mobile\":\"13520174926\",\"platform\":\"gasq\",\"store_phone\":\"010-33636634\",\"city_name\":\"北京市\",\"store_name\":\"呼家楼门店\",\"eshop_code\":\"E180924095431792460\",\"order_type\":\"group_split_no\",\"longitude\":\"116.463\",\"store_id\":\"00000000000000000000000000000034\",\"service_time\":\"2018-05-30 06:00:00\",\"remark_pic\":[],\"store_addr\":\"北京市朝阳区呼家楼宾馆写字楼\",\"detail_address\":\"了\",\"area_code\":\"110105\",\"is_split\":\"no\",\"gasq_order_status\":\"waitConfirm\",\"province_name\":\"北京市\",\"opt\":\"createOrder\",\"login_name\":\"哈喽去\",\"phone\":\"13520174926\",\"group_id\":\"432073e6f4a6482984bc33a34499b326\",\"gasq_order_id\":\"4cf735f548fc4219b80d6d0117b6f574\",\"gasq_order_sn\":[\"20180528135501498821\"],\"name\":\"了\",\"sum_price\":\"1500.00\"}";
String yy ="{\"opt\":\"cancelAppoint\",\"group_id\":\"c36caa073b2741758b01016d6aa59681\",\"gasq_order_sn\":\"20180528143249292882\",\"platform\":\"gasq\"}";




    String as = "{\"opt\":\"updateOrderStatus\",\"group_id\":\"840c74c5aaa94eac8bd3dea0a5b95662\",\"gasq_order_id\":\"042c34e0216c42afb60981cd55a73731\",\"gasq_order_sn\":\"20180528155318546185\",\"comment\":\"\",\"platform\":\"gasq\",\"status\":\"cancel\"}";

    String qh2 = "{\"service_time\":\"2018-05-31 06:00:00\",\"opt\":\"appointOrder\",\"remark_pic\":[],\"group_id\":\"df498830300249a4b74712e9255503ad\",\"gasq_order_sn\":[\"20180528201629480276\"],\"service_info\":[{\"pay_price\":32.00,\"cate_goods_id\":\"0abf778fc2e64db785df4f64f3e6d888_8b0f24fd80184f27b48436566f1d5b0e\"}],\"remark\":\"\",\"orderIds\":\"00c38c2f1b894b7886181cff19563656\",\"platform\":\"gasq\"}";
String qh3 = "{\"service_time\":\"2018-05-29 23:30:00\",\"opt\":\"appointOrder\",\"remark_pic\":[],\"group_id\":\"7bdbb912c7b747b69f1a6243c180d242\",\"gasq_order_sn\":[\"20180529144756779216\",\"20180529144756777226\"],\"service_info\":[{\"pay_price\":326.40,\"cate_goods_id\":\"0abf778fc2e64db785df4f64f3e6d888_8b0f24fd80184f27b48436566f1d5b0e\"}],\"orderIds\":\"1286eeb625184537b393a6b7d7fda7d3,4baa486fc28a49189a90e6ef9e40edc5\",\"platform\":\"gasq\"}";

String qh4 = "{\"opt\":\"updateOrderStatus\",\"group_id\":\"f813014ae7984c6e9c6814ee2971a820\",\"gasq_order_id\":\"16b7d5b3dfde4d719723ffc2f1ecc5cb\",\"gasq_order_sn\":\"20180529191014675815\",\"comment\":\"\",\"platform\":\"gasq\",\"status\":\"cancel\"}";
String qh5="{\"area_name\":\"朝阳区\",\"latitude\":\"39.9221\",\"service_info\":[{\"buy_num\":1,\"pay_price\":326.40,\"cate_goods_id\":\"0abf778fc2e64db785df4f64f3e6d888_b338c4acbff441439e79a008e0771cc3\"}],\"city_code\":\"010\",\"province_code\":\"\",\"placename\":\"关东店北街\",\"login_mobile\":\"13520174926\",\"store_phone\":\"010-33636634\",\"city_name\":\"北京市\",\"store_name\":\"呼家楼店\",\"eshop_code\":\"E180924095431792460\",\"order_type\":\"group_split_yes\",\"longitude\":\"116.46149\",\"store_id\":\"00000000000000000000000000000034\",\"remark_pic\":[],\"store_addr\":\"北京市朝阳区呼家楼街道向军南里2巷5号呼家楼宾馆一层西侧大厅\",\"detail_address\":\"非常减肥操接触过陈键锋\",\"area_code\":\"110105\",\"is_split\":\"yes\",\"gasq_order_status\":\"waitConfirm\",\"province_name\":\"北京市\",\"opt\":\"createOrder\",\"login_name\":\"哈喽去\",\"phone\":\"13520174926\",\"group_id\":\"4225105311e9417aa1f366cdca453ca3\",\"gasq_order_id\":\"1d6a7edec8f547e9ae1df0f506928c3a\",\"gasq_order_sn\":[\"20180529194815579984\",\"20180529194815581339\",\"20180529194815589767\",\"20180529194815585630\",\"20180529194815583568\",\"20180529194815577078\",\"20180529194815588622\",\"20180529194815586742\"],\"name\":\"春风飞虎陈飞虎\",\"sum_price\":\"326.40\"}";

        String qh6 = "{\"opt\":\"updateOrderStatus\",\"group_id\":\"eae96775645b4796a04f7eb3e8550b49\",\"gasq_order_id\":\"89180f7bc17741d297044da7f728410e\",\"gasq_order_sn\":\"20180530161449235333\",\"comment\":\"\",\"status\":\"cancel\"}";
String qh7="{\"opt\":\"updateOrderStatus\",\"group_id\":\"cb2feffb10574cdbb179c6889b0a2aaa\",\"gasq_order_id\":\"6c547653315440238f2a017255e7c536\",\"gasq_order_sn\":\"20180530175258390494\",\"service_order_id\":\"201805301755000188363364\",\"platform\":\"gasq\",\"status\":\"cancel\"}";







        String encode = Base64Encoder.encode(qh7).replace("\r", "").replace("\n", "").replace("\t", "");

        System.out.println(encode);
        System.out.println(MD5Util.getStringMD5(encode+"7e1c77ac-29c4-40f3-ad2f-1027dc75713c"));
    }
}
