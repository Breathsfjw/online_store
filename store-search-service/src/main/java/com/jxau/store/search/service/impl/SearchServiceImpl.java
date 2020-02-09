package com.jxau.store.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.jxau.store.beans.PmsSearchParam;
import com.jxau.store.beans.PmsSearchSkuInfo;
import com.jxau.store.beans.PmsSkuAttrValue;
import com.jxau.store.service.SearchService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    JestClient jestClient;

//    @Override
//    public List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam) {
////        String dslstr = getSearchDsl(pmsSearchParam);
//////        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();
//////
//////        Search build = new Search.Builder(dslstr).addIndex("store").addType("PmsSkuInfo").build();
////////        Search build = new Search.Builder("{\n" +
////////                "  \"query\": {\n" +
////////                "    \"bool\": {\n" +
////////                "      \"filter\": [\n" +
////////                "        {\"terms\":{\"skuAttrValueList.valueId\": [\"39\",\"40\",\"41\"]}},\n" +
////////                "          {\n" +
////////                "        \"term\": {\n" +
////////                "          \"skuAttrValueList.valueId\": \"39\"\n" +
////////                "        }}, {\"term\": {\n" +
////////                "          \"skuAttrValueList.valueId\": \"43\"\n" +
////////                "        }\n" +
////////                "      }],\"must\": [\n" +
////////                "        {\"match\": {\n" +
////////                "         \"skuName\": \"华为\"\n" +
////////                "        }}\n" +
////////                "      ]\n" +
////////                "    }\n" +
////////                "  }\n" +
////////                "}").addIndex("store").addType("PmsSkuInfo").build();
//////        SearchResult execute = null;
//////        try {
//////            execute = jestClient.execute(build);
//////        } catch (IOException e) {
//////            e.printStackTrace();
//////        }
//////        List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = execute.getHits(PmsSearchSkuInfo.class);
//////        for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit :
//////                hits) {
//////            PmsSearchSkuInfo source = hit.source;
//////            Map<String, List<String>> highlight = hit.highlight;
//////            String skuName = highlight.get("skuName").get(0);
//////            source.setSkuName(skuName);
//////            pmsSearchSkuInfos.add(source);
//////        }
//////        return pmsSearchSkuInfos;
//        String dslStr = getSearchDsl(pmsSearchParam);
//        System.err.println(dslStr);
//        // 用api执行复杂查询
//        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();
//        Search search = new Search.Builder(dslStr).addIndex("store").addType("PmsSkuInfo").build();
//        SearchResult execute = null;
//        try {
//            execute = jestClient.execute(search);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = execute.getHits(PmsSearchSkuInfo.class);
//        for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
//            PmsSearchSkuInfo source = hit.source;
//            Map<String, List<String>> highlight = hit.highlight;
//            String skuName = highlight.get("skuName").get(0);
//            source.setSkuName(skuName);
//            pmsSearchSkuInfos.add(source);
//        }
//
//        System.out.println(pmsSearchSkuInfos.size());
//        return pmsSearchSkuInfos;
//    }
//
//    private String getSearchDsl(PmsSearchParam pmsSearchParam) {
////        String keyword = pmsSearchParam.getKeyword();
////        String catalog3Id = pmsSearchParam.getCatalog3Id();
////        List<PmsSkuAttrValue> skuAttrValueList = pmsSearchParam.getSkuAttrValueList();
////
////        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
////
////        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
////        if (StringUtils.isNotBlank(catalog3Id)) {
////
////            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", catalog3Id);
////            boolQueryBuilder.filter(termQueryBuilder);
////
////        }
////        if (skuAttrValueList != null) {
////            for (PmsSkuAttrValue pmsSkuAttrValue :
////                    skuAttrValueList) {
////                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", pmsSkuAttrValue.getValueId());
////                boolQueryBuilder.filter(termQueryBuilder);
////            }
////        }
////
////        if (StringUtils.isNotBlank(keyword)) {
////            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", keyword);
////            boolQueryBuilder.must(matchQueryBuilder);
////        }
////        //高亮
////        HighlightBuilder highlightBuilder = new HighlightBuilder();
////        highlightBuilder.preTags("<span style='color:red;'>");
////        highlightBuilder.field("skuName");
////        highlightBuilder.preTags("</span>");
////        searchSourceBuilder.highlight(highlightBuilder);
////        //sort SortOrder 枚举类
////        searchSourceBuilder.sort("id", SortOrder.DESC);
////        //query
////        searchSourceBuilder.query(boolQueryBuilder);
////    //from
////        searchSourceBuilder.from(0);
////        //size 控制数据量
////        searchSourceBuilder.size(200);
////        return searchSourceBuilder.toString();
//        List<PmsSkuAttrValue> skuAttrValueList = pmsSearchParam.getSkuAttrValueList();
//        String keyword = pmsSearchParam.getKeyword();
//        String catalog3Id = pmsSearchParam.getCatalog3Id();
//
//        // jest的dsl工具
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        // bool
//        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
//
//        // filter
//        if(StringUtils.isNotBlank(catalog3Id)){
//            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id",catalog3Id);
//            boolQueryBuilder.filter(termQueryBuilder);
//        }
//        if(skuAttrValueList!=null){
//            for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
//                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId",pmsSkuAttrValue.getValueId());
//                boolQueryBuilder.filter(termQueryBuilder);
//            }
//        }
//
//        // must
//        if(StringUtils.isNotBlank(keyword)){
//            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName",keyword);
//            boolQueryBuilder.must(matchQueryBuilder);
//        }
//
//        // query
//        searchSourceBuilder.query(boolQueryBuilder);
//
//        // highlight
//        HighlightBuilder highlightBuilder = new HighlightBuilder();
//        highlightBuilder.preTags("<span style='color:red;'>");
//        highlightBuilder.field("skuName");
//        highlightBuilder.postTags("</span>");
//        searchSourceBuilder.highlight(highlightBuilder);
//        // sort
//        searchSourceBuilder.sort("id",SortOrder.DESC);
//        // from
//        searchSourceBuilder.from(0);
//        // size
//        searchSourceBuilder.size(20);
//
//        return searchSourceBuilder.toString();
//    }
public List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam) {
    String dslStr = getSearchDsl(pmsSearchParam);
//    System.err.println(dslStr);
    // 用api执行复杂查询
    List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();
    Search search = new Search.Builder(dslStr).addIndex("store").addType("PmsSkuInfo").build();
    SearchResult execute = null;
    try {
        execute = jestClient.execute(search);
    } catch (IOException e) {
        e.printStackTrace();
    }
    List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = execute.getHits(PmsSearchSkuInfo.class);
    for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
        PmsSearchSkuInfo source = hit.source;
            Map<String, List<String>> highlight = hit.highlight;
            if (highlight!=null){
                String skuName = highlight.get("skuName").get(0);
                source.setSkuName(skuName);
            }
        pmsSearchSkuInfos.add(source);
    }

//    System.out.println(pmsSearchSkuInfos.size());
    return pmsSearchSkuInfos;
}

    private String getSearchDsl(PmsSearchParam pmsSearchParam) {

        List<PmsSkuAttrValue> skuAttrValueList = pmsSearchParam.getSkuAttrValueList();
        String keyword = pmsSearchParam.getKeyword();
        String catalog3Id = pmsSearchParam.getCatalog3Id();

        // jest的dsl工具
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // bool
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        // filter
        if(StringUtils.isNotBlank(catalog3Id)){
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id",catalog3Id);
            boolQueryBuilder.filter(termQueryBuilder);
        }
        if(skuAttrValueList!=null){
            for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId",pmsSkuAttrValue.getValueId());
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }

        // must
        if(StringUtils.isNotBlank(keyword)){
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName",keyword);
            boolQueryBuilder.must(matchQueryBuilder);
        }

        // query
        searchSourceBuilder.query(boolQueryBuilder);

        // highlight
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span style='color:red;'>");
        highlightBuilder.field("skuName");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlight(highlightBuilder);
        // sort
        searchSourceBuilder.sort("id",SortOrder.DESC);
        // from
        searchSourceBuilder.from(0);
        // size
        searchSourceBuilder.size(20);

        return searchSourceBuilder.toString();

    }
}
