package org.fiery.md.actions;

import com.intellij.ide.browsers.BrowserLauncher;
import com.intellij.ide.browsers.WebBrowser;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VfsUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

public class PreviewMdFileAction extends AnAction {

    private final WebBrowser browser;

    public PreviewMdFileAction(WebBrowser browser) {
        super(browser.getName(), null, browser.getIcon());
        this.browser = browser;
    }

    public void actionPerformed(AnActionEvent event) {
        Editor currentEditor = DataKeys.EDITOR.getData(event.getDataContext());
        Document document = currentEditor == null ? null : currentEditor.getDocument();
        if (document == null) {
            return;
        }
        try {
            File file = File.createTempFile("markdown", ".html");

            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file),
                                                                    Charset.forName("UTF-8"))) {
                writer.write(new GitMarkdownHelper().toGitMarkdownHtml(document));
            }

            BrowserLauncher.getInstance()
                           .browse(VfsUtil.toUri(file).toURL().toExternalForm(), browser);
            file.deleteOnExit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
