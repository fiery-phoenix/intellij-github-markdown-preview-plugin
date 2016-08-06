package org.fiery.md.actions;

import com.intellij.openapi.editor.Document;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class GitMarkdownHelper {

    private static final String MARKDOWN_HTML_WRAPPER_START = "<html>\n"
                                                              + "<head>\n"
                                                              + "    <meta charset=\"utf-8\">\n"
                                                              + "    <title>Markdown Preview</title>\n"
                                                              + "    <link href=\"http://maxcdn.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css\" rel=\"stylesheet\">\n"
                                                              + "    <link href=\"http://netdna.bootstrapcdn.com/font-awesome/3.1.1/css/font-awesome.css\" rel=\"stylesheet\">\n"
                                                              + "</head>\n"
                                                              + "<body>";

    private static final String MARKDOWN_HTML_WRAPPER_END = "</body>";

    public String toGitMarkdownHtml(Document document) {
        try {
            return MARKDOWN_HTML_WRAPPER_START + requestGitMarkdownHtml(document) + MARKDOWN_HTML_WRAPPER_END;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String requestGitMarkdownHtml(Document document) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = createGettingMarkdownRequest(document);
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                switch (response.getStatusLine().getStatusCode()) {
                case 200:
                    return EntityUtils.toString(response.getEntity());
                default:
                    throw new RuntimeException(response.getStatusLine().toString());
                }
            }
        }
    }

    private HttpPost createGettingMarkdownRequest(Document document) {
        HttpPost httpPost = new HttpPost("https://api.github.com/markdown/raw");
        StringEntity requestEntity = new StringEntity(document.getText(), ContentType.create("text/plain", "UTF-8"));
        httpPost.setEntity(requestEntity);
        httpPost.setConfig(RequestConfig.custom()
                                        .setSocketTimeout(200)
                                        .setConnectTimeout(200)
                                        .setConnectionRequestTimeout(300)
                                        .build());

        return httpPost;
    }

}
