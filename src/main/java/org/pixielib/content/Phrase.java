package org.pixielib.content;

public class Phrase {
    public PhraseStatus status;
    public String text;
    public Phrase(String text, PhraseStatus status) {
        this.text = text;
        this.status = status;
    }

    public static PhraseStatus toStatus(String rep) {
        if (rep.equalsIgnoreCase("regular")) {
            return PhraseStatus.Regular;
        } else if (rep.equalsIgnoreCase("hidden")) {
            return PhraseStatus.Hidden;
        } else if (rep.equalsIgnoreCase("title")) {
            return PhraseStatus.Title;
        }

        throw new IllegalArgumentException("unknown phrase status.");
    }

    public static String toString(PhraseStatus status) {
        if (status == PhraseStatus.Regular)
            return "regular";
        if (status == PhraseStatus.Hidden)
            return "hidden";
        if (status == PhraseStatus.Title)
            return "title";

        return "";
    }

    public enum PhraseStatus {
        Regular,
        Hidden,
        Title
    }
}
