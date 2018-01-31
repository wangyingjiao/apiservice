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
                    "\"cate_goods_id\":\"5bb6d03fd80b4df0b558083b9a927fef_8a2e91df3dbc4336a914075eec494b1a\"," +
                    "\"gasq_product_id\":\"bda23cc2b60a1fe9ae19a67af537cc6e\"," +
                    "\"pay_price\":\"1\"," +
                    "\"buy_num\":\"1\"" +
                "}]," +
                "\"platform\":\"gasq\"" +
                "}";
        // 订单创建
        String order=
                "{\"store_id\":\"00000000000000000000000000000034\"," +
                        "\"eshop_code\":\"E172493508099668134\"," +

                        "\"phone\":\"18613864806\"," +
                        "\"remark\":\"\"," +
                        "\"gasq_order_id\":\"20170129151748122825\"," +

                        "\"province_code\":\"110000\"," +
                        "\"city_code\":\"010\"," +
                        "\"area_code\":\"110101\"," +
                        "\"address\":\"北京市北京市朝阳区关东店北街11号楼热的天\"," +

                        "\"service_time\":\"2018-01-31 10:30:00\"," +
                        "\"latitude\":\"116.461\"," +
                        "\"longitude\":\"39.9221\"," +

                        "\"sum_price\":\"1\"," +
                        "\"order_type\":\"common\"," +

                        "\"service_info\": [{" +
                        "\"cate_goods_id\":\"5bb6d03fd80b4df0b558083b9a927fef_8a2e91df3dbc4336a914075eec494b1a\"," +
                        "\"pay_price\":\"1\"," +
                        "\"buy_num\":\"1\"" +
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


        String encode = Base64Encoder.encode(time);
        System.out.println(encode);
        System.out.println(MD5Util.getStringMD5(encode+"7e1c77ac-29c4-40f3-ad2f-1027dc75713c"));
    }
}
