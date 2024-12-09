package com.example.studycard.Prism4j;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.Set;

import io.noties.prism4j.GrammarLocator;
import io.noties.prism4j.Prism4j;

public class MyGrammarLocator implements GrammarLocator {

    @Nullable
    @Override
    public Prism4j.Grammar grammar(@NonNull Prism4j prism4j, @NonNull String language) {
        switch (language) {

            case "json":
                return Prism_json.create(prism4j);
            case "javascript":
                return Prism_javascript.create(prism4j);
            // everything else is omitted

            default:
                return null;
        }
    }

    @androidx.annotation.NonNull
    @Override
    public Set<String> languages() {
        return Collections.emptySet();
    }
}