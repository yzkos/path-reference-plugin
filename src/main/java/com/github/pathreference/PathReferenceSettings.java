package com.github.pathreference;

import com.intellij.ide.util.PropertiesComponent;
import org.jetbrains.annotations.NotNull;

/**
 * Small wrapper around {@link PropertiesComponent} so that the plugin does not
 * need a persistent-state service or a settings XML file.
 */
public final class PathReferenceSettings {

    public enum PathMode {
        PROJECT,
        CONTENT_ROOT,
        ABSOLUTE;

        /**
         * @return the label for the current IDE language, e.g. "Project root"
         * or "项目根目录"
         */
        public String getLabel() {
            return PathReferenceBundle.message("pathMode." + name());
        }

        @Override
        public String toString() {
            return getLabel();
        }
    }

    private static final String KEY_PATH_MODE = "pathReference.pathMode";
    private static final String KEY_SEPARATOR = "pathReference.lineSeparator";
    private static final String KEY_PATH_SEPARATOR = "pathReference.pathSeparator";
    private static final String KEY_PREFIX = "pathReference.prefix";
    private static final String KEY_BACKTICKS = "pathReference.wrapInBackticks";
    private static final String KEY_INCLUDE_CODE = "pathReference.includeCode";

    public static final String DEFAULT_SEPARATOR = "-";
    public static final String DEFAULT_PATH_SEPARATOR = ":";
    public static final String DEFAULT_PREFIX = "@";

    private PathReferenceSettings() {
    }

    public static @NotNull PathMode getPathMode() {
        String value = PropertiesComponent.getInstance().getValue(KEY_PATH_MODE, PathMode.PROJECT.name());
        try {
            return PathMode.valueOf(value);
        } catch (IllegalArgumentException e) {
            return PathMode.PROJECT;
        }
    }

    public static void setPathMode(@NotNull PathMode mode) {
        PropertiesComponent.getInstance().setValue(KEY_PATH_MODE, mode.name());
    }

    public static @NotNull String getSeparator() {
        String value = PropertiesComponent.getInstance().getValue(KEY_SEPARATOR, DEFAULT_SEPARATOR);
        return value == null || value.isEmpty() ? DEFAULT_SEPARATOR : value;
    }

    public static @NotNull String getPathSeparator() {
        String value = PropertiesComponent.getInstance().getValue(KEY_PATH_SEPARATOR, DEFAULT_PATH_SEPARATOR);
        return value == null || value.isEmpty() ? DEFAULT_PATH_SEPARATOR : value;
    }

    public static void setPathSeparator(@NotNull String separator) {
        PropertiesComponent.getInstance().setValue(KEY_PATH_SEPARATOR,
                separator == null || separator.isEmpty() ? DEFAULT_PATH_SEPARATOR : separator);
    }

    public static void setSeparator(@NotNull String separator) {
        PropertiesComponent.getInstance().setValue(KEY_SEPARATOR,
                separator == null || separator.isEmpty() ? DEFAULT_SEPARATOR : separator);
    }

    public static @NotNull String getPrefix() {
        String value = PropertiesComponent.getInstance().getValue(KEY_PREFIX, DEFAULT_PREFIX);
        return value == null ? DEFAULT_PREFIX : value;
    }

    public static void setPrefix(@NotNull String prefix) {
        PropertiesComponent.getInstance().setValue(KEY_PREFIX, prefix == null ? "" : prefix);
    }

    public static boolean isWrapInBackticks() {
        return PropertiesComponent.getInstance().getBoolean(KEY_BACKTICKS, false);
    }

    public static void setWrapInBackticks(boolean value) {
        PropertiesComponent.getInstance().setValue(KEY_BACKTICKS, value);
    }

    public static boolean isIncludeCode() {
        return PropertiesComponent.getInstance().getBoolean(KEY_INCLUDE_CODE, false);
    }

    public static void setIncludeCode(boolean value) {
        PropertiesComponent.getInstance().setValue(KEY_INCLUDE_CODE, value);
    }
}
