package com.zman.stock.service;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zman.stock.data.domain.FinanceForecast;
import com.zman.stock.data.domain.HoldStockInfo;
import com.zman.stock.data.domain.StockBasicInfo;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class StockDataService {

    private static final Logger logger = LoggerFactory
            .getLogger(StockDataService.class);
    /**
     * 备份文件的结尾，备份文件名=原文件名+"_backup"
     */
    public static final String backupExtension = "_backup";

    protected final static ObjectMapper mapper = new ObjectMapper();

    @Value("${stock.basic.info.file}")
    protected String stockBasicInfoFile;

    @Value("${stock.basic.finance.dir}")
    private String basicFinanceDir;

    @Value("${stock.hold.info.file}")
    private String holdStockInfoFile;

    @Value("${finance-forecast-file-path}")
    private String financeForecastFilepath;

    /**
     * 读取文件获得所有股票基本信息
     * 
     * @return
     */
    public Map<String, StockBasicInfo> getAllStockBasicInfo() {
        Map<String, StockBasicInfo> allStockBasicInfoMap = null;

        // 读取所有股票基本信息
        File file = new File(stockBasicInfoFile);
        if( !file.exists() ){
            logger.info("当前股票基本信息文件不存在，应该第一次下载股票基本数据");
            return Collections.emptyMap();
        }
        try {
            JavaType javaType = mapper.getTypeFactory().constructMapType(
                    Map.class, String.class, StockBasicInfo.class);
            allStockBasicInfoMap = mapper.readValue(
                    file, javaType);
        } catch (Exception e) {
            logger.error("从文件中读取所有股票信息出错,file:{}", stockBasicInfoFile);
            logger.error("", e);
            allStockBasicInfoMap = Collections.emptyMap();
        }
        return allStockBasicInfoMap;
    }

    /**
     * 获得股票的财务数据：报告期->指标->数值
     * 
     * 报告期，例： "2015年年报", "2015年三季报", "2015年中报", "2015年一季报", "2014年年报"
     * 
     * 指标：发布日期，营业收入，收入同比增长率，收入环比增长率，净利润，净利润同比增长率"，净利润环比增长率",
     * ，每股收益，每股净资产，资产收益率，每股现金流，毛利率
     * 
     * @param stockCode
     * @return
     * @throws Exception
     */
    public Map<String, Map<String, String>> getBasicFinanceData(String stockCode)
            throws Exception {
        @SuppressWarnings("unchecked")
        Map<String, Map<String, String>> financeMap = mapper.readValue(
                new File(basicFinanceDir, stockCode), Map.class);
        return financeMap;
    }

    /**
     * 读取持有的股票信息
     * 
     * @throws Exception
     */
    public Map<String, HoldStockInfo> loadHoldStockInfo() throws Exception {
        Map<String, HoldStockInfo> holdStockInfoMap = null;
        // 读取所有股票基本信息
        try {
            JavaType javaType = mapper.getTypeFactory().constructMapType(
                    Map.class, String.class, HoldStockInfo.class);
            holdStockInfoMap = mapper.readValue(new File(holdStockInfoFile),
                    javaType);
        } catch (FileNotFoundException e) {
            logger.warn("文件不存在，file{}", holdStockInfoFile);
            holdStockInfoMap = new HashMap<>();
        } catch (Exception e) {
            logger.error("从文件中读取持有的股票信息出错,file:{}", holdStockInfoFile);
            throw e;
        }
        return holdStockInfoMap;
    }

    /**
     * 写入或更新持有股票信息
     * 
     * @throws IOException
     */
    public void writeHoldStockInfo(Map<String, HoldStockInfo> map)
            throws IOException {
        try {
            mapper.writeValue(new File(holdStockInfoFile), map);
        } catch (IOException e) {
            logger.error("写入或更新持有股票信息出错,file: {}", holdStockInfoFile);
            throw e;
        }
    }

    /**
     * 备份原始文件，并写入新文件
     * 
     * @throws IOException
     */
    public void backupAndWriteNew(String filePath, Object content)
            throws IOException {
        File srcFile = new File(filePath);
        try {
            FileUtils.copyFile(srcFile, new File(filePath + backupExtension));
        } catch (FileNotFoundException e) {
            logger.warn("文件没找到: {}", filePath);
        } catch (IOException e) {
            logger.error("备份文件出错,原文件名:{}", filePath);
            throw e;
        }

        mapper.writeValue(srcFile, content);
    }

    /**
     * 输出财务预告到文件
     */
    public void writeFinanceForecast(Map<String, FinanceForecast> value)
            throws IOException {
        mapper.writeValue(new File(financeForecastFilepath), value);
    }

    /**
     * 加载财务预告信息
     */
    public Map<String, FinanceForecast> loadFinanceForecast()
            throws IOException {
        JavaType javaType = mapper.getTypeFactory().constructMapType(Map.class,
                String.class, FinanceForecast.class);
        return mapper.readValue(new File(financeForecastFilepath), javaType);
    }

}
