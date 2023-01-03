package com.zhangyu.esdemo;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.SuggestMode;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.indices.*;
import co.elastic.clients.elasticsearch.sql.QueryResponse;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhangyu.esdemo.domain.Poet;
import lombok.SneakyThrows;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangyu
 * @date 2022/8/24 22:06
 */
@SpringBootTest
public class ESJavaApiTests {
    ElasticsearchClient client = null;
    ElasticsearchTransport transport = null;

    private static final String INDEX_NAME = "index_test_v1";

    /**
     * 建立连接
     */
    @BeforeEach
    void initClient() {
        RestClient restClient = RestClient.builder(
                new HttpHost("localhost", 9200)
        ).build();
        ObjectMapper objectMapper = new ObjectMapper();
        transport = new RestClientTransport(restClient, new JacksonJsonpMapper(objectMapper));
        client = new ElasticsearchClient(transport);
    }

    /**
     * 关闭连接
     */
    // @AfterEach
    // void after() {
    //     client.shutdown();
    // }

    /**
     * 建立索引
     */
    @Test
    void createIndex() throws IOException {
        CreateIndexResponse createIndexResponse = client.indices().create(builder -> builder
                .settings(settingBuilder -> settingBuilder.numberOfShards("1").numberOfReplicas("1"))
                .mappings(mappingsBuilder -> mappingsBuilder
                        .properties("age", propertiesBuilder -> propertiesBuilder.integer(integerNumberPropertyBuilder -> integerNumberPropertyBuilder))
                        .properties("name", propertyBuilder -> propertyBuilder.keyword(keywordPropertyBuilder -> keywordPropertyBuilder))
                        .properties("remarks", propertyBuilder -> propertyBuilder.text(textPropertyBuilder -> textPropertyBuilder.analyzer("ik_max_word").searchAnalyzer("ik_max_word")))
                )
                .index("INDEX_NAME")
        );
        System.out.println(createIndexResponse.acknowledged());
    }

    /**
     * 修改_mappings信息
     * 字段可以新增，已有的字段只能修改字段的search_analyzer属性
     */
    @Test
    void modifyIndex() throws IOException {
        PutMappingResponse putMappingResponse = client.indices().putMapping(typeMappingBuilder -> typeMappingBuilder
                .index("INDEX_NAME")
                // new
                .properties("poems", peopertyBuilder -> peopertyBuilder.text(textPropertyBuilder -> textPropertyBuilder.analyzer("ik_max_word").searchAnalyzer("ik_max_word")))
                // update searchAnalyzer only
                .properties("remarks", propertyBuilder -> propertyBuilder.text(textPropertyBuilder -> textPropertyBuilder.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
        );
        System.out.println(putMappingResponse.acknowledged());
    }

    /**
     * 查询索引列表
     */
    @Test
    void getIndex() throws IOException {
        // 使用*也可以
        // GetIndexResponse all = client.indices().get(builder -> builder.index("_all"));
        // System.out.println(all.result());
        GetIndexResponse getIndexResponse = client.indices().get(builder -> builder.index("INDEX_NAME"));
        System.out.println(getIndexResponse.result());
    }

    /**
     * 文档操作
     * age
     * name
     * poems
     * remarks
     */
    @Test
    void createDoc() throws IOException {
        // Map<String, Object> doc = new HashMap<>();
        // doc.put("age", 30);
        // doc.put("name", "李白");
        // doc.put("poems", "静夜思");
        // doc.put("remarks", "创造了古代浪漫主义文学高峰、歌行体和七绝达到后人难及的高度");
        //
        // CreateResponse INDEX_NAME = client.create(builder -> builder.index("INDEX_NAME").id("1").document(doc));
        // System.out.println(INDEX_NAME.toString());

        Poet poet = new Poet();
        poet.setAge(33)
                .setName("杜甫")
                .setPoems("登高")
                .setRemarks("唐代伟大的现实主义文学作家，唐诗思想艺术的集大成者")
        ;
        CreateResponse index_test_v11 = client.create(builder -> builder.index("INDEX_NAME").id("2").document(poet));

        System.out.println(index_test_v11);

    }

    // 修改文档，只修改设置的字段
    @Test
    void updateDoc() throws IOException {
        Map<String, Object> doc = new HashMap<>();
        doc.put("age", 32);

        UpdateResponse<Map> INDEX_NAME = client.update(builder -> builder.index("INDEX_NAME").id("1").doc(doc), Map.class);
        System.out.println(INDEX_NAME.toString());
    }

    // 新增或修改文档，修改时所有的字段都会覆盖，相当于先删除再新增
    @Test
    void createOrUpdateDoc() throws IOException {
        Map<String, Object> doc = new HashMap<>();
        doc.put("age", 33);
        doc.put("name", "李白2");

        IndexResponse INDEX_NAME = client.index(builder -> builder.index("INDEX_NAME").id("1").document(doc));
        System.out.println(INDEX_NAME);
    }

    // 删除文档
    @Test
    void deleteDoc() throws IOException {
        DeleteResponse INDEX_NAME = client.delete(builder -> builder.index("INDEX_NAME").id("id"));
        System.out.println(INDEX_NAME);
    }

    // 查询所有文档
    @Test
    void selectDoc() throws IOException {
        SearchResponse<Map> INDEX_NAME = client.search(builder -> builder.index("INDEX_NAME"), Map.class);

        System.out.println(INDEX_NAME);
    }

    // 文档的批量操作
    @SneakyThrows
    @Test
    void bulk() {
        List<BulkOperation> list = new ArrayList<>();
        // for (int i = 0; i < 5; i++) {
        //     String id = 10 + i + "";
        //     String id1 = 20 + i + "";
        //     list.add(new BulkOperation.Builder().delete(builder -> builder.index("INDEX_NAME").id(id1)).build());
        //     list.add(new BulkOperation.Builder().delete(builder -> builder.index("INDEX_NAME").id(id)).build());
        // }

        for (int i = 0; i < 5; i++) {
            Map<String, Object> doc = new HashMap<>();
            doc.put("age", 30);
            doc.put("name", "李白" + i);
            doc.put("poems", "静夜思");
            doc.put("remarks", "创造了古代浪漫主义文学高峰、歌行体和七绝达到后人难及的高度");
            String id = 10 + i + "";
            list.add(new BulkOperation.Builder().create(builder -> builder.index("INDEX_NAME").id(id).document(doc)).build());
        }
        for (int i = 0; i < 5; i++) {
            Poet poet = new Poet(31, "杜甫" + i, "登高", "唐代伟大的现实主义文学作家，唐诗思想艺术的集大成者");
            String id = 20 + i + "";
            list.add(new BulkOperation.Builder().create(builder -> builder.index("INDEX_NAME").id(id).document(poet)).build());
        }

        // list.add(new BulkOperation.Builder().delete(builder -> builder.index("INDEX_NAME").id("1")).build());
        // list.add(new BulkOperation.Builder().delete(builder -> builder.index("INDEX_NAME").id("2")).build());

        BulkResponse INDEX_NAME = client.bulk(builder -> builder.index("INDEX_NAME").operations(list));
        System.out.println(INDEX_NAME);
    }

    // 查询单个文档 - get
    @Test
    void getDoc() throws IOException {
        GetResponse<Map> INDEX_NAME = client.get(builder -> builder.index("INDEX_NAME").id("454"), Map.class);
        if (INDEX_NAME.found()) {
            System.out.println(INDEX_NAME.source());
        } else {
            System.out.println("没找到");
        }
    }

    /**
     * term/terms查询,
     * 对输入内容不做分词处理
     *
     * @throws IOException
     */
    @Test
    void termSearch() throws IOException {
        SearchResponse<Map> search = client.search(searchRequestBuilder -> searchRequestBuilder
                        .index("INDEX_NAME")
                        .query(queryBuilder -> queryBuilder
                                .term(termQueryBuilder -> termQueryBuilder
                                        .field("name").value("杜甫0")))
                        .sort(sortOptionsBuilder -> sortOptionsBuilder
                                .field(fieldSortBuilder -> fieldSortBuilder
                                        .field("name").order(SortOrder.Asc)))
                        .source(sourceConfigBuilder -> sourceConfigBuilder
                                .filter(sourceFilterBuilder -> sourceFilterBuilder
                                        .includes("name", "age")))
                        .from(0)
                        .size(10)
                , Map.class);

        System.out.println(search);
        System.out.println("++++++++++++++++++");

        List<FieldValue> words = new ArrayList<>();
        words.add(new FieldValue.Builder().stringValue("李白0").build());
        words.add(new FieldValue.Builder().stringValue("杜甫0").build());
        SearchResponse<Poet> search1 = client.search(searchRequestBuilder -> searchRequestBuilder
                        .index("INDEX_NAME")
                        .query(queryBuilder -> queryBuilder
                                .terms(termsQueryBuilder -> termsQueryBuilder
                                        .field("name").terms(termsQueryField -> termsQueryField.value(words))))
                        .source(sourceConfigBuilder -> sourceConfigBuilder
                                .filter(sourceFilterBuilder -> sourceFilterBuilder
                                        .excludes("age")))
                        .from(0)
                        .size(10)
                , Poet.class);
        System.out.println(search1);
    }

    /**
     * 范围查询
     *
     * @throws IOException
     */
    @Test
    void rangeSearch() throws IOException {
        SearchResponse<Map> age = client.search(searchRequestBuilder -> searchRequestBuilder
                        .index("INDEX_NAME")
                        .query(queryBuilder -> queryBuilder
                                .range(rangeQuerybuilder -> rangeQuerybuilder
                                        .field("age").gte(JsonData.of("20")).lte(JsonData.of("40"))))
                , Map.class);
        System.out.println(age);
    }

    /**
     * 全文查询
     *
     * @throws IOException
     */
    /**
     * match查询
     * 先对搜索词进行分词,分词完毕后再逐个对分词结果进行匹配，因此相比于term的精确搜索，match是分词匹配搜索
     */

    @Test
    void matchSearch() throws IOException {
        SearchResponse<Map> search = client.search(searchRequestBuilder -> searchRequestBuilder
                        .index("INDEX_NAME")
                        .query(queryBuilder -> queryBuilder
                                .match(matchQueryBuilder -> matchQueryBuilder
                                        .field("remarks").query("现实作家")))
                , Map.class);
        System.out.println(search);
    }

    // multi_match 多个字段进行匹配。
    @Test
    void multiMatchSearch() throws IOException {
        SearchResponse<Poet> search = client.search(searchRequestBuilder -> searchRequestBuilder
                        .index("INDEX_NAME")
                        .query(queryBuilder -> queryBuilder
                                .multiMatch(multiMatchQuery -> multiMatchQuery
                                        .fields("poems", "remarks").query("现实作家")))
                , Poet.class);
        System.out.println(search);
    }

    /**
     * match_phrase 紧邻搜索
     * 先对搜索词建立索引，并要求所有分词必须在文档中出现(像不像operator为and的match查询)，
     * 除此之外，还必须满足分词在文档中出现的顺序和搜索词中一致且各搜索词之间必须紧邻，因此match_phrase也可以叫做紧邻搜索。
     */
    @Test
    void matchPhraseSearch() throws IOException {
        SearchResponse<Map> search = client.search(searchRequestBuilder -> searchRequestBuilder
                        .query(queryBuilder -> queryBuilder
                                .matchPhrase(matchPhraseQueryBuilder -> matchPhraseQueryBuilder
                                        .field("remarks")
                                        .query("现实作家")))
                , Map.class);
        System.out.println(search);
    }

    /**
     * matchAll查询
     * 查询所有文档
     *
     * @throws IOException
     */
    @Test
    void matchAllSearch() throws IOException {
        SearchResponse<Map> INDEX_NAME = client.search(searchRequestBuilder -> searchRequestBuilder
                        .index("INDEX_NAME")
                        .query(queryBuilder -> queryBuilder
                                .matchAll(matchAllQueryBuilder -> matchAllQueryBuilder))
                , Map.class);
        System.out.println(INDEX_NAME);
    }

    /**
     * query_string查询
     * query_string 可以同时实现前面几种查询方法。
     *
     * @throws IOException
     */
    @Test
    void queryStringSearch() throws IOException {
        // 类似于match
        SearchResponse<Map> search = null;
        // search = client.search(searchRequestBuilder -> searchRequestBuilder
        //                 .index("INDEX_NAME")
        //                 .query(queryBuilder -> queryBuilder
        //                         .queryString(queryStringQueryBuilder -> queryStringQueryBuilder
        //                                 .defaultField("remarks").query("主义")))
        //         , Map.class);
        // System.out.println(search);
        //
        // // 类似于multi_match
        // search = client.search(searchRequestBuilder -> searchRequestBuilder
        //                 .index("INDEX_NAME")
        //                 .query(querybuilder -> querybuilder
        //                         .queryString(queryStringQueryBuilder -> queryStringQueryBuilder
        //                                 .fields("name", "remarks").query("现实")))
        //         , Map.class);
        // System.out.println(search);

        // 类似于match_phrase，临近查询
        // search = client.search(searchRequestBuilder -> searchRequestBuilder
        //                 .index("INDEX_NAME")
        //                 .query(queryBuilder -> queryBuilder
        //                         .queryString(queryStringQueryBuilder -> queryStringQueryBuilder
        //                                 .defaultField("remarks").query("\"现实主义\"")))
        //         , Map.class);
        // System.out.println(search);

        // 带运算符查询，运算符两边的词不再分词
        // search = client.search(searchRequestBuilder -> searchRequestBuilder
        //                 .index("INDEX_NAME")
        //                 .query(queryBuilder -> queryBuilder
        //                         .queryString(queryStringQueryBuilder -> queryStringQueryBuilder
        //                                 .defaultField("remarks").query("现实 AND 伟大")))
        //         , Map.class);
        // System.out.println(search);
        // 跟上面一样的作用
        // search = client.search(searchRequestBuilder -> searchRequestBuilder
        //                 .index("INDEX_NAME")
        //                 .query(querybuilder -> querybuilder
        //                         .queryString(queryStringQueryBuilder -> queryStringQueryBuilder
        //                                 .defaultField("remarks").query("现实 伟大").defaultOperator(Operator.And)))
        //         , Map.class);
        // System.out.println(search);

        //查询 name 或 success 字段包含"文学"和"伟大"这两个单词，或者包含"李白"这个单词的文档。
        search = client.search(searchRequestBuilder -> searchRequestBuilder
                        .index("INDEX_NAME")
                        .query(queryBuilder -> queryBuilder
                                .queryString(queryStringQueryBuilder -> queryStringQueryBuilder
                                        .fields("name", "remarks").query("(文学 AND 伟大) OR 李白0")))
                , Map.class);
        System.out.println(search);

    }

    /**
     * simple_query_string
     * 类似 query_string，主要区别如下：
     * 1、不支持AND OR NOT ，会当做字符处理；使用 + 代替 AND，| 代替OR，- 代替 NOT
     * 2、会忽略错误的语法
     *
     * @throws IOException
     */
    @Test
    void simpleQueryStringSearch() throws IOException {
        SearchResponse<Map> remarks = client.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .query(queryBuilder -> queryBuilder
                                .simpleQueryString(simpleQueryStringSearchBuilder -> simpleQueryStringSearchBuilder
                                        .fields("remarks").query("文学 + 伟大")))
                , Map.class);
        System.out.println(remarks);
    }

    /**
     * 模糊查询
     *
     * @throws IOException
     */
    @Test
    void fuzzySearch() throws IOException {
        //全文查询时使用模糊参数，先分词再计算模糊选项。
        SearchResponse<Poet> response = client.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .query(queryBuilder -> queryBuilder
                                .match(matchQueryBuilder -> matchQueryBuilder
                                        .field("remarks").query("文字啊").fuzziness("1")))
                , Poet.class);
        System.out.println(response);

        //使用 fuzzy query，对输入不分词，直接计算模糊选项。
        SearchResponse<Poet> response2 = client.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .query(queryBuilder -> queryBuilder
                                .fuzzy(fuzzyQueryBuilder -> fuzzyQueryBuilder
                                        .field("remarks").fuzziness("1").value("理想")))
                , Poet.class);
        System.out.println(response2);
    }

    /**
     * 组合查询
     */
    @Test
    void boolSearch() throws IOException {
        //查询 remarks 包含 “思想” 且 age 在 [20-40] 之间的文档
        SearchResponse<Map> search = client.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .query(queryBuilder -> queryBuilder
                                .bool(boolQueryBuilder -> boolQueryBuilder
                                        .must(queryBuilder1 -> queryBuilder1
                                                .match(matchQueryBuilder -> matchQueryBuilder
                                                        .field("remarks").query("思想"))
                                        )
                                        .must(queryBuilder2 -> queryBuilder2
                                                .range(rangeQueryBuilder -> rangeQueryBuilder
                                                        .field("age")
                                                        .gte(JsonData.of("20"))
                                                        .lte(JsonData.of("40"))
                                                )
                                        )
                                )
                        )
                , Map.class);
        System.out.println(search);

        //过滤出 success 包含 “思想” 且 age 在 [20-40] 之间的文档，不计算得分
        SearchResponse<Poet> response2 = client.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .query(queryBuilder -> queryBuilder
                                .bool(boolQueryBuilder -> boolQueryBuilder
                                        .filter(queryBuilder2 -> queryBuilder2
                                                .match(matchQueryBuilder -> matchQueryBuilder
                                                        .field("remarks").query("思想"))
                                        )
                                        .filter(queryBuilder2 -> queryBuilder2
                                                .range(rangeQueryBuilder -> rangeQueryBuilder
                                                        .field("age").gte(JsonData.of("20")).lt(JsonData.of("40")))
                                        )
                                )
                        )
                , Poet.class);
        System.out.println(response2);
    }

    /**
     * 聚合查询
     *
     * @throws IOException
     */
    @Test
    void aggregationSearch() throws IOException {
        SearchResponse<Map> search = null;
        // 求和
        // search = client.search(searchRequestBuilder -> searchRequestBuilder
        //                 .index(INDEX_NAME)
        //                 .aggregations("age_sum", aggregationBuilder -> aggregationBuilder
        //                         .sum(sumAggregationBuilder -> sumAggregationBuilder
        //                                 .field("age")))
        //         , Map.class);
        // System.out.println(search);

        // 类似 select count distinct(age) from poet-index
        // cardinality 基数
        // search = client.search(searchRequestBuilder -> searchRequestBuilder
        //                 .index(INDEX_NAME)
        //                 .aggregations("age_count", aggregationBuilder -> aggregationBuilder
        //                         .cardinality(cardinalityAggregationBuilder -> cardinalityAggregationBuilder
        //                                 .field("age")))
        //         , Map.class);
        // System.out.println(search);

        // 数量、最大、最小、平均、求和
        // "aggregations":{"stats#age_stats":{"count":10,"min":30.0,"max":31.0,"avg":30.5,"sum":305.0}}}
        // search = client.search(searchRequestBuilder -> searchRequestBuilder
        //                 .index(INDEX_NAME)
        //                 .aggregations("age_stats", aggregationBuilder -> aggregationBuilder
        //                         .stats(statsAggregationBuilder -> statsAggregationBuilder
        //                                 .field("age")))
        //         , Map.class);
        // System.out.println(search);

        //select name,count(*) from poet-index group by name
        // search = client.search(searchRequestBuilder -> searchRequestBuilder
        //                 .index(INDEX_NAME)
        //                 .aggregations("name_terms", aggregationBuilder -> aggregationBuilder
        //                         .terms(termsAggregationBuilder -> termsAggregationBuilder
        //                                 .field("name")))
        //         , Map.class);
        // System.out.println(search);

        // select name,age,count(*) from poet-index group by name,age
        // search = client.search(searchRequestBuilder -> searchRequestBuilder
        //                 .index(INDEX_NAME)
        //                 .aggregations("name_term", aggregationBuilder -> aggregationBuilder
        //                         .terms(termsAggregationBuilder -> termsAggregationBuilder
        //                                 .field("name"))
        //                         .aggregations("age_term", aggregationBuilder1 -> aggregationBuilder1
        //                                 .terms(termsAggregationBuilder -> termsAggregationBuilder
        //                                         .field("age")))
        //                 )
        //
        //         , Map.class);
        // System.out.println(search);

        // 类似 select avg(age) from poet-index where name='李白'
        search = client.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .query(queryBuilder -> queryBuilder
                                .bool(boolQueryBuilder -> boolQueryBuilder
                                        .filter(queryBuilder1 -> queryBuilder1
                                                .term(termQueryBuilder -> termQueryBuilder
                                                        .field("name").value("李白0"))))
                        )
                        .aggregations("ave_age", aggregationBuilder -> aggregationBuilder
                                .avg(averageAggregationBuilder -> averageAggregationBuilder
                                        .field("age"))
                        )
                , Map.class);
        System.out.println(search);
    }

    /**
     * 推荐搜索
     *
     * @throws IOException
     */
    @Test
    void suggestSearch() throws IOException {
        SearchResponse<Map> search = client.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .suggest(suggesterBuilder -> suggesterBuilder
                                .suggesters("remarks_suggest", fieldSuggesterBuilder -> fieldSuggesterBuilder
                                        .text("思考")
                                        .term(termSuggesterBuilder -> termSuggesterBuilder
                                                .field("remarks")
                                                .suggestMode(SuggestMode.Always)
                                                .minWordLength(2))
                                ))
                , Map.class);
        System.out.println(search);
        // todo (zhangyu, 2022-08-28, 21:41:43) : co.elastic.clients.json.JsonpMappingException: Error deserializing co.elastic.clients.elasticsearch.core.search.TermSuggest: co.elastic.clients.json.UnexpectedJsonEventException: Unexpected JSON event 'START_ARRAY' instead of '[START_OBJECT, KEY_NAME]' (JSON path: suggest['term#success_suggest'][0].options) (line no=1, column no=247, offset=-1)
    }

    /**
     * 高亮显示
     */
    @Test
    void highlightSearch() throws IOException {
        SearchResponse<Map> search = client.search(searchRequestBuilder -> searchRequestBuilder
                        .index(INDEX_NAME)
                        .query(queryBuilder -> queryBuilder
                                .match(matchQueryBuilder -> matchQueryBuilder
                                        .field("remarks").query("思想"))
                        )
                        .highlight(highlightBuilder -> highlightBuilder
                                .preTags("<span color='red'>")
                                .postTags("</span>")
                                .fields("remarks", highlightFieldBuilder -> highlightFieldBuilder))
                , Map.class);
        System.out.println(search);
    }

    @Test
    void highlightSearch1() throws IOException {
        SearchResponse<Map> search = client.search(searchRequestBuilder -> searchRequestBuilder
                        .index("idx")
                        .query(queryBuilder -> queryBuilder
                                .match(matchQueryBuilder -> matchQueryBuilder
                                        .field("content").query("苹果"))
                        )
                        .highlight(highlightBuilder -> highlightBuilder
                                .preTags("<span color='red'>")
                                .postTags("</span>")
                                .fields("content", highlightFieldBuilder -> highlightFieldBuilder))
                , Map.class);
        System.out.println(search);
    }


    // 查看所有的分词
    @Test
    void getAnalyzer() throws IOException {
        AnalyzeResponse analyze = client
                .indices()
                .analyze(analyzeRequestBuilder -> analyzeRequestBuilder
                        // 使用该字段的分词器，
                        // .index("INDEX_NAME")
                        // .field("remarks")
                        // 也可以自己手动指定分词器
                        .analyzer("ik_smart")
                        .text("创造了古代浪漫主义文学高峰、歌行体和七绝达到后人难及的高度")
                );
        System.out.println(analyze);
    }

    // todo (zhangyu, 2022-08-26, 09:34:55) : 这个有bug
    @Test
    public void searchSql() throws IOException {
        QueryResponse response = client.sql().query(builder -> builder
                .format("json").query("SELECT * FROM \"INDEX_NAME\""));
        System.out.println(response);
    }

    @Test
    void test() throws IOException {
        GetMappingResponse mapping = client.indices().getMapping(getMappingRequestBuilder -> getMappingRequestBuilder.index("INDEX_NAME"));
        System.out.println(mapping);
    }
}
