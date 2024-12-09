package com.example.studycard.Prism4j;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;
import static io.noties.prism4j.Prism4j.grammar;
import static io.noties.prism4j.Prism4j.pattern;
import static io.noties.prism4j.Prism4j.token;

import androidx.annotation.NonNull;

import io.noties.prism4j.Prism4j;

public class Prism_javascript {

    @NonNull
    public static Prism4j.Grammar create(@NonNull Prism4j prism4j) {
        return grammar(
                "javascript",
                // Комментарии
                token("comment", pattern(compile("//.*|/\\*[\\s\\S]*?\\*/"))),

                // Строки
                token("string", pattern(compile("\"(?:\\\\.|[^\\\\\"\\r\\n])*\"|'(?:\\\\.|[^\\\\'\\r\\n])*'"), false, true)),

                // Шаблонные строки
                token("template-string", pattern(compile("`(?:\\\\.|[^\\\\`])*`"), false, true)),

                // Числа
                token("number", pattern(compile("\\b\\d+(\\.\\d+)?([eE][+-]?\\d+)?\\b|0[xX][a-fA-F0-9]+"))),

                // Ключевые слова
                token("keyword", pattern(compile("\\b(?:if|else|for|while|return|function|var|let|const|class|new|try|catch|throw|switch|case|break|continue|import|export|default|extends|super|this|typeof|instanceof|in|of|void|yield|async|await)\\b"))),

                // Классы
                token("class-name", pattern(compile("\\b[A-Z][a-zA-Z0-9_]*\\b"))),

                // Функции
                token("function", pattern(compile("\\b[a-zA-Z_][a-zA-Z0-9_]*\\s*(?=\\()"))),

                // Булевы значения
                token("boolean", pattern(compile("\\b(?:true|false)\\b", CASE_INSENSITIVE))),

                // null и undefined
                token("null-undefined", pattern(compile("\\b(?:null|undefined)\\b", CASE_INSENSITIVE))),

                // Операторы
                token("operator", pattern(compile("[-+*/%&|^!=<>]=?|\\?|:|~"))),

                // Знаки препинания
                token("punctuation", pattern(compile("[{}\\[\\];(),.:]")))
        );
    }
}

