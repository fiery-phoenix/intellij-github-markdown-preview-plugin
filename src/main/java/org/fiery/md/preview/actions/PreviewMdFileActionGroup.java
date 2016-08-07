package org.fiery.md.preview.actions;

import com.intellij.ide.browsers.WebBrowser;
import com.intellij.ide.browsers.WebBrowserManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ComputableActionGroup;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.util.CachedValueProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PreviewMdFileActionGroup extends ComputableActionGroup {

    public PreviewMdFileActionGroup() {
        super(true);
    }

    @NotNull
    @Override
    protected CachedValueProvider<AnAction[]> createChildrenProvider(@NotNull ActionManager actionManager) {
        return () -> {
            List<WebBrowser> browsers = WebBrowserManager.getInstance().getActiveBrowsers();
            AnAction[] actions = browsers.stream().map(PreviewMdFileAction::new).toArray(AnAction[]::new);

            return CachedValueProvider.Result.create(actions, WebBrowserManager.getInstance());
        };
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        VirtualFile currentFile = DataKeys.VIRTUAL_FILE.getData(event.getDataContext());
        boolean isMarkDownFile = currentFile != null && "md".equals(currentFile.getExtension());
        WebBrowserManager browserManager = WebBrowserManager.getInstance();
        event.getPresentation().setVisible(isMarkDownFile
                                           && browserManager.isShowBrowserHover()
                                           && !browserManager.getActiveBrowsers().isEmpty());
    }
}
