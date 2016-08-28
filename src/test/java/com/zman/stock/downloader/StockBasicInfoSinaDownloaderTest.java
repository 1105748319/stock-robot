package com.zman.stock.downloader;

import com.zman.stock.data.domain.StockBasicInfo;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by zman on 2016/8/28.
 */
public class StockBasicInfoSinaDownloaderTest {

    @Test
    public void findPageCount() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method findPageCountMethod = StockBasicInfoSinaDownloader.class.getDeclaredMethod("findPageCount",String.class);
        findPageCountMethod.setAccessible(true);
        String url = "http://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/Market_Center.getHQNodeStockCount?node=hs_a";
        int count = (int) findPageCountMethod.invoke(new StockBasicInfoSinaDownloader(),url);
        System.out.println( count );
    }

    @Test
    public void findStockBasicInfo() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String url = "http://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/Market_Center.getHQNodeData?page=1&num=80&sort=symbol&asc=1&node=hs_a&symbol=&_s_r_a=init#";

        Method findStockBasicInfoMethod = StockBasicInfoSinaDownloader.class.getDeclaredMethod("findStockBasicInfo",String.class);
        findStockBasicInfoMethod.setAccessible(true);
        Map<String, StockBasicInfo> result = (Map<String, StockBasicInfo>) findStockBasicInfoMethod.invoke(new StockBasicInfoSinaDownloader(), url);
        for( StockBasicInfo stock : result.values() ){
            System.out.println(stock);
        }

    }

}
