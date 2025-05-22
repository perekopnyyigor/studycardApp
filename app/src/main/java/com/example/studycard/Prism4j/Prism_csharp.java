package com.example.studycard.Prism4j;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;
import static io.noties.prism4j.Prism4j.grammar;
import static io.noties.prism4j.Prism4j.pattern;
import static io.noties.prism4j.Prism4j.token;

import androidx.annotation.NonNull;

import io.noties.prism4j.Prism4j;

public class Prism_csharp {

    @NonNull
    public static Prism4j.Grammar create(@NonNull Prism4j prism4j) {
        return grammar(
                "csharp",
                // Комментарии
                token("comment", pattern(compile("//.*|/\\*[\\s\\S]*?\\*/"))),

                // Строки
                token("string", pattern(compile("\"(?:\\\\.|[^\\\\\"\\r\\n])*\"|'(?:\\\\.|[^\\\\'\\r\\n])*'"), false, true)),

                // Интеполированные строки
                token("interpolated-string", pattern(compile("\\$\"(?:\\\\.|[^\\\\\"])*\""), false, true)),

                // Числа
                token("number", pattern(compile("\\b\\d+(\\.\\d+)?([eE][+-]?\\d+)?\\b|0[xX][a-fA-F0-9]+"))),

                // Ключевые слова
                token("keyword", pattern(compile("\\b(?:abstract|add|alias|as|ascending|async|await|base|bool|break|byte|case|catch|char|checked|class|const|continue|decimal|default|delegate|descending|do|double|dynamic|else|enum|event|explicit|extern|false|finally|fixed|float|for|foreach|from|get|global|goto|group|if|implicit|in|int|interface|internal|into|is|join|let|lock|long|namespace|new|null|object|operator|orderby|out|override|params|partial|private|protected|public|readonly|ref|remove|return|sbyte|sealed|select|set|short|sizeof|stackalloc|static|string|struct|switch|this|throw|true|try|typeof|uint|ulong|unchecked|unsafe|ushort|using|value|var|virtual|void|volatile|where|while|yield)\\b"))),

                // Классы
                token("class-name", pattern(compile("\\b[A-Z][a-zA-Z0-9_]*\\b"))),

                // Типы
                //token("type", pattern(compile("\\b(?:int|long|short|float|double|decimal|char|string|bool|object|byte|sbyte|uint|ulong|ushort)\\b"))),

                // Функции
                token("function", pattern(compile("\\b[a-zA-Z_][a-zA-Z0-9_]*\\s*(?=\\()"))),

                // Булевы значения
                token("boolean", pattern(compile("\\b(?:true|false)\\b", CASE_INSENSITIVE))),

                // null
                token("null", pattern(compile("\\bnull\\b", CASE_INSENSITIVE))),

                // Операторы
                token("operator", pattern(compile("[-+*/%&|^!=<>]=?|\\?|:|~|\\*\\*|>>|<<|=>|\\.\\.|\\+\\+|--"))),

                // Знаки препинания
                token("punctuation", pattern(compile("[{}\\[\\];(),.:]")))
        );
    }
}
