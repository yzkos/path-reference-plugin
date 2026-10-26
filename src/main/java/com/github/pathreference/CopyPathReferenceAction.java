package com.github.pathreference;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.icons.AllIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Copies {@code project/relative/Path.php:42} or
 * {@code project/relative/Path.php:42-57} for the current caret/selection.
 */
public class CopyPathReferenceAction extends DumbAwareAction {

    public CopyPathReferenceAction() {
        // Set the presentation instead of using the deprecated
        // AnAction(text, description, icon) constructor.
        getTemplatePresentation().setText(PathReferenceBundle.message("action.copy.text"));
        getTemplatePresentation().setDescription(PathReferenceBundle.message("action.copy.description"));
        getTemplatePresentation().setIcon(AllIcons.Actions.Copy);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        VirtualFile[] files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        boolean available = file != null || (files != null && files.length > 0);
        e.getPresentation().setEnabledAndVisible(available);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        VirtualFile[] files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);

        // ReadAction.compute(...) is deprecated; run the read synchronously with
        // the non-blocking API instead.
        String text = ReadAction.nonBlocking(() -> buildText(project, file, files, editor))
                .executeSynchronously();
        if (text == null || text.isEmpty()) {
            return;
        }

        CopyPasteManager.getInstance().setContents(new StringSelection(text));
        showFeedback(project, text);
    }

    private static @Nullable String buildText(@Nullable Project project,
                                              @Nullable VirtualFile file,
                                              VirtualFile @Nullable [] files,
                                              @Nullable Editor editor) {
        if (editor != null && file != null
                && file.equals(FileDocumentManager.getInstance().getFile(editor.getDocument()))) {
            return buildFromEditor(project, file, editor);
        }
        String prefix = PathReferenceSettings.getPrefix();
        boolean backticks = PathReferenceSettings.isWrapInBackticks();
        if (files != null && files.length > 0) {
            List<String> paths = new ArrayList<>(files.length);
            for (VirtualFile selected : files) {
                paths.add(ReferenceFormatter.formatPath(referencePath(project, selected), prefix, backticks));
            }
            return String.join("\n", paths);
        }
        if (file != null) {
            return ReferenceFormatter.formatPath(referencePath(project, file), prefix, backticks);
        }
        return null;
    }

    private static @NotNull String buildFromEditor(@NotNull Project project,
                                                   @NotNull VirtualFile file,
                                                   @NotNull Editor editor) {
        Document document = editor.getDocument();
        String path = referencePath(project, file);
        String pathSeparator = PathReferenceSettings.getPathSeparator();
        String separator = PathReferenceSettings.getSeparator();
        String prefix = PathReferenceSettings.getPrefix();
        boolean backticks = PathReferenceSettings.isWrapInBackticks();

        List<Caret> carets = new ArrayList<>(editor.getCaretModel().getAllCarets());
        carets.sort(Comparator.comparingInt(Caret::getSelectionStart));

        List<String> references = new ArrayList<>(carets.size());
        String code = null;

        for (Caret caret : carets) {
            int[] lines = lineRange(document, caret);
            references.add(ReferenceFormatter.format(path, lines[0], lines[1],
                    pathSeparator, separator, prefix, backticks));

            if (PathReferenceSettings.isIncludeCode() && carets.size() == 1 && caret.hasSelection()) {
                code = caret.getSelectedText();
            }
        }

        String result = String.join("\n", references);
        if (code != null && !code.isBlank()) {
            result += "\n```" + ReferenceFormatter.fenceLanguage(file.getExtension()) + "\n"
                    + stripTrailingNewlines(code) + "\n```";
        }
        return result;
    }

    /**
     * @return one-based {@code [startLine, endLine]}
     */
    static int[] lineRange(@NotNull Document document, @NotNull Caret caret) {
        return LineRangeCalculator.lineRange(caret.getSelectionStart(), caret.getSelectionEnd(),
                caret.getOffset(), offset -> lineOf(document, offset));
    }

    private static int lineOf(@NotNull Document document, int offset) {
        int length = document.getTextLength();
        int clamped = Math.max(0, Math.min(offset, length));
        return document.getLineNumber(clamped);
    }

    private static @NotNull String referencePath(@Nullable Project project, @NotNull VirtualFile file) {
        PathReferenceSettings.PathMode mode = PathReferenceSettings.getPathMode();
        if (project == null || mode == PathReferenceSettings.PathMode.ABSOLUTE) {
            return file.getPath();
        }

        if (mode == PathReferenceSettings.PathMode.PROJECT) {
            String relative = relativeTo(file, ProjectUtil.guessProjectDir(project));
            if (relative != null) {
                return relative;
            }
        }

        // Content root: the explicit choice in CONTENT_ROOT mode, and the
        // fallback for files that live outside the project directory (extra
        // content roots, linked modules) instead of silently going absolute.
        String relative = relativeTo(file,
                ProjectFileIndex.getInstance(project).getContentRootForFile(file, false));
        return relative != null ? relative : file.getPath();
    }

    private static @Nullable String relativeTo(@NotNull VirtualFile file, @Nullable VirtualFile base) {
        if (base == null) {
            return null;
        }
        String relative = VfsUtilCore.getRelativePath(file, base, '/');
        if (relative == null) {
            relative = ReferenceFormatter.relativize(file.getPath(), base.getPath());
        }
        return relative == null || relative.isEmpty() ? null : relative;
    }

    private static @NotNull String stripTrailingNewlines(@NotNull String value) {
        int end = value.length();
        while (end > 0) {
            char c = value.charAt(end - 1);
            if (c == '\n' || c == '\r') {
                end--;
            } else {
                break;
            }
        }
        return value.substring(0, end);
    }

    private static void showFeedback(@Nullable Project project, @NotNull String text) {
        if (project == null || project.isDisposed()) {
            return;
        }
        int newline = text.indexOf('\n');
        String message = newline >= 0 ? text.substring(0, newline) + " …" : text;
        try {
            StatusBar.Info.set(PathReferenceBundle.message("status.copied", message), project);
        } catch (RuntimeException ignored) {
            // Feedback is nice to have; never fail the copy because of it.
        }
    }
}
