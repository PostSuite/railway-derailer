package com.postsuite.derailer.util;

import io.smallrye.mutiny.Uni;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@UtilityClass
public class MutinyUtil {

    public static <T, R> Uni<List<R>> uniListFailFast(@NonNull final List<T> items, @NonNull final Function<? super T, Uni<R>> mapper) {
        if (ObjectUtils.isEmpty(items)) {
            return Uni.createFrom().item(Collections.emptyList());
        }

        final List<Uni<R>> listOfUnis = items.stream()
                .map(mapper)
                .toList();

        return Uni.join().all(listOfUnis).andFailFast();
    }

}
