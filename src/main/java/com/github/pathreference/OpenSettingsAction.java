package com.github.pathreference;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Direct entry point: Tools | Path Reference Settings…
 *
 * <p>Also searchable via Search Everywhere ("Path Reference").</p>
 */
public class OpenSettingsAction extends AnAction {

    public OpenSettingsAction() {
        // Set the presentation instead of using the deprecated
        // AnAction(text, description, icon) constructor.
        getTemplatePresentation().setText(PathReferenceBundle.message("action.settings.text"));
        getTemplatePresentation().setDescription(PathReferenceBundle.message("action.settings.description"));
        getTemplatePresentation().setIcon(AllIcons.General.Settings);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        ShowSettingsUtil.getInstance().showSettingsDialog(project, PathReferenceConfigurable.class);
    }
}
