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
        super(PathReferenceBundle.message("action.settings.text"),
                PathReferenceBundle.message("action.settings.description"),
                AllIcons.General.Settings);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        ShowSettingsUtil.getInstance().showSettingsDialog(project, PathReferenceConfigurable.class);
    }
}
