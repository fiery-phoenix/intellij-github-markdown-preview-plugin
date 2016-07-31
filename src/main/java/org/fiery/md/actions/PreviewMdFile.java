package org.fiery.md.actions;

import com.intellij.ide.browsers.BrowserLauncher;
import com.intellij.ide.browsers.WebBrowser;
import com.intellij.ide.browsers.WebBrowserManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.util.ArrayUtil;

import java.util.List;

public class PreviewMdFile extends AnAction {

	public PreviewMdFile() {
		super("Preview *.md file");
	}

	public void actionPerformed(AnActionEvent event) {
		Project project = event.getData(PlatformDataKeys.PROJECT);
		List<WebBrowser> browsers = WebBrowserManager.getInstance().getBrowsers();
		BrowserLauncher.getInstance().browseUsingPath("http://www.markdowntutorial.com/lesson/1/", null, browsers.get(0), project, ArrayUtil.EMPTY_STRING_ARRAY);
	}

}
