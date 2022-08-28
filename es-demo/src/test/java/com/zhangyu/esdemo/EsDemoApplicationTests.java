package com.zhangyu.esdemo;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.elasticsearch.indices.GetIndexResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhangyu.esdemo.domain.Book;
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

@SpringBootTest
class EsDemoApplicationTests {
    ElasticsearchClient client = null;
    ElasticsearchTransport transport = null;

    @BeforeEach
    void setUp() {
        // Create the low-level client
        RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200)).build();
        // Create the transport with a Jackson mapper
        transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        // And create the API client
        this.client = new ElasticsearchClient(transport);
    }

    void setUp1() {
        RestClient restClient = RestClient.builder(
                new HttpHost("localhost", 9200),
                new HttpHost("localhost", 9201),
                new HttpHost("localhost", 9202)
        ).build();

        ObjectMapper objectMapper = new ObjectMapper();
        transport = new RestClientTransport(restClient, new JacksonJsonpMapper(objectMapper));
        client = new ElasticsearchClient(transport);
    }

    /**
     * ------------index-------------
     *
     * @throws IOException
     */
    // create index
    @Test
    void createIndex() throws IOException {
        CreateIndexResponse bookshelf = client.indices()
                .create(createIndexBuilder -> createIndexBuilder
                        .index("bookshelf"));

        System.out.println(bookshelf.acknowledged());
    }

    // select index
    @Test
    void selectIndex() throws IOException {
        GetIndexResponse index = client.indices().get(getIndexBuilder -> getIndexBuilder.index("bookshelf"));
        System.out.println(index.result().keySet());
    }

    // delete index
    @Test
    void deleteIndex() throws IOException {
        DeleteIndexResponse bookshelf = client.indices().delete(deleteIndexBuilder -> deleteIndexBuilder.index("bookshelf"));

        System.out.println(bookshelf.acknowledged());

    }

    /**
     * -------------doc---------------
     */
    // create doc
    @Test
    void createDoc() throws IOException {
        Book book = new Book();
        book.setBName("Python学习手册")
                .setBAuthor("Mark Lutz")
                .setBPress("机械工业出版社")
                .setBRemark("本书将帮助你使用Python编写出高质量、高效的并且易于与其他语言和工具集成的代码。本书根据Python专家Mark Lutz的著名培训课程编写而成，是易于掌握和自学的Python教程。本书每一章都对Python语言的关键内容做单独讲解，并且配有章后习题、编程练习及详尽的解答，还配有大量注释的示例以及图表，便于你学习新的技能并巩固加深自己的理解。第5版基于Python2.7和3.3版本，同时也适用于其他Python版本。无论你是编程新手还是其他编程语言的资深开发者，本书都会是你学习Python的理想选择。")
        ;

        CreateResponse bookshelf = client.create(createBuilder -> createBuilder.index("bookshelf").id("1001").document(book));

        System.out.println(bookshelf.result());
    }

    // update doc
    @Test
    void updateDoc() throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("bname", "Python学习手册（第五版）上册");

        UpdateResponse<Book> bookshelf = client.update(updateBuilder -> updateBuilder
                        .index("bookshelf")
                        .id("1001")
                        .doc(map),
                Book.class
        );

        System.out.println(bookshelf.result());
    }

    //get Doc
    @Test
    void getDoc() throws IOException {
        GetResponse<Book> bookshelf = client.get(getBuilder -> getBuilder
                .index("bookshelf")
                .id("1001"), Book.class);

        System.out.println(bookshelf.source());
    }

    // del Doc
    @Test
    void delDoc() throws IOException {
        DeleteResponse bookshelf = client
                .delete(deleteBuilder -> deleteBuilder
                        .index("bookshelf")
                        .id("1001"));
        System.out.println(bookshelf.result());
    }

    /**
     * -----------advanced-----------
     */
    // 批量添加文档
    @Test
    void batchAddDoc() throws IOException {
        List<BulkOperation> bulkOperations = new ArrayList<>();
        bulkOperations.add(new BulkOperation.Builder().create(createBuilder -> createBuilder.index("bookshelf").id("1002").document(new Book("Python学习手册（第五版）下册", "Mark Lutz", "机械工业出版社", "下册主要介绍类跟异常机制"))).build());
        bulkOperations.add(new BulkOperation.Builder().create(createBuilder -> createBuilder.index("bookshelf").id("1003").document(new Book("红楼梦", "曹雪芹", "人民日报出版社", "四大名著之一"))).build());
        bulkOperations.add(new BulkOperation.Builder().create(createBuilder -> createBuilder.index("bookshelf").id("1004").document(new Book("西游记", "吴承恩", "出版社2", "四大名著之一，师徒四人历经九九八十一难取得真经的故事"))).build());

        BulkResponse bookshelf = client.bulk(builder -> builder.index("bookshelf").operations(bulkOperations));

        System.out.println(bookshelf.took());
        System.out.println(bookshelf.items());


        // 批量删除
        // bulkOperations.add(new BulkOperation.Builder().delete(builder -> builder.index("bookshelf").id("1002")).build());
    }

    // 分页查询
    @Test
    void pageSearch() throws IOException {
        SearchResponse<Book> bookshelf = client.search(builder -> builder
                        .index("bookshelf")
                        .query(querybuilder -> querybuilder
                                .matchAll(m -> m)// todo (zhangyu, 2022-08-23, 09:36:57) : m -> m 是啥意思
                        )
                        .from(1) // from代表的是数据开始的下标
                        .size(2) // size代表的是每次查询需要获取到的文档数量
                , Book.class);

        System.out.println(bookshelf.took());
        System.out.println(bookshelf.hits().total().value());
    }

    // 查询排序、过滤字段
    @Test
    void sortedSearch() throws IOException {
        SearchResponse<Book> search = client.search(builder -> builder
                        .index("bookshelf")
                        .query(q -> q
                                .matchAll(m -> m)
                        )
                        // 使用sort方法传入排序参数，需要传入field名称以及排序方式，如ASC、DESC
                        .sort(sort -> sort
                                .field(f -> f
                                        .field("_source")
                                        .order(SortOrder.Desc)
                                )
                        )
                // 过滤字段同样使用source传入参数。使用includes和excludes来标记白名单或黑名单模式，其中includes代表白名单，只返回指定的字段。excludes代表黑名单，不返回指定的字段。
                // .source(source -> source
                //         .filter(f -> f
                //                 .includes("bname")
                //                 // .excludes("")
                //         )
                // )
                , Book.class);

        System.out.println(search.hits().hits());
    }

}
