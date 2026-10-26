package com.github.pathreference;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Pure formatting helpers, kept separate from the platform action so they are
 * easy to test.
 */
public final class ReferenceFormatter {

    private ReferenceFormatter() {
    }

    public static @NotNull String format(@NotNull String path,
                                         int startLine,
                                         int endLine,
                                         @NotNull String pathSeparator,
                                         @NotNull String rangeSeparator,
                                         @NotNull String prefix,
                                         boolean wrapInBackticks) {
        String reference = startLine == endLine
                ? path + pathSeparator + startLine
                : path + pathSeparator + startLine + rangeSeparator + endLine;
        return formatPath(reference, prefix, wrapInBackticks);
    }

    public static @NotNull String formatPath(@NotNull String path,
                                             @NotNull String prefix,
                                             boolean wrapInBackticks) {
        String value = prefix + path;
        return wrapInBackticks ? "`" + value + "`" : value;
    }

    /**
     * Makes {@code path} relative to {@code basePath} by comparing the paths
     * themselves. Used as a fallback when {@code VfsUtilCore.getRelativePath}
     * refuses to relativize two files (it returns {@code null} when they belong
     * to different file system implementations, which happens under Remote
     * Development, WSL mounts and symlinked project roots).
     *
     * @return the relative path, or {@code null} if {@code path} is not inside
     * {@code basePath}
     */
    public static @Nullable String relativize(@NotNull String path, @NotNull String basePath) {
        String base = trimTrailingSlashes(basePath);
        if (base.isEmpty()) {
            return null;
        }
        if (path.equals(base)) {
            return "";
        }
        String prefix = base.endsWith("/") ? base : base + "/";
        return path.startsWith(prefix) ? path.substring(prefix.length()) : null;
    }

    private static @NotNull String trimTrailingSlashes(@NotNull String path) {
        int end = path.length();
        while (end > 1 && path.charAt(end - 1) == '/') {
            end--;
        }
        return path.substring(0, end);
    }

    public static @NotNull String fenceLanguage(@Nullable String extension) {
        if (extension == null || extension.isEmpty()) {
            return "";
        }
        switch (extension.toLowerCase()) {
            case "php":
                return "php";
            case "js":
            case "mjs":
            case "cjs":
                return "javascript";
            case "ts":
            case "mts":
            case "cts":
                return "typescript";
            case "vue":
                return "vue";
            case "html":
            case "htm":
                return "html";
            case "css":
            case "scss":
            case "less":
                return "css";
            case "json":
                return "json";
            case "xml":
                return "xml";
            case "yml":
            case "yaml":
                return "yaml";
            case "sql":
                return "sql";
            case "sh":
            case "bash":
                return "bash";
            case "md":
                return "markdown";
            case "py":
                return "python";
            case "java":
                return "java";
            case "kt":
            case "kts":
                return "kotlin";
            case "go":
                return "go";
            case "rs":
                return "rust";
            default:
                return extension.toLowerCase();
        }
    }
}
