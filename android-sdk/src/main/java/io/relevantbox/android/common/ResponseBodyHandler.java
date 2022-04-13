package io.relevantbox.android.common;

import androidx.annotation.Nullable;

public interface ResponseBodyHandler<R> {

    R handle(@Nullable String rawResponseBody);
}
