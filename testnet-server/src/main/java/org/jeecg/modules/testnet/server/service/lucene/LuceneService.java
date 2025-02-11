package org.jeecg.modules.testnet.server.service.lucene;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.SneakyThrows;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LuceneService {

    @Autowired
    @Qualifier("logDirectory")
    private Directory logDirectory;

    @Autowired
    @Qualifier("webDirectory")
    private Directory webDirectory;

    @Autowired
    @Qualifier("logIndexWriter")
    private IndexWriter logIndexWriter;

    @Autowired
    @Qualifier("webIndexWriter")
    private IndexWriter webIndexWriter;

    /**
     * 添加日志文档到 log 索引
     */
    @SneakyThrows
    public void addLogDocument(String taskId, String clientName, String message, String level) {
        Document doc = new Document();
        doc.add(new StringField("taskId", taskId, Field.Store.YES));
        doc.add(new StringField("clientName", clientName, Field.Store.YES));
        doc.add(new StringField("timestamp", System.currentTimeMillis() + "", Field.Store.YES));
        doc.add(new TextField("message", message, Field.Store.YES));
        doc.add(new StringField("level", level, Field.Store.YES));
        logIndexWriter.addDocument(doc);
        logIndexWriter.commit();
    }

    /**
     * 添加 Web 文档到 web 索引
     */
    @SneakyThrows
    public void addWebDocument(String url, String method, String requestBody, String responseBody) {
        Document doc = new Document();
        doc.add(new StringField("url", url, Field.Store.YES));
        doc.add(new StringField("method", method, Field.Store.YES));
        doc.add(new TextField("requestBody", requestBody, Field.Store.YES));
        doc.add(new TextField("responseBody", responseBody, Field.Store.YES));
        webIndexWriter.addDocument(doc);
        webIndexWriter.commit();
    }

    /**
     * 根据 taskId 精确搜索日志文档（支持分页）
     */
    @SneakyThrows
    public IPage<JSONObject> searchLogsByTaskId(String taskId, int pageNo, int pageSize) {
        try (IndexReader reader = DirectoryReader.open(logDirectory)) {
            IndexSearcher searcher = new IndexSearcher(reader);

            // 创建 TermQuery，精确匹配 taskId
            Term term = new Term("taskId", taskId);
            Query query = new TermQuery(term);

            // 计算分页参数
            int start = (pageNo - 1) * pageSize;
            int end = start + pageSize;

            // 执行查询
            TopDocs topDocs = searcher.search(query, end);
            List<JSONObject> results = new ArrayList<>();

            // 获取当前页的数据
            for (int i = start; i < Math.min(topDocs.scoreDocs.length, end); i++) {
                Document doc = searcher.doc(topDocs.scoreDocs[i].doc);
                JSONObject json = new JSONObject();
                json.put("taskId", doc.get("taskId"));
                json.put("clientName", doc.get("clientName"));
                json.put("timestamp", doc.get("timestamp"));
                json.put("message", doc.get("message"));
                json.put("level", doc.get("level"));
                results.add(json);
            }

            // 返回分页结果
            IPage<JSONObject> page = new Page<>();
            page.setCurrent(pageNo);
            page.setSize(pageSize);
            page.setRecords(results);
            page.setTotal(topDocs.totalHits.value);
            return page;
        }
    }

    /**
     * 搜索 web 索引（支持分页）
     */
    public IPage<JSONObject> searchWebLogs(String queryStr, int pageNo, int pageSize) throws Exception {
        return searchIndex(webDirectory, queryStr, "requestBody", pageNo, pageSize);
    }

    /**
     * 通用搜索方法（支持分页）
     */
    @SneakyThrows
    public IPage<JSONObject> searchIndex(Directory directory, String queryStr, String field, int pageNo, int pageSize) {
        try (IndexReader reader = DirectoryReader.open(directory)) {
            IndexSearcher searcher = new IndexSearcher(reader);

            // 解析查询
            QueryParser parser = new QueryParser(field, new StandardAnalyzer());
            Query query = parser.parse(queryStr);

            // 计算分页参数
            int start = (pageNo - 1) * pageSize;
            int end = start + pageSize;

            // 执行查询
            TopDocs topDocs = searcher.search(query, end);
            List<JSONObject> results = new ArrayList<>();

            // 获取当前页的数据
            for (int i = start; i < Math.min(topDocs.scoreDocs.length, end); i++) {
                Document doc = searcher.doc(topDocs.scoreDocs[i].doc);
                results.add(JSONObject.parseObject(doc.get("content")));
            }

            // 返回分页结果
            IPage<JSONObject> page = new Page<>();
            page.setCurrent(pageNo);
            page.setSize(pageSize);
            page.setRecords(results);
            page.setTotal(topDocs.totalHits.value);
            return page;
        }
    }
}