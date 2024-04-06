package com.flyingpig.cloudmusic.util;

import com.flyingpig.cloudmusic.dataobject.es.MusicDoc;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ElasticSearchUtil {
    @Autowired
    private RestHighLevelClient client;

    public void importDocument(String indexName, String documentId, String documentJson) throws IOException {
        IndexRequest request = new IndexRequest(indexName)
                .id(documentId)
                .source(documentJson, XContentType.JSON);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println("Document imported successfully. Response: " + response);
    }

    // 删除索引的方法
    public void deleteIndex(String indexName) throws IOException {
        GetIndexRequest existsRequest = new GetIndexRequest().indices(indexName);
        boolean indexExists = client.indices().exists(existsRequest, RequestOptions.DEFAULT);

        if (indexExists) {
            DeleteIndexRequest request = new DeleteIndexRequest(indexName);
            AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
            if (response.isAcknowledged()) {
                System.out.println("Index " + indexName + " deleted successfully.");
            } else {
                System.out.println("Failed to delete index " + indexName);
            }
        } else {
            System.out.println("Index " + indexName + " does not exist.");
        }
    }



    // 创建索引的方法
    public void createIndex(String indexName) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(indexName);

        // 设置索引的配置
        request.settings(Settings.builder()
                .put("index.number_of_shards", 3)
                .put("index.number_of_replicas", 2)
        );

        // 添加映射或设置索引的分析器等
        // request.mapping("_doc", "field1", "type=text");

        // 或者从外部提供索引的映射文件
        // request.source(new FileInputStream("path/to/mapping.json"), XContentType.JSON);

        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        if (response.isAcknowledged()) {
            System.out.println("Index " + indexName + " created successfully.");
        } else {
            System.out.println("Failed to create index " + indexName);
        }
    }


        public List<MusicDoc> searchMusicByKeyword(String keyword) throws IOException {
        SearchRequest searchRequest = new SearchRequest("music"); // 指定索引名称
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.multiMatchQuery(keyword, "name"));
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        List<MusicDoc> musicDocs = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            MusicDoc musicDoc = MusicDoc.fromSearchHit(hit);
            musicDocs.add(musicDoc);
        }
        return musicDocs;
    }


}
