package com.github.pathreference;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Settings page: Settings | Tools | Path Reference.
 *
 * <p>All labels come from the localized bundle, so the page follows the IDE's
 * language (English by default, Chinese with the Chinese language pack).</p>
 */
public class PathReferenceConfigurable implements Configurable {

    private ComboBox<PathReferenceSettings.PathMode> pathModeCombo;
    private JBTextField prefixField;
    private JBTextField pathSeparatorField;
    private JBTextField rangeSeparatorField;
    private JCheckBox backticksBox;
    private JCheckBox includeCodeBox;

    @Override
    public @Nls String getDisplayName() {
        return PathReferenceBundle.message("settings.displayName");
    }

    @Override
    public @Nullable JComponent createComponent() {
        pathModeCombo = new ComboBox<>(PathReferenceSettings.PathMode.values());

        prefixField = new JBTextField(4);
        prefixField.setToolTipText(PathReferenceBundle.message("settings.prefix.tooltip"));

        pathSeparatorField = new JBTextField(4);
        pathSeparatorField.setToolTipText(PathReferenceBundle.message("settings.pathSeparator.tooltip"));

        rangeSeparatorField = new JBTextField(4);
        rangeSeparatorField.setToolTipText(PathReferenceBundle.message("settings.separator.tooltip"));

        backticksBox = new JCheckBox(PathReferenceBundle.message("settings.backticks"));
        includeCodeBox = new JCheckBox(PathReferenceBundle.message("settings.includeCode"));

        JPanel panel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel(PathReferenceBundle.message("settings.pathStyle")), pathModeCombo)
                .addLabeledComponent(new JBLabel(PathReferenceBundle.message("settings.prefix")), prefixField)
                .addLabeledComponent(new JBLabel(PathReferenceBundle.message("settings.pathSeparator")), pathSeparatorField)
                .addLabeledComponent(new JBLabel(PathReferenceBundle.message("settings.separator")), rangeSeparatorField)
                .addComponent(backticksBox)
                .addComponent(includeCodeBox)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
        panel.setBorder(JBUI.Borders.empty(12));
        reset();
        return panel;
    }

    @Override
    public boolean isModified() {
        if (pathModeCombo == null) {
            return false;
        }
        return selectedPathMode() != PathReferenceSettings.getPathMode()
                || !prefix().equals(PathReferenceSettings.getPrefix())
                || !pathSeparator().equals(PathReferenceSettings.getPathSeparator())
                || !rangeSeparator().equals(PathReferenceSettings.getSeparator())
                || backticksBox.isSelected() != PathReferenceSettings.isWrapInBackticks()
                || includeCodeBox.isSelected() != PathReferenceSettings.isIncludeCode();
    }

    @Override
    public void apply() {
        if (pathModeCombo == null) {
            return;
        }
        PathReferenceSettings.setPathMode(selectedPathMode());
        PathReferenceSettings.setPrefix(prefix());
        PathReferenceSettings.setPathSeparator(pathSeparator());
        PathReferenceSettings.setSeparator(rangeSeparator());
        PathReferenceSettings.setWrapInBackticks(backticksBox.isSelected());
        PathReferenceSettings.setIncludeCode(includeCodeBox.isSelected());
    }

    @Override
    public void reset() {
        if (pathModeCombo == null) {
            return;
        }
        pathModeCombo.setSelectedItem(PathReferenceSettings.getPathMode());
        prefixField.setText(PathReferenceSettings.getPrefix());
        pathSeparatorField.setText(PathReferenceSettings.getPathSeparator());
        rangeSeparatorField.setText(PathReferenceSettings.getSeparator());
        backticksBox.setSelected(PathReferenceSettings.isWrapInBackticks());
        includeCodeBox.setSelected(PathReferenceSettings.isIncludeCode());
    }

    @Override
    public void disposeUIResources() {
        pathModeCombo = null;
        prefixField = null;
        pathSeparatorField = null;
        rangeSeparatorField = null;
        backticksBox = null;
        includeCodeBox = null;
    }

    private @NotNull PathReferenceSettings.PathMode selectedPathMode() {
        Object selected = pathModeCombo.getSelectedItem();
        return selected instanceof PathReferenceSettings.PathMode
                ? (PathReferenceSettings.PathMode) selected
                : PathReferenceSettings.PathMode.PROJECT;
    }

    private @NotNull String prefix() {
        return prefixField.getText().trim();
    }

    private @NotNull String pathSeparator() {
        String value = pathSeparatorField.getText().trim();
        return value.isEmpty() ? PathReferenceSettings.DEFAULT_PATH_SEPARATOR : value;
    }

    private @NotNull String rangeSeparator() {
        String value = rangeSeparatorField.getText().trim();
        return value.isEmpty() ? PathReferenceSettings.DEFAULT_SEPARATOR : value;
    }
}
