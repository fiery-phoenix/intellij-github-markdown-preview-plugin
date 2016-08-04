package org.fiery.md.actions;

import com.intellij.ide.browsers.BrowserLauncher;
import com.intellij.ide.browsers.WebBrowser;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.util.ArrayUtil;

public class PreviewMdFileAction extends AnAction {

    private final WebBrowser browser;

    public PreviewMdFileAction(WebBrowser browser) {
        super(browser.getName(), null, browser.getIcon());
        this.browser = browser;
    }

    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        BrowserLauncher.getInstance()
                       .browseUsingPath("http://www.markdowntutorial.com/lesson/1/", null, browser, project,
                                        ArrayUtil.EMPTY_STRING_ARRAY);

    }

}
