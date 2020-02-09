package com.jxau.store.search;

import com.alibaba.dubbo.config.annotation.Reference;
import com.jxau.store.beans.PmsSearchSkuInfo;
import com.jxau.store.beans.PmsSkuInfo;
import com.jxau.store.service.SkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StoreSearchServiceApplicationTests {
    {
    }

    @Reference
    SkuService skuService;
    @Autowired
    JestClient jestClient;

    @Test
    public void contextLoads() throws IOException {
        put();
    }

    private void searchElatic() throws IOException {
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId","43");
        boolQueryBuilder.filter(termQueryBuilder);
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName","华为");
        boolQueryBuilder.must(matchQueryBuilder);
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(200);
        searchSourceBuilder.highlight(null);
        Search build = new Search.Builder(searchSourceBuilder.toString()).addIndex("store").addType("PmsSkuInfo").build();
//        Search build = new Search.Builder("{\n" +
//                "  \"query\": {\n" +
//                "    \"bool\": {\n" +
//                "      \"filter\": [\n" +
//                "        {\"terms\":{\"skuAttrValueList.valueId\": [\"39\",\"40\",\"41\"]}},\n" +
//                "          {\n" +
//                "        \"term\": {\n" +
//                "          \"skuAttrValueList.valueId\": \"39\"\n" +
//                "        }}, {\"term\": {\n" +
//                "          \"skuAttrValueList.valueId\": \"43\"\n" +
//                "        }\n" +
//                "      }],\"must\": [\n" +
//                "        {\"match\": {\n" +
//                "         \"skuName\": \"华为\"\n" +
//                "        }}\n" +
//                "      ]\n" +
//                "    }\n" +
//                "  }\n" +
//                "}").addIndex("store").addType("PmsSkuInfo").build();
        SearchResult execute = jestClient.execute(build);
        List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = execute.getHits(PmsSearchSkuInfo.class);
        for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit :
                hits) {
            PmsSearchSkuInfo source = hit.source;
            pmsSearchSkuInfos.add(source);
        }
        System.out.print(pmsSearchSkuInfos.size());
    }

    public void put() throws IOException {
        List<PmsSkuInfo> pmsSkuInfos = new ArrayList<>();
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();
        pmsSkuInfos = skuService.getAllSku();
        for (PmsSkuInfo pmsSkuInfo :
                pmsSkuInfos) {
            PmsSearchSkuInfo pmsSearchSkuInfo = new PmsSearchSkuInfo();
            BeanUtils.copyProperties(pmsSkuInfo, pmsSearchSkuInfo);
            pmsSearchSkuInfo.setId(Long.parseLong(pmsSkuInfo.getId()));
            pmsSearchSkuInfos.add(pmsSearchSkuInfo);
        }
        for (PmsSearchSkuInfo pmsSearchSkuInfo :
                pmsSearchSkuInfos) {
            Index builder = new Index.Builder(pmsSearchSkuInfo).index("store").type("PmsSkuInfo").id(pmsSearchSkuInfo.getId() + "").build();
            jestClient.execute(builder);
        }
    }
}
