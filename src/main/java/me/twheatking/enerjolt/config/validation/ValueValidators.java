package me.twheatking.enerjolt.config.validation;

import me.twheatking.enerjolt.config.ConfigValidationException;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public final class ValueValidators {
    public static final ValueValidator<String> STRING_NOT_EMPTY = new ValueValidator<>() {
        @Override
        public void validate(@NotNull String value) throws ConfigValidationException {
            if(value.isEmpty())
                throw new ConfigValidationException("Value must not be empty");
        }

        @Override
        public @NotNull List<String> getValidationCommentLines() {
            return List.of("Value must not be empty");
        }
    };
    public static final ValueValidator<String> STRING_NOT_BLANK = new ValueValidator<>() {
        @Override
        public void validate(@NotNull String value) throws ConfigValidationException {
            if(value.isEmpty())
                throw new ConfigValidationException("Value must not be blank");
        }

        @Override
        public @NotNull List<String> getValidationCommentLines() {
            return List.of("Value must not be blank");
        }
    };

    @NotNull
    public static <T> ElementOfCollectionValueValidator<T> elementOfCollection(@NotNull Collection<T> elements) {
        return new ElementOfCollectionValueValidator<>(elements);
    }

    private ValueValidators() {}
}
