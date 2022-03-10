package com.idc.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.idc.common.constants.Constants;
import com.idc.common.translate.TransApi;
import com.idc.common.translate.TransVo;
import com.idc.common.utils.UtilHandle;
import com.idc.dao.entity.IndicatorBean;
import com.idc.dao.entity.ReporterBean;
import com.idc.dao.entity.WtoBean;
import com.idc.service.DataService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.stereotype.Service;

import javax.xml.stream.events.Characters;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service("WTODataServiceImpl")
@Slf4j
public class WTODataServiceImpl implements DataService {

    private final String KEY = "1dc531027a3b48a588e167c449bdb739";
    private final String DATA_URL = "https://api.wto.org/timeseries/v1/data";
    private final String INDICATORS_URL = "https://api.wto.org/timeseries/v1/indicators?i=all&t=all&pc=all&tp=all&frq=all&lang=1";
    private final String REPORTERS_URL = "https://api.wto.org/timeseries/v1/reporters?ig=all&reg=all&gp=all&lang=1";
    private String enStr = "MFN -  Simple average duty#MFN -  Trade weighted average duty#MFN -  Maximum duty#MFN -  National peaks (greater than 3x national average)#MFN -  International peaks (greater than 15%)#MFN -  Duty free#MFN -  Non ad valorem duties#MFN -  Imports duty free#MFN -  Coefficient of variation#MFN -  Number of distinct duty rates#MFN -  Number of applied tariff lines#MFN AG -  Simple average duty#MFN AG -  Trade weighted average duty#MFN AG -  Maximum duty#MFN AG -  Tariffs greater than 3* national average#MFN AG -  Tariffs greater than 15%#MFN AG -  Duty free#MFN AG -  Duties in duty range:  0 <= 5%#MFN AG -  Duties in duty range:  5% <= 10%#MFN AG -  Duties in duty range:  10% <= 15%#MFN AG -  Duties in duty range:  15% <= 25%#MFN AG -  Duties in duty range:  25% <= 50%#MFN AG -  Duties in duty range:  50% <= 100%#MFN AG -  Duties in duty range: > 100%#MFN AG -  Non ad valorem duties#MFN AG -  Imports duty free#MFN AG -  Imports in duty range:  0 <= 5%#MFN AG -  Imports in duty range:  5% <= 10%#MFN AG -  Imports in duty range:  10% <= 15%#MFN AG -  Imports in duty range:  15% <= 25%#MFN AG -  Imports in duty range:  25% <= 50%#MFN AG -  Imports in duty range:  50% <= 100%#MFN AG -  Imports in duty range: > 100%#MFN AG -  Imports facing non ad valorem duties#MFN AG -  Coefficient of variation#MFN AG -  Number of distinct duty rates#MFN AG -  Number of applied tariff lines#MFN Non-AG -  Simple average duty#MFN Non-AG -  Trade weighted average duty#MFN Non-AG -  Maximum duty#MFN Non-AG -  Tariffs greater than 3* national average#MFN Non-AG -  Tariffs greater than 15%#MFN Non-AG -  Duty free#MFN Non-AG -  Duties in duty range:  0 <= 5%#MFN Non-AG -  Duties in duty range:  5% <= 10%#MFN Non-AG -  Duties in duty range:  10% <= 15%#MFN Non-AG -  Duties in duty range:  15% <= 25%#MFN Non-AG -  Duties in duty range:  25% <= 50%#MFN Non-AG -  Duties in duty range:  50% <= 100%#MFN Non-AG -  Duties in duty range: > 100%#MFN Non-AG -  Non ad valorem duties#MFN Non-AG -  Imports duty free#MFN Non-AG -  Imports in duty range:  0 <= 5%#MFN Non-AG -  Imports in duty range:  5% <= 10%#MFN Non-AG -  Imports in duty range:  10% <= 15%#MFN Non-AG -  Imports in duty range:  15% <= 25%#MFN Non-AG -  Imports in duty range:  25% <= 50%#MFN Non-AG -  Imports in duty range:  50% <= 100%#MFN Non-AG -  Imports in duty range: > 100%#MFN Non-AG -  Imports facing non ad valorem duties#MFN Non-AG -  Coefficient of variation#MFN Non-AG -  Number of distinct duty rates#MFN Non-AG -  Number of applied tariff lines#MFN -  Simple average duty by product groups#MFN -  Maximum duty by product groups#MFN -  Duty free by product#MFN -  Imports duty free by product groups#MFN -  Imports by product groups#HS MFN - Simple average ad valorem duty#HS MFN - Simple average with ad valorem equivalents (AVE)#HS MFN - Maximum  ad valorem duty#HS MFN - Maximum duty including ad valorem equivalents (AVE)#HS MFN - Duty free#HS MFN - Number of national tariff lines#HS MFN - Number of NAV tariff lines#HS Pref - Best preferential simple average ad valorem duty#Bilateral imports by MTN product category#Bilateral imports by detailed HS codes (2,4,6 digit)#Bnd  -  Binding coverage#Bnd  -  Concessions not yet implemented#Bnd  -  Tariffs greater than 3* national average#Bnd  -  Tariffs greater than 15%#Bnd  -  Maximum duty#Bnd  -  Simple average duty#Bnd  -  Duty free#Bnd  -  Non ad valorem duties#Bnd  -  Coefficient of variation#Bnd  -  Number of distinct duty rates#Bnd AG -  Simple average duty#Bnd AG -  Concessions not yet implemented#Bnd AG -  Tariffs greater than 3* national average#Bnd AG -  Tariffs greater than 15%#Bnd AG -  Maximum duty#Bnd AG -  Special Safeguards bound#Bnd AG -  Bound tariff quotas#Bnd AG -  Duty free#Bnd AG -  Duties in duty range:  0 <= 5#Bnd AG -  Duties in duty range:  5 <= 10#Bnd AG -  Duties in duty range:  10 <= 15#Bnd AG -  Duties in duty range:  15 <= 25#Bnd AG -  Duties in duty range:  25 <= 50#Bnd AG -  Duties in duty range:  50 <= 100#Bnd AG -  Duties in duty range: > 100#Bnd AG -  Non ad valorem duties#Bnd AG -  AG -Coefficient of variation#Bnd AG -  Number of distinct duty rates#Bnd Non-AG -  Simple average duty#Bnd Non-AG -  Binding coverage#Bnd Non-AG -  Concessions not yet implemented#Bnd Non-AG -  Tariffs greater than 3* national average#Bnd Non-AG -  Tariffs greater than 15%#Bnd Non-AG -  Maximum duty#Bnd Non-AG -  Duty free#Bnd Non-AG -  Duties in duty range:  0 <= 5#Bnd Non-AG -  Duties in duty range:  5 <= 10#Bnd Non-AG -  Duties in duty range:  10 <= 15#Bnd Non-AG -  Duties in duty range:  15 <= 25#Bnd Non-AG -  Duties in duty range:  25 <= 50#Bnd Non-AG -  Duties in duty range:  50 <= 100#Bnd Non-AG -  Duties in duty range: > 100#Bnd Non-AG -  Non ad valorem duties#Bnd Non-AG -  Coefficient of variation#Bnd Non-AG -  Number of distinct duty rates#Bnd  -  Binding coverage by product groups#Bnd  -  Maximum duty by product groups#Bnd  -  Simple average duty by product groups#Bnd  -  Duty free by product groups#HS Bnd - Simple average ad valorem duty#HS Bnd - Maximum  ad valorem duty#HS Bnd - Duty free#HS Bnd - Number of national tariff lines#HS Bnd - Number of NAV tariff lines#HS ODC - Dutiable tariff lines#HS ODC - AVG ad valorem duty#HS ODC - Number of NAV tariff lines#AG - Partner rank based on exports value to partner#AG - Trade weighted average preferential duty faced#AG - Trade weighted average MFN applied duty faced#AG - Simple average MFN applied duty faced#AG - Trade weighted average preferential margin faced#AG - Value of exports to partner (imports by partner)#AG - Share of duty free exports to partner#AG - Share of duty free tariff lines exported to partner#AG - HS chapters exported to partner#AG - HS subheadings exported to partner#Non-AG - Rank based on exports value to partner#Non-AG - Trade weighted average preferential duty faced#Non-AG - Trade weighted average MFN applied duty faced#Non-AG - Simple average MFN applied duty faced#Non-AG - Trade weighted average preferential margin#Non-AG - Value of exports to partner (imports by partner)#Non-AG - Share of duty free exports to partner#Non-AG - Share of duty free tariff lines exported to partner#Non-AG - HS chapters exported to partner#Non-AG - HS subheadings exported to partner#ADP final measures in force (cumulated)#ADP measures initiated#ADP final measures implemented#ADP final measures withdrawn#ADP final measures in force (cumulated) - faced by exporter#ADP measures initiated - faced by exporter#ADP final measures implemented - faced by exporter#ADP final measures withdrawn - faced by exporter#ADP final measures in force (cumulated) by partners#ADP measures initiated by partners#ADP final measures implemented by partners#ADP final measures withdrawn by partners#ADP final measures in force (cumulated) by product groups#ADP measures initiated by product groups#ADP final measures implemented by product groups#ADP final measures withdrawn by product groups#ADP final measures in force (cumulated) by product groups - faced by exporter#ADP measures initiated by product groups - faced by exporter#ADP final measures implemented by product groups - faced by exporter#ADP final measures withdrawn by product groups - faced by exporter#CVD final measures in force (cumulated)#CVD measures initiated#CVD final measures implemented#CVD final measures withdrawn#CVD final measures in force (cumulated) - faced by exporter#CVD measures initiated - faced by exporter#CVD final measures implemented - faced by exporter#CVD final measures withdrawn - faced by exporter#CVD final measures in force (cumulated) by partners#CVD measures initiated by partners#CVD final measures implemented by partners#CVD final measures withdrawn by partners#CVD final measures in force (cumulated) by product groups#CVD measures initiated by product groups#CVD final measures implemented by product groups#CVD final measures withdrawn by product groups#CVD final measures in force (cumulated) by product groups - faced by exporter#CVD measures initiated by product groups - faced by exporter#CVD final measures implemented by product groups - faced by exporter#CVD final measures withdrawn by product groups - faced by exporter#SG final measures in force (cumulated)#SG measures initiated#SG final measures implemented#SG final measures withdrawn#SG final measures in force (cumulated) by product groups#SG measures initiated by product groups#SG final measures implemented by product groups#SG final measures withdrawn by product groups#SPS measures notified#SPS measures notified (cumulated)#Merchandise exports by product group – annual#Merchandise imports by product group – annual#Total merchandise exports - quarterly#Total merchandise imports - quarterly#Total merchandise exports - monthly#Total merchandise imports - monthly#Merchandise export value chained indices - annual#Merchandise import value chained indices - annual#Merchandise export volume chained indices - annual#Merchandise import volume chained indices - annual#Merchandise export unit value chained indices - annual#Merchandise import unit value chained indices - annual#Merchandise export value fixed-base indices - annual#Merchandise import value fixed-base indices - annual#Merchandise export volume fixed-base indices - annual#Merchandise import volume fixed-base indices - annual#Merchandise export unit value fixed-base indices - annual#Merchandise import unit value fixed-base indices - annual#Merchandise export volume change - annual#Merchandise import volume change - annual#Merchandise export volume indices, not seasonally adjusted - quarterly#Merchandise import volume indices, not seasonally adjusted - quarterly#Merchandise export volume indices, seasonally adjusted - quarterly#Merchandise import volume indices, seasonally adjusted - quarterly#Export price changes of manufactured goods - monthly#Import price changes of manufactured goods - monthly#Export price indices of manufactured goods - monthly#Import price indices of manufactured goods - monthly#Commercial services exports by sector and partner – annual#Commercial services imports by sector and partner – annual#Commercial services exports by sector – annual  (1980-2013)#Commercial services imports by sector – annual (1980-2013)#Commercial services exports by main sector – quarterly#Commercial services imports by main sector – quarterly#Commercial services exports of selected economies – monthly#Commercial services imports of selected economies – monthly#FATS - Sales by service sector (inward)#FATS - Sales by service sector (outward)#FATS - Number of foreign affiliates by service sector (inward)#FATS - Number of foreign affiliates by service sector (outward)#FATS - Number of employees by service sector (inward)#FATS - Number of employees by service sector (outward)#Services exports: reported values#Services imports: reported values#Services exports: reported values including estimates#Services imports: reported values including estimates#Services exports: balanced values#Services imports: balanced values";
    private String zhResult = "MFN - 简单平均关税#MFN - 贸易加权平均关税#MFN - 最高关税#MFN - 全国峰值（大于全国平均水平的 3 倍）#MFN - 国际峰值（大于 15%）#MFN - 免税#MFN - 非广告从价关税#MFN - 进口免税#MFN - 变异系数#MFN - 不同关税税率的数量#MFN - 适用关税细目的数量#MFN AG - 简单平均关税#MFN AG - 贸易加权平均关税#MFN AG - 最大值关税#MFN AG - 关税高于 3* 全国平均水平#MFN AG - 关税高于 15%#MFN AG - 免税#MFN AG - 关税范围内的关税：0 <= 5%#MFN AG - 关税范围内的关税： 5% <= 10%#MFN AG - 工作范围内的关税：10% <= 15%#MFN AG - 工作范围内的关税：15% <= 25%#MFN AG - 工作范围内的关税：25% <= 50 %#MFN AG - 关税范围内的关税：50% <= 100%#MFN AG - 关税范围内的关税：> 100%#MFN AG - 非从价关税#MFN AG - 进口免税#MFN AG - 关税进口范围：0 <= 5%#MFN AG - 关税范围内的进口：5% <= 10%#MFN AG - 关税范围内的进口: 10% <= 15%#MFN AG - 关税范围内的进口：15% <= 25%#MFN AG - 关税范围内的进口：25% <= 50%#MFN AG - 关税范围内的进口：50% <= 100%#MFN AG - 关税范围内的进口：> 100%#MFN AG - 面临非从价关税的进口#MFN AG - 变异系数#MFN AG - 不同关税税率的数量#MFN AG - 适用关税细目的数量# MFN Non-AG - 简单平均关税#MFN Non-AG - 贸易加权平均关税#MFN Non-AG - 最高关税#MFN Non-AG - 关税高于 3* 全国平均水平#MFN Non-AG - 关税高于 15% #MFN Non-AG - 免税#MFN Non-AG - 关税范围内的关税：0 <= 5%#MFN Non-AG - 关税范围内的关税：5% <= 10%#MFN Non-AG - 关税范围内的关税范围：10% <= 15%#MFN Non-AG - 职责范围内的职责：15% <= 25%#MFN Non-AG - 职责范围内的职责：25% <= 50%#MFN Non-AG - 职责范围内关税范围：50% <= 100%#MFN Non-AG - 关税范围内的关税：> 100%#MFN Non-AG - 非从价关税#MFN Non-AG - 进口免税#MFN Non-AG - 进口占空比：0 <= 5%#MFN Non-AG - Import s 在关税范围内：5% <= 10%#MFN Non-AG - 在关税范围内的进口：10% <= 15%#MFN Non-AG - 在关税范围内的进口：15% <= 25%#MFN Non-AG - 关税范围内的进口：25% <= 50%#MFN Non-AG - 关税范围内的进口：50% <= 100%#MFN Non-AG - 关税范围内的进口：> 100%#MFN Non-AG - 进口面临非从价关税#MFN Non-AG - 变异系数#MFN Non-AG - 不同关税税率的数量#MFN Non-AG - 适用关税细目的数量#MFN - 产品组的简单平均关税#MFN - 最高关税按产品组#MFN - 按产品免税#MFN - 按产品组免税进口#MFN - 按产品组进口#HS MFN - 简单平均从价关税#HS MFN - 简单平均从价当量 (AVE)#HS MFN - 最高从价关税#HS MFN - 包括从价等价物 (AVE) 在内的最高关税#HS MFN - 免税#HS MFN - 国家关税细目数量#HS MFN - NAV 关税细目数量#HS Pref - 最优惠的简单平均从价税#按MTN产品类别的双边进口血腥#双边进口按详细的 HS 编码（2、4、6 位）#Bnd - 具有约束力的覆盖范围#Bnd - 优惠尚未实施#Bnd - 关税高于 3* 全国平均水平#Bnd - 关税高于 15%#Bnd - 最高关税#Bnd - 简单平均关税#Bnd - 免税#Bnd - 非从价关税#Bnd - 变动系数#Bnd - 不同税率的数量#Bnd AG - 简单平均关税#Bnd AG - 尚未实施的优惠#Bnd AG - 关税高于 3* 全国平均水平#Bnd AG - 关税高于 15%#Bnd AG - 最高关税#Bnd AG - 受约束的特殊保障措施#Bnd AG - 约束关税配额#Bnd AG - 免税#Bnd AG - 关税工作范围：0 <= 5#Bnd AG - 工作范围内的工作：5 <= 10#Bnd AG - 工作范围内的工作：10 <= 15#Bnd AG - 工作范围内的工作：15 <= 25#Bnd AG -关税范围内的关税：25 <= 50#Bnd AG - 关税范围内的关税：50 <= 100#Bnd AG - 关税范围内的关税：> 100#Bnd AG - 非从价关税#Bnd AG - AG - 变异系数#Bnd AG - 不同税率的数量#Bnd 否n-AG - 简单平均关税#Bnd Non-AG - 具有约束力的覆盖范围#Bnd Non-AG - 优惠尚未实施#Bnd Non-AG - 关税高于 3* 全国平均水平#Bnd Non-AG - 关税高于 15%# Bnd Non-AG - 最大关税#Bnd Non-AG -免税#Bnd Non-AG - 关税范围内的关税：0 <= 5#Bnd Non-AG - 关税范围内的关税：5 <= 10#Bnd Non- AG - 职责范围：10 <= 15#Bnd Non-AG - 职责范围：15 <= 25#Bnd Non-AG - 职责范围：25 <= 50#Bnd Non-AG - 职责范围范围：50 <= 100#Bnd Non-AG - 关税范围内的关税：> 100#Bnd Non-AG - 非从价关税#Bnd Non-AG - 变异系数#Bnd Non-AG - 不同关税税率的数量# Bnd - 产品组的约束范围#Bnd - 产品组的最高关税#Bnd - 产品组的简单平均关税#Bnd - 产品组的免税#HS Bnd - 简单平均从价关税#HS Bnd - 最高从价关税# HS Bnd - 免税#HS Bnd - 国家关税细目数量#HS Bnd - NAV 关税细目数量#HS ODC - Dut可用关税细目#HS ODC - 平均从价关税#HS ODC - 净资产值关税细目数量#AG - 基于对合作伙伴的出口价值的合作伙伴排名#AG - 面临的贸易加权平均优惠关税#AG - 面临的贸易加权平均最惠国适用关税#AG - 面临的简单平均最惠国适用关税#AG - 面临的贸易加权平均优惠幅度#AG - 对合作伙伴的出口价值（由合作伙伴进口）#AG - 对合作伙伴的免税出口份额#AG - 免税关税细目份额出口到合作伙伴#AG - HS 章节出口到合作伙伴#AG - HS 子标题出口到合作伙伴#Non-AG - 基于对合作伙伴的出口价值排名#Non-AG - 贸易加权平均优惠关税#Non-AG - 贸易加权平均最惠国适用关税#Non-AG - 简单平均最惠国适用关税#Non-AG - 贸易加权平均优惠幅度#Non-AG - 对合作伙伴的出口价值（由合作伙伴进口）#Non-AG - 免税出口份额到合作伙伴#Non-AG - 出口到合作伙伴#Non-AG 的免税关税细目份额 - HS 章节出口到合作伙伴#Non-AG - HS 副标题出口到合作伙伴#ADP 最终措施生效（累积）#ADP 措施启动#ADP 最终措施实施#ADP 最终措施撤消#ADP 最终措施生效（累积） - 面临出口商#ADP 发起的措施 - 出口商面临#ADP 实施的最终措施 - 出口商面临#ADP 最终措施撤回 - 出口商面临#ADP 合作伙伴有效（累计）的最终措施#ADP 合作伙伴发起的措施#ADP 最终措施由合作伙伴实施合作伙伴#合作伙伴撤销的ADP最终措施#产品组有效的ADP最终措施（累计）#产品组发起的ADP措施#产品组实施的ADP最终措施#产品组撤销的ADP最终措施#ADP有效的最终措施（累积）按产品组 - 出口商面临#ADP 产品组发起的措施 - 出口商面临#ADP 产品组实施的最终措施 - 出口商面临#ADP 最终措施由产品组生成 - 出口商面临#CVD 生效的最终措施（累计）#CVD 已启动的措施#CVD 已实施的最终措施#CVD 最终措施已撤消#CVD 已生效的最终措施（累计）- 出口商面临#CVD 已启动的措施 - 面临由出口商#CVD 实施的最终措施 - 出口商面临#CVD 最终措施被撤回 - 出口商面临#CVD 合作伙伴有效（累计）的最终措施#CVD 由合作伙伴发起的措施#CVD 由合作伙伴实施的最终措施#CVD 最终措施由合作伙伴撤销合作伙伴#CVD 最终有效措施（累计）按产品组#CVD 最终措施由产品组发起#CVD 最终措施按产品组实施#CVD 最终措施按产品组撤销#CVD 最终有效措施（累计）按产品组 - 面临出口商#CVD 产品组发起的措施 - 出口商面临#CVD 产品组实施的最终措施 - 出口商#CVD 产品组撤回的最终措施 - 面临出口商#有效的SG最终措施（累积）#已启动的SG措施#已实施的SG最终措施#已撤销的SG最终措施#按产品组的SG最终有效措施（累积）#按产品组启动的SG措施#按产品执行的SG最终措施组#SG 按产品组撤回的最终措施#SPS 措施通知#SPS 措施通知（累积）#按产品组的商品出口 - 年度#按产品组的商品进口 - 年度#商品出口总额 - 季度#商品进口总额 - 季度#Total商品出口 - 月度#商品进口总额 - 月度#商品出口价值链指数 - 年度#商品进口价值链指数 - 年度#商品出口量链指数 - 年度#商品进口量链指数 - 年度#商品出口单位价值链指数 -年度#商品进口单位价值链指数 - 年度#商品出口价值固定基指数 - 年度#商品进口价值固定基础指数 - 年度#商品出口量固定基础指数 - 年度#商品进口量固定基础指数 - 年度#商品出口单位价值固定基础指数 - 年度#商品进口单位价值固定基础指数 - 年度#商品出口量变化 - 年度#商品进口量变化 - 年度#商品出口量指数，未经季节性调整 - 季度#商品进口量指数，未经季节性调整 - 季度#商品出口量指数，经季节性调整 - 季度#商品进口量指数，季节性调整 - 季度#制成品出口价格变化 - 每月#制成品进口价格变化 - 每月#制成品出口价格指数 - 每月#制成品进口价格指数 - 每月#按行业和合作伙伴的商业服务出口 -年度#按部门和合作伙伴划分的商业服务进口 - 年度#按部门划分的商业服务出口 - 年度（1980 年） -2013)#按行业划分的商业服务进口——年度（1980-2013）#按主要行业划分的商业服务出口——每季度#按主要行业划分的商业服务进口——每季度#特定经济体的商业服务出口——每月#特定经济体的商业服务进口– 每月#FATS - 服务部门销售额（向内）#FATS - 服务部门销售额（向外）#FATS - 服务部门外国子公司数量（向内）#FATS - 服务部门外国子公司数量（向外）#FATS - 服务部门的雇员人数（向内）#FATS - 服务部门的雇员人数（向外）#服务出口：报告值#服务进口：报告值#服务出口：报告值，包括估计值#服务进口：报告值，包括估计值#服务出口：平衡值#服务进口：平衡值";
    private String enCounry = "World\n" +
            "Afghanistan\n" +
            "Africa\n" +
            "African, Caribbean and Pacific States (ACP)\n" +
            "Africa, CIS and Middle East\n" +
            "Albania\n" +
            "Algeria\n" +
            "American Samoa\n" +
            "Andean Community (ANDEAN)\n" +
            "Andorra\n" +
            "Angola\n" +
            "Anguilla\n" +
            "Antigua and Barbuda\n" +
            "Argentina\n" +
            "Armenia\n" +
            "Aruba, the Netherlands with respect to\n" +
            "Asia\n" +
            "Asia-Pacific Economic Cooperation (APEC)\n" +
            "Association of Southeast Asian Nations (ASEAN)\n" +
            "Australia\n" +
            "Australia and New Zealand\n" +
            "Austria\n" +
            "Azerbaijan\n" +
            "Bahamas\n" +
            "Bahrain, Kingdom of\n" +
            "Bangladesh\n" +
            "Barbados\n" +
            "Belarus\n" +
            "Belgium\n" +
            "Belgium-Luxembourg\n" +
            "Belize\n" +
            "Benin\n" +
            "Bermuda\n" +
            "Bhutan\n" +
            "Bolivia, Plurinational State of\n" +
            "Bonaire, Sint Eustatius and Saba\n" +
            "Bosnia and Herzegovina\n" +
            "Botswana\n" +
            "Brazil\n" +
            "BRIC members\n" +
            "BRICS members\n" +
            "Brunei Darussalam\n" +
            "Bulgaria\n" +
            "Burkina Faso\n" +
            "Burundi\n" +
            "Cabo Verde\n" +
            "Cambodia\n" +
            "Cameroon\n" +
            "Canada\n" +
            "Caribbean Community (CARICOM)\n" +
            "Cayman Islands\n" +
            "Central African Economic and Monetary Community (CAEMC)\n" +
            "Central African Republic\n" +
            "Central American Common Market (CACM)\n" +
            "Chad\n" +
            "Chile\n" +
            "China\n" +
            "Colombia\n" +
            "Common Market for Eastern and Southern Africa (COMESA)\n" +
            "Commonwealth of Independent States (CIS), including certain associate and former member States\n" +
            "Comoros\n" +
            "Congo\n" +
            "Cook Islands\n" +
            "Costa Rica\n" +
            "Côte d'Ivoire\n" +
            "Croatia\n" +
            "Cuba\n" +
            "Curaçao\n" +
            "Cyprus\n" +
            "Czech and Slovak Federal Republic\n" +
            "Czech Republic\n" +
            "Democratic Republic of the Congo\n" +
            "Denmark\n" +
            "Djibouti\n" +
            "Dominica\n" +
            "Dominican Republic\n" +
            "ECCAS (Economic Community of Central African States)\n" +
            "Ecuador\n" +
            "Egypt\n" +
            "El Salvador\n" +
            "Equatorial Guinea\n" +
            "Eritrea\n" +
            "Estonia\n" +
            "Eswatini\n" +
            "Ethiopia\n" +
            "Ethiopia (+ Eritrea)\n" +
            "Euro Area (19)\n" +
            "Europe\n" +
            "European Free Trade Association (EFTA)\n" +
            "European Union\n" +
            "European Union (28)\n" +
            "Faeroe Islands\n" +
            "Fiji\n" +
            "Finland\n" +
            "Four East Asian traders\n" +
            "France\n" +
            "French Guiana\n" +
            "French Polynesia\n" +
            "French Southern Territories\n" +
            "G-20\n" +
            "G-7 (Group of Seven)\n" +
            "Gabon\n" +
            "The Gambia\n" +
            "Georgia\n" +
            "German Democratic Republic\n" +
            "Germany\n" +
            "Germany, Federal Republic of\n" +
            "Ghana\n" +
            "Gibraltar\n" +
            "Greece\n" +
            "Greenland\n" +
            "Grenada\n" +
            "Guadeloupe\n" +
            "Guam\n" +
            "Guatemala\n" +
            "Guinea\n" +
            "Guinea-Bissau\n" +
            "Gulf Cooperation Council (GCC)\n" +
            "Guyana\n" +
            "Haiti\n" +
            "Honduras\n" +
            "Hong Kong, China\n" +
            "Hungary\n" +
            "Iceland\n" +
            "India\n" +
            "Indonesia\n" +
            "Iran\n" +
            "Iraq\n" +
            "Ireland\n" +
            "Israel\n" +
            "Italy\n" +
            "Jamaica\n" +
            "Japan\n" +
            "Jordan\n" +
            "Kazakhstan\n" +
            "Kenya\n" +
            "Kiribati\n" +
            "Korea, Democratic People's Republic of\n" +
            "Korea, Republic of\n" +
            "Kuwait, the State of\n" +
            "Kyrgyz Republic\n" +
            "Landlocked developing countries\n" +
            "Lao People's Democratic Republic\n" +
            "Latvia\n" +
            "LDC exporters of agriculture\n" +
            "LDC exporters of manufactures\n" +
            "LDC non-fuel mineral exporters\n" +
            "LDC oil exporters\n" +
            "Least-developed countries\n" +
            "Lebanese Republic\n" +
            "Lesotho\n" +
            "Liberia\n" +
            "Libya\n" +
            "Lithuania\n" +
            "Luxembourg\n" +
            "Macao, China\n" +
            "Madagascar\n" +
            "Malawi\n" +
            "Malaysia\n" +
            "Maldives\n" +
            "Mali\n" +
            "Malta\n" +
            "Marshall Islands\n" +
            "Martinique\n" +
            "Mauritania\n" +
            "Mauritius\n" +
            "Mayotte\n" +
            "Mexico\n" +
            "Micronesia, Federated States of\n" +
            "Middle East\n" +
            "Moldova, Republic of\n" +
            "Mongolia\n" +
            "Montenegro\n" +
            "Montserrat\n" +
            "Morocco\n" +
            "Mozambique\n" +
            "Myanmar\n" +
            "Namibia\n" +
            "Nauru\n" +
            "Nepal\n" +
            "Netherlands\n" +
            "Netherlands Antilles\n" +
            "Netherlands Antilles (incl. Aruba)\n" +
            "New Caledonia\n" +
            "New Zealand\n" +
            "Nicaragua\n" +
            "Niger\n" +
            "Nigeria\n" +
            "Niue\n" +
            "Non-EU south-eastern Europe\n" +
            "Non-EU western Europe\n" +
            "North America\n" +
            "North American Free Trade Agreement (NAFTA)\n" +
            "North Macedonia\n" +
            "Northern Mariana Islands\n" +
            "Norway\n" +
            "OIC (Organisation of Islamic Cooperation)\n" +
            "Oman\n" +
            "OPEC (Organization of the Petroleum Exporting Countries)\n" +
            "Other Africa\n" +
            "Other Asia\n" +
            "Other CIS\n" +
            "Pacific Alliance\n" +
            "Pakistan\n" +
            "Palau\n" +
            "Palestine\n" +
            "Panama\n" +
            "Papua New Guinea\n" +
            "Paraguay\n" +
            "Peru\n" +
            "Philippines\n" +
            "Poland\n" +
            "Portugal\n" +
            "Qatar\n" +
            "Reunion\n" +
            "Romania\n" +
            "Russian Federation\n" +
            "Rwanda\n" +
            "Saint Kitts and Nevis\n" +
            "Saint Lucia\n" +
            "Saint Martin\n" +
            "Saint Pierre and Miquelon\n" +
            "Saint Vincent and the Grenadines\n" +
            "Samoa\n" +
            "Sao Tomé and Principe\n" +
            "Saudi Arabia, Kingdom of\n" +
            "Senegal\n" +
            "Serbia\n" +
            "Serbia and Montenegro\n" +
            "Seychelles\n" +
            "Sierra Leone\n" +
            "Singapore\n" +
            "Six East Asian traders\n" +
            "Slovak Republic\n" +
            "Slovenia\n" +
            "Solomon Islands\n" +
            "Somalia\n" +
            "South Africa\n" +
            "South and Central America and the Caribbean\n" +
            "South Asian Association for Regional Cooperation (SAARC)\n" +
            "South Sudan\n" +
            "Southern African Development Community (SADC)\n" +
            "Southern Common Market (MERCOSUR)\n" +
            "Southern Common Market (MERCOSUR) excluding Venezuela, Bolivarian Republic of\n" +
            "Spain\n" +
            "Sri Lanka\n" +
            "Sudan\n" +
            "Suriname\n" +
            "Swaziland\n" +
            "Sweden\n" +
            "Switzerland\n" +
            "Syrian Arab Republic\n" +
            "Chinese Taipei\n" +
            "Tajikistan\n" +
            "Tanzania\n" +
            "Thailand\n" +
            "The former Yugoslav Republic of Macedonia\n" +
            "Timor-Leste\n" +
            "Togo\n" +
            "Tokelau\n" +
            "Tonga\n" +
            "Trinidad and Tobago\n" +
            "Tunisia\n" +
            "Turkey\n" +
            "Turkmenistan\n" +
            "Turks and Caicos Islands\n" +
            "Tuvalu\n" +
            "U.S.S.R.\n" +
            "Uganda\n" +
            "Ukraine\n" +
            "United Arab Emirates\n" +
            "United Kingdom\n" +
            "United States of America\n" +
            "UNMIK/Kosovo\n" +
            "Uruguay\n" +
            "Uzbekistan\n" +
            "Vanuatu\n" +
            "Venezuela\n" +
            "Venezuela, Bolivarian Republic of\n" +
            "Viet Nam\n" +
            "Virgin Islands, British\n" +
            "Wallis and Futuna Islands\n" +
            "West African Economic and Monetary Union (WAEMU)\n" +
            "West African Economic Community (ECOWAS)\n" +
            "WTO Members\n" +
            "WTO Observer governments\n" +
            "Yemen\n" +
            "Yemen, Arab Republic of\n" +
            "Yemen, People's Democratic Republic\n" +
            "Yugoslavia, Socialist Federal Republic of\n" +
            "Zambia\n" +
            "Zimbabwe";
    private String zhCountry = "世界\n" +
            "阿富汗\n" +
            "非洲\n" +
            "非洲、加勒比和太平洋国家 (ACP)\n" +
            "非洲、独联体和中东\n" +
            "阿尔巴尼亚\n" +
            "阿尔及利亚\n" +
            "美属萨摩亚\n" +
            "安第斯共同体 (ANDEAN)\n" +
            "安道尔\n" +
            "安哥拉\n" +
            "安圭拉\n" +
            "安提瓜和巴布达\n" +
            "阿根廷\n" +
            "亚美尼亚\n" +
            "荷兰阿鲁巴岛\n" +
            "亚洲\n" +
            "亚太经济合作组织 (APEC)\n" +
            "东南亚国家联盟（东盟）\n" +
            "澳大利亚\n" +
            "澳大利亚和新西兰\n" +
            "奥地利\n" +
            "阿塞拜疆\n" +
            "巴哈马\n" +
            "巴林王国\n" +
            "孟加拉国\n" +
            "巴巴多斯\n" +
            "白俄罗斯\n" +
            "比利时\n" +
            "比利时-卢森堡\n" +
            "伯利兹\n" +
            "贝宁\n" +
            "百慕大\n" +
            "不丹\n" +
            "多民族玻利维亚国\n" +
            "博内尔岛、圣尤斯特歇斯岛和萨巴岛\n" +
            "波斯尼亚和黑塞哥维那（简称：波黑\n" +
            "博茨瓦纳\n" +
            "巴西\n" +
            "金砖四国成员\n" +
            "金砖国家\n" +
            "文莱达鲁萨兰国\n" +
            "保加利亚\n" +
            "布基纳法索\n" +
            "布隆迪\n" +
            "佛得角\n" +
            "柬埔寨\n" +
            "喀麦隆\n" +
            "加拿大\n" +
            "加勒比共同体（加共体）\n" +
            "开曼群岛\n" +
            "中非经济和货币共同体（CAEMC）\n" +
            "中非共和国\n" +
            "中美洲共同市场 (CACM)\n" +
            "乍得\n" +
            "智利\n" +
            "中国\n" +
            "哥伦比亚\n" +
            "东部和南部非洲共同市场（COMESA）\n" +
            "独立国家联合体（独联体），包括某些准成员国和前成员国\n" +
            "科摩罗\n" +
            "刚果\n" +
            "库克群岛\n" +
            "哥斯达黎加\n" +
            "科特迪瓦\n" +
            "克罗地亚\n" +
            "古巴\n" +
            "库拉索\n" +
            "塞浦路斯\n" +
            "捷克和斯洛伐克联邦共和国\n" +
            "捷克共和国\n" +
            "刚果民主共和国\n" +
            "丹麦\n" +
            "吉布提\n" +
            "多米尼克\n" +
            "多明尼加共和国\n" +
            "ECCAS（中非国家经济共同体）\n" +
            "厄瓜多尔\n" +
            "埃及\n" +
            "萨尔瓦多\n" +
            "赤道几内亚\n" +
            "厄立特里亚\n" +
            "爱沙尼亚\n" +
            "斯威士兰\n" +
            "埃塞俄比亚\n" +
            "埃塞俄比亚（+厄立特里亚）\n" +
            "欧元区 (19)\n" +
            "欧洲\n" +
            "欧洲自由贸易联盟 (EFTA)\n" +
            "欧洲联盟\n" +
            "欧盟 (28)\n" +
            "法罗群岛\n" +
            "斐济\n" +
            "芬兰\n" +
            "四位东亚贸易商\n" +
            "法国\n" +
            "法属圭亚那\n" +
            "法属波利尼西亚\n" +
            "法属南部领地\n" +
            "G-20\n" +
            "G-7（七国集团）\n" +
            "加蓬\n" +
            "冈比亚\n" +
            "乔治亚州\n" +
            "德意志民主共和国\n" +
            "德国\n" +
            "德国，联邦共和国\n" +
            "加纳\n" +
            "直布罗陀\n" +
            "希腊\n" +
            "格陵兰\n" +
            "格林纳达\n" +
            "瓜德罗普\n" +
            "关岛\n" +
            "危地马拉\n" +
            "几内亚\n" +
            "几内亚比绍\n" +
            "海湾合作委员会 (GCC)\n" +
            "圭亚那\n" +
            "海地\n" +
            "洪都拉斯\n" +
            "中国香港\n" +
            "匈牙利\n" +
            "冰岛\n" +
            "印度\n" +
            "印度尼西亚\n" +
            "伊朗\n" +
            "伊拉克\n" +
            "爱尔兰\n" +
            "以色列\n" +
            "意大利\n" +
            "牙买加\n" +
            "日本\n" +
            "约旦\n" +
            "哈萨克斯坦\n" +
            "肯尼亚\n" +
            "基里巴斯\n" +
            "朝鲜民主主义人民共和国\n" +
            "大韩民国\n" +
            "科威特国\n" +
            "吉尔吉斯共和国\n" +
            "内陆发展中国家\n" +
            "老挝人民民主共和国\n" +
            "拉脱维亚\n" +
            "最不发达国家农业出口国\n" +
            "最不发达国家 制成品出口国\n" +
            "最不发达国家非燃料矿产出口国\n" +
            "最不发达国家石油出口国\n" +
            "最不发达国家\n" +
            "黎巴嫩共和国\n" +
            "莱索托\n" +
            "利比里亚\n" +
            "利比亚\n" +
            "立陶宛\n" +
            "卢森堡\n" +
            "中国澳门\n" +
            "马达加斯加\n" +
            "马拉维\n" +
            "马来西亚\n" +
            "马尔代夫\n" +
            "马里\n" +
            "马耳他\n" +
            "马绍尔群岛\n" +
            "马提尼克岛\n" +
            "毛里塔尼亚\n" +
            "毛里求斯\n" +
            "马约特岛\n" +
            "墨西哥\n" +
            "密克罗尼西亚联邦\n" +
            "中东\n" +
            "摩尔多瓦共和国\n" +
            "蒙古\n" +
            "黑山\n" +
            "蒙特塞拉特\n" +
            "摩洛哥\n" +
            "莫桑比克\n" +
            "缅甸\n" +
            "纳米比亚\n" +
            "瑙鲁\n" +
            "尼泊尔\n" +
            "荷兰\n" +
            "荷属安的列斯\n" +
            "荷属安的列斯群岛（包括阿鲁巴）\n" +
            "新喀里多尼亚\n" +
            "新西兰\n" +
            "尼加拉瓜\n" +
            "尼日尔\n" +
            "尼日利亚\n" +
            "纽埃\n" +
            "非欧盟东南欧\n" +
            "非欧盟西欧\n" +
            "北美\n" +
            "北美自由贸易协定 (NAFTA)\n" +
            "北马其顿\n" +
            "北马里亚纳群岛\n" +
            "挪威\n" +
            "OIC（伊斯兰合作组织）\n" +
            "阿曼\n" +
            "欧佩克（石油输出国组织）\n" +
            "其他非洲\n" +
            "亚洲其他地区\n" +
            "其他独联体\n" +
            "太平洋联盟\n" +
            "巴基斯坦\n" +
            "帕劳\n" +
            "巴勒斯坦\n" +
            "巴拿马\n" +
            "巴布亚新几内亚\n" +
            "巴拉圭\n" +
            "秘鲁\n" +
            "菲律宾\n" +
            "波兰\n" +
            "葡萄牙\n" +
            "卡塔尔\n" +
            "团圆\n" +
            "罗马尼亚\n" +
            "俄罗斯联邦\n" +
            "卢旺达\n" +
            "圣基茨和尼维斯\n" +
            "圣卢西亚\n" +
            "圣马丁\n" +
            "圣皮埃尔和密克隆\n" +
            "圣文森特和格林纳丁斯\n" +
            "萨摩亚\n" +
            "圣多美和普林西比\n" +
            "沙特阿拉伯王国\n" +
            "塞内加尔\n" +
            "塞尔维亚\n" +
            "塞尔维亚和黑山\n" +
            "塞舌尔\n" +
            "塞拉利昂\n" +
            "新加坡\n" +
            "六位东亚贸易商\n" +
            "斯洛伐克共和国\n" +
            "斯洛文尼亚\n" +
            "所罗门群岛\n" +
            "索马里\n" +
            "南非\n" +
            "南美洲和中美洲及加勒比地区\n" +
            "南亚区域合作联盟 (SAARC)\n" +
            "南苏丹\n" +
            "南部非洲发展共同体 (SADC)\n" +
            "南方共同市场（南方共同市场）\n" +
            "南方共同市场 (MERCOSUR)，不包括委内瑞拉、玻利瓦尔共和国\n" +
            "西班牙\n" +
            "斯里兰卡\n" +
            "苏丹\n" +
            "苏里南\n" +
            "斯威士兰\n" +
            "瑞典\n" +
            "瑞士\n" +
            "阿拉伯叙利亚共和国\n" +
            "\n" +
            "塔吉克斯坦\n" +
            "坦桑尼亚\n" +
            "泰国\n" +
            "前南斯拉夫的马其顿共和国\n" +
            "东帝汶\n" +
            "多哥\n" +
            "托克劳\n" +
            "汤加\n" +
            "特立尼达和多巴哥\n" +
            "突尼斯\n" +
            "火鸡\n" +
            "土库曼斯坦\n" +
            "特克斯和凯科斯群岛\n" +
            "图瓦卢\n" +
            "苏联\n" +
            "乌干达\n" +
            "乌克兰\n" +
            "阿拉伯联合酋长国\n" +
            "英国\n" +
            "美国\n" +
            "科索沃特派团/科索沃\n" +
            "乌拉圭\n" +
            "乌兹别克斯坦\n" +
            "瓦努阿图\n" +
            "委内瑞拉\n" +
            "委内瑞拉玻利瓦尔共和国\n" +
            "越南\n" +
            "英属维尔京群岛\n" +
            "瓦利斯和富图纳群岛\n" +
            "西非经济货币联盟 (WAEMU)\n" +
            "西非经济共同体 (ECOWAS)\n" +
            "世贸组织成员\n" +
            "世贸组织观察员政府\n" +
            "也门\n" +
            "也门，阿拉伯共和国\n" +
            "也门，人民民主共和国\n" +
            "南斯拉夫社会主义联邦共和国\n" +
            "赞比亚\n" +
            "津巴布韦\n";

    @Override
    public JSONObject getDataInfo(String id) {
        JSONObject wtoJson = new JSONObject();
        List<String> indicators = getIndicators();
        int i = 0;
        List<WtoBean> allBeans = new ArrayList<>(12);
        for (String indicator : indicators) {
            log.info("size:#{}" + "now:#{}", indicators.size(), i++);
            if (i > 50) {
                log.info("慢了");
                continue;
            }
            List<WtoBean> wtoSourceList = getWtoSourceList(indicator, id);
            allBeans.addAll(wtoSourceList);
        }
        Map<String, List<WtoBean>> collect = allBeans.stream().collect(Collectors.groupingBy(WtoBean::getReportingEconomy));
        for (String reportingEconomy : collect.keySet()) {
            List<WtoBean> singlebeans = collect.get(reportingEconomy);
            assembleJSON(wtoJson, singlebeans);
        }
        try {
            writeFile("C:\\Users\\PandaIP\\Desktop\\file\\vital.json", wtoJson.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wtoJson;
    }

    public void assembleJSON(JSONObject wtoJson, List<WtoBean> wtoBeans) {
//        JSONObject data1 = new JSONObject();
//        JSONObject data1Detail = new JSONObject();
//        JSONObject idc1WithAttr = new JSONObject();
//        JSONObject idc1WithData = new JSONObject();
        //巴西
        Map<String, List<WtoBean>> collect = wtoBeans.stream().collect(Collectors.groupingBy(WtoBean::getIndicator));
        JSONObject treeJsonData = new JSONObject();
        for (String indicator : collect.keySet()) {
            List<WtoBean> groupByIndcatorBeans = collect.get(indicator);
            JSONObject attrB = new JSONObject();
            String enIndicator = (String)getMap().get(indicator);
            attrB.put("indicator", enIndicator);
            JSONObject dataB = new JSONObject();
            for (WtoBean wtoBean : groupByIndcatorBeans) {
                dataB.put(String.valueOf(wtoBean.getYear()),wtoBean.getValue());
            }
            JSONObject resultB = UtilHandle.setNodeData(attrB, dataB);
            treeJsonData.put(enIndicator, resultB);
        }
        wtoJson.put(String.valueOf(getCounrtyMap().get(wtoBeans.get(0).getReportingEconomy())), treeJsonData);
//        log.info("wtoJsonSize: {}", wtoJson.toJSONString());
    }
//        for (WtoBean bean : wtoBeans) {
//
//            //巴西-idcAttribute
//            JSONObject IDCAttr1 = new JSONObject();
//            IDCAttr1.put("reportingEconomy", bean.getReportingEconomy());
//            data1.put(Constants.IDC_ATTRIBUTE, IDCAttr1);
//
//            // 巴西-通货-attribute
//            idc1WithAttr.put("indicator", bean.getIndicator());
//            data1Detail.put(Constants.IDC_ATTRIBUTE, idc1WithAttr);
//            // 巴西-通货-data
//            idc1WithData.put(String.valueOf(bean.getYear()), bean.getValue());
//            data1Detail.put(Constants.IDC_DATA, idc1WithData);
//            //
//            data1.put(bean.getIndicator(), data1Detail);
//            wtoJson.put(bean.getReportingEconomy(), data1);
//        }


    @Override
    public JSONObject getDataInfo() {
        JSONObject wtoJson = new JSONObject();
        List<String> indicators = getIndicators();
        JSONObject data1 = new JSONObject();
        int i = 0;
        List<WtoBean> wtoBeans = new ArrayList<>(12);
        for (String indicator : indicators) {
            log.info("size:#{}" + "now:#{}", indicators.size(), i++);
            wtoBeans.addAll(getWtoSourceList(indicator, ""));
        }
//        log.info("已获取所有待解析Bean,size:{}", wtoBeans.size());
//        log.info("已获取所有待解析Beans :{}", JSONObject.toJSONString(wtoBeans));
//
//        log.info("wtoJSONBeans:{}", JSONObject.toJSONString(wtoBeans));
        wtoBeans.forEach(bean -> {
            JSONObject data1Detail = new JSONObject();
            JSONObject idc1WithData = new JSONObject();
            JSONObject idc1WithAttr = new JSONObject();
            //巴西
            //巴西-idcAttribute
            JSONObject IDCAttr1 = new JSONObject();
            IDCAttr1.put("reportingEconomy", bean.getReportingEconomy());
            data1.put(Constants.IDC_ATTRIBUTE, IDCAttr1);

            // 巴西-通货-attribute
            idc1WithAttr.put("indicator", bean.getIndicator());
            data1Detail.put(Constants.IDC_ATTRIBUTE, idc1WithAttr);
            // 巴西-通货-data
            idc1WithData.put(String.valueOf(bean.getYear()), bean.getValue());
            data1Detail.put(Constants.IDC_DATA, idc1WithData);
            //
            data1.put(bean.getIndicator(), data1Detail);
            wtoJson.put(bean.getReportingEconomy(), data1);

//            log.info(wtoJson.toJSONString());
//            try {
//                writeFile("C:\\Users\\PandaIP\\Desktop\\file\\" + bean.getReportingEconomy() + ".json", wtoJson.toJSONString());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        });
        try {
            writeFile("C:\\Users\\PandaIP\\Desktop\\file\\vital.json", wtoJson.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wtoJson;
    }

    public List<String> getReporters() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("subscription-key", KEY);
        return JSONObject.parseArray(HttpUtil.get(REPORTERS_URL, paramMap), ReporterBean.class).stream().map(ReporterBean::getCode).collect(Collectors.toList());
    }
    public List<String> getReporterNames() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("subscription-key", KEY);
        return JSONObject.parseArray(HttpUtil.get(REPORTERS_URL, paramMap), ReporterBean.class).stream().map(ReporterBean::getName).collect(Collectors.toList());
    }

    public List<WtoBean> getWtoSourceList(String IndicatorCode, String id) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("i", IndicatorCode);
        paramMap.put("r", id == null ? "all" : id);
        paramMap.put("max", "1000000");
        paramMap.put("fmt", "json");
        paramMap.put("mode", "full");
        paramMap.put("lang", 1);
        paramMap.put("meta", false);
        paramMap.put("subscription-key", KEY);
        String response = null;
        try {
            response = HttpUtil.get(DATA_URL, paramMap,100000);
        } catch (Exception e) {
            return new ArrayList<>();
        }

        JSONArray dataset = JSONObject.parseObject(response).getJSONArray("Dataset");
        if (dataset == null) {
            return null;
        }
        return dataset.toJavaList(WtoBean.class);
    }

    public List<String> getIndicators() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("subscription-key", KEY);
        String response = HttpUtil.get(INDICATORS_URL, paramMap);
        return JSONObject.parseArray(response, IndicatorBean.class).stream().map(IndicatorBean::getCode).collect(Collectors.toList());
    }
    public List<String> getIndicatorNames() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("subscription-key", KEY);
        String response = HttpUtil.get(INDICATORS_URL, paramMap);
        return JSONObject.parseArray(response, IndicatorBean.class).stream().map(IndicatorBean::getName).collect(Collectors.toList());
    }

    public static void writeFile(String filePath, String sets)
            throws IOException {
//        new File(filePath).delete();
        FileWriter fw = new FileWriter(filePath);
        PrintWriter out = new PrintWriter(fw);
        out.write(sets);
        out.println();
        fw.close();
        out.close();
    }


    @Test
    public void testGetAll() throws UnsupportedEncodingException {
        JSONObject data = getDataInfo(null);
    }

    private Map getMap() {
        Map map = new HashMap();
        String[] split = enStr.split("#");
        String[] split2 = zhResult.split("#");
        for (int i = 0; i < split.length; i++) {
            map.put(split[i], split2[i]);
        }
        //System.out.println(map.toString());
        return map;
    }
    public Map getCounrtyMap() {
        List<String> counrtys = getReporterNames();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < counrtys.size(); i++) {
            sb.append(counrtys.get(i));
            sb.append("\n");
    }
        String s = sb.toString();
        //System.out.println(s);
//        String APP_ID = "20220310001118799";
//        String SECURITY_KEY = "_I40DqQDM5E0quByUAlu";
//        TransApi api = new TransApi(APP_ID, SECURITY_KEY);
//        String transResult = api.getTransResult(s, "auto", "zh");
//        String dst = JSONObject.parseObject(transResult, TransVo.class).getTrans_result().get(0).getDst();
//        String src = JSONObject.parseObject(transResult, TransVo.class).getTrans_result().get(0).getSrc();
        String[] split1 = enCounry.split("\n");
        String[] split2 = zhCountry.split("\n");
        //System.out.println(split2.length);
        //System.out.println(split1.length);
        Map map = new HashMap();
        for (int i = 0; i < split1.length; i++) {
            map.put(split1[i], split2[i]);
        }
       return map;
    }
    //
    @Test
    public void testGet10() {
        String s = "156," + "840," + "392," + "408," + "410," + "643," + "826," + "040," + "804," + "923," + "918," + "928";
//        JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.DisableCircularReferenceDetect.getMask();
//        String s = "923";
        for (String id : Arrays.asList(s)) {
            getDataInfo(id);
        }
    }


}
