package org.king2.test;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.king2.webkcache.cache.consumer.PrConsumer;
import org.king2.webkcache.cache.definition.CacheDefinition;
import org.king2.webkcache.cache.pojo.PrData;

import java.io.File;
import java.util.Arrays;

public class TestZip {

    @Test
    public void testZip() {

        byte[] bytes = new byte[10244];
        PrData test = new PrData("测试", new CacheDefinition(bytes, true, 1000000), "C://dadaasdasdadsdasdasdasdadsdasd/asdas/das/das/dasd");
        System.out.println(JSON.toJSONString(test).getBytes().length);
        byte[] zip = PrConsumer.zip(test);
        System.out.println(zip.length);
        String s = Arrays.toString(zip);
        s = s.substring(1);
        s = s.substring(0, s.length() - 1);
        byte[] unzip = PrConsumer.unzip(zip);
        PrData prData = JSON.parseObject(new String(unzip), PrData.class);
        System.out.println();
    }


    @Test
    public void testWrite() {
        byte[] bytes = new byte[10244];
        PrData test = new PrData("测试", new CacheDefinition(bytes, true, 1000000), "C://dadaasdasdadsdasdasdasdadsdasd/asdas/das/das/dasd");
        byte[] zip = PrConsumer.zip(test);
        PrConsumer.prDisk(zip, new File("/Users/sam/luqiqi/codes/Demo.txt"));
    }
}
