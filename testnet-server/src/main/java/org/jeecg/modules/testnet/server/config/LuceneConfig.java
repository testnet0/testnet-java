package org.jeecg.modules.testnet.server.config;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class LuceneConfig {

    @Value("${lucene.index.path}")
    private String indexPath;

    // 为 log 索引创建 Directory 和 IndexWriter
    @Bean(name = "logDirectory")
    public Directory logDirectory() throws IOException {
        return FSDirectory.open(getIndexPath("lucene-log-index"));
    }

    public Path getIndexPath(String index) {
        return Paths.get(indexPath, index);
    }

    @Bean(name = "logIndexWriter")
    public IndexWriter logIndexWriter(@Autowired Directory logDirectory) throws IOException {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        return new IndexWriter(logDirectory, config);
    }

    // 为 web 索引创建 Directory 和 IndexWriter
    @Bean(name = "webDirectory")
    public Directory webDirectory() throws IOException {
        return FSDirectory.open(getIndexPath("lucene-web-index"));
    }

    @Bean(name = "webIndexWriter")
    public IndexWriter webIndexWriter(@Autowired Directory webDirectory) throws IOException {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        return new IndexWriter(webDirectory, config);
    }
}