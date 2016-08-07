package org.fiery.md.actions;

import com.intellij.openapi.editor.Document;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class GithubMarkdownHelper {

    private static final String MARKDOWN_HTML_WRAPPER_START = "<!DOCTYPE html>\n"
                                                              + "<html lang=\"en\">\n"
                                                              + "<head>\n"
                                                              + "    <meta charset=\"utf-8\">\n"
                                                              + "    <title>%1$s</title>\n"
                                                              + "    <link crossorigin=\"anonymous\" href=\"https://assets-cdn.github.com/assets/frameworks-a2a1ae24a31177c689f6ec6d0f320684962f572ef60a84b385ec47dd00c06b38.css\" integrity=\"sha256-oqGuJKMRd8aJ9uxtDzIGhJYvVy72CoSzhexH3QDAazg=\" media=\"all\" rel=\"stylesheet\">\n"
                                                              + "    <link crossorigin=\"anonymous\" href=\"https://assets-cdn.github.com/assets/github-1e541164394dd957f0ccaeb1a6b08b1064064d8491212bc28a3379907db2ef96.css\" integrity=\"sha256-HlQRZDlN2VfwzK6xprCLEGQGTYSRISvCijN5kH2y75Y=\" media=\"all\" rel=\"stylesheet\">\n   "
                                                              + "</head>\n"
                                                              + "<body>"
                                                              + "<div class=\"container new-discussion-timeline experiment-repo-nav\">\n"
                                                              + "    <div class=\"repository-content\">\n"
                                                              + "        <div id=\"readme\" class=\"readme boxed-group clearfix announce instapaper_body md\">\n"
                                                              + "            <h3>\n"
                                                              + "                <svg aria-hidden=\"true\" class=\"octicon octicon-book\" height=\"16\" version=\"1.1\" viewBox=\"0 0 16 16\" width=\"16\"><path d=\"M3 5h4v1H3V5zm0 3h4V7H3v1zm0 2h4V9H3v1zm11-5h-4v1h4V5zm0 2h-4v1h4V7zm0 2h-4v1h4V9zm2-6v9c0 .55-.45 1-1 1H9.5l-1 1-1-1H2c-.55 0-1-.45-1-1V3c0-.55.45-1 1-1h5.5l1 1 1-1H15c.55 0 1 .45 1 1zm-8 .5L7.5 3H2v9h6V3.5zm7-.5H9.5l-.5.5V12h6V3z\"></path></svg>\n"
                                                              + "                    %1$s\n"
                                                              + "            </h3>\n"
                                                              + "\n"
                                                              + "            <article class=\"markdown-body entry-content\" itemprop=\"text\">\n";

    private static final String MARKDOWN_HTML_WRAPPER_END = "</article></div></div></div></body>";

    public String toGitMarkdownHtml(String fileName, Document document) {
        try {
            return String.format(MARKDOWN_HTML_WRAPPER_START, StringEscapeUtils.escapeHtml(fileName))
                   + requestGitMarkdownHtml(document) + MARKDOWN_HTML_WRAPPER_END;
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
