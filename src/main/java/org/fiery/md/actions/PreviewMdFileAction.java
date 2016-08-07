package org.fiery.md.actions;

import com.intellij.ide.browsers.BrowserLauncher;
import com.intellij.ide.browsers.WebBrowser;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

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
        DataContext dataContext = event.getDataContext();
        Editor currentEditor = DataKeys.EDITOR.getData(dataContext);
        VirtualFile currentFile = DataKeys.VIRTUAL_FILE.getData(dataContext);
        Document document = currentEditor == null ? null : currentEditor.getDocument();
        if (document == null || currentFile == null) {
            return;
        }
        try {
            File tempFile = File.createTempFile(currentFile.getNameWithoutExtension(), ".md");

            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(tempFile),
                                                                    Charset.forName("UTF-8"))) {
                writer.write(new GithubMarkdownHelper().toGitMarkdownHtml(currentFile.getName(), document));
            }
            BrowserLauncher.getInstance().browse(VfsUtil.toUri(tempFile).toURL().toExternalForm(), browser);
            tempFile.deleteOnExit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
