package com.github.pathreference;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

/**
 * Localized strings. English is the default bundle; the IDE's locale selects
 * the matching translation (for example {@code _zh_CN} with the Chinese
 * language pack).
 *
 * <p>{@link DynamicBundle} picks the bundle up per call, so the strings follow
 * the IDE language after a restart without caching a stale locale.</p>
 */
public final class PathReferenceBundle extends DynamicBundle {

    private static final String BUNDLE = "messages.PathReferenceBundle";
    private static final PathReferenceBundle INSTANCE = new PathReferenceBundle();

    private PathReferenceBundle() {
        // The DynamicBundle(String) constructor is deprecated: pass the class so
        // the platform can locate the bundle in the plugin's own classloader.
        super(PathReferenceBundle.class, BUNDLE);
    }

    public static @NotNull String message(@PropertyKey(resourceBundle = BUNDLE) @NotNull String key,
                                          Object @NotNull ... params) {
        return INSTANCE.getMessage(key, params);
    }
}
