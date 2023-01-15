package LanguageProcesses.Utils;

import LanguageProcesses.Corpus;

public class TextBuilder {
    private String text;

    public TextBuilder(String text) {
        this.text = text;
    }


    public TextBuilder replaceAll(String target, String replacement) {
        this.text = this.text.replaceAll(target, replacement);
        return this;
    }


    
    public TextBuilder normalizeText() {
        this.text = Corpus.normalizeText(this.text);
        return this;
    }
    public TextBuilder removeStopWords() {
        this.text = Corpus.removeStopWords(this.text);
        return this;
    }
    public TextBuilder cleanTheLine() {
        this.text = Corpus.cleanTheLine(this.text);
        return this;
    }



    public TextBuilder replaceUnderscoreWithSpace() {
        this.text = Corpus.replaceUnderscoreWithSpace(this.text);
        return this;
    }
    public TextBuilder replaceSpecialCharactersWithWords() {
        this.text = Corpus.replaceSpecialCharactersWithWords(this.text);
        return this;
    }
    public TextBuilder removeKashida() {
        this.text = Corpus.removeKashida(this.text);
        return this;
    }
    public TextBuilder replaceAlternativeLetters() {
        this.text = Corpus.replaceAlternativeLetters(this.text);
        return this;
    }
    public TextBuilder removeConsecutiveRedundantCharacters(int num) {
        this.text = Corpus.removeConsecutiveRedundantCharacters(this.text, num);
        return this;
    }
    public TextBuilder replaceArabicLettersToMatch() {
        this.text = Corpus.replaceArabicLettersToMatch(this.text);
        return this;
    }
    public TextBuilder removeNonArabic() {
        this.text = Corpus.removeNonArabic(this.text);
        return this;
    }



    public TextBuilder replaceWithLetterAlef() {
        this.text = Corpus.replaceWithLetterAlef(this.text);
        return this;
    }
    public TextBuilder replaceWithLetterBeh() {
        this.text = Corpus.replaceWithLetterBeh(this.text);
        return this;
    }
    public TextBuilder replaceWithLetterTeh() {
        this.text = Corpus.replaceWithLetterTeh(this.text);
        return this;
    }
    public TextBuilder replaceWithLetterJeem() {
        this.text = Corpus.replaceWithLetterJeem(this.text);
        return this;
    }
    public TextBuilder replaceWithLetterHah() {
        this.text = Corpus.replaceWithLetterHah(this.text);
        return this;
    }
    public TextBuilder replaceWithLetterKhah() {
        this.text = Corpus.replaceWithLetterKhah(this.text);
        return this;
    }
    public TextBuilder replaceWithLetterDal() {
        this.text = Corpus.replaceWithLetterDal(this.text);
        return this;
    }
    public TextBuilder replaceWithLetterReh() {
        this.text = Corpus.replaceWithLetterReh(this.text);
        return this;
    }
    public TextBuilder replaceWithLetterZain() {
        this.text = Corpus.replaceWithLetterZain(this.text);
        return this;
    }
    public TextBuilder replaceWithLetterSeen() {
        this.text = Corpus.replaceWithLetterSeen(this.text);
        return this;
    }
    public TextBuilder replaceWithLetterSheen() {
        this.text = Corpus.replaceWithLetterSheen(this.text);
        return this;
    }
    public TextBuilder replaceWithLetterSad() {
        this.text = Corpus.replaceWithLetterSad(this.text);
        return this;
    }
    public TextBuilder replaceWithLetterDad() {
        this.text = Corpus.replaceWithLetterDad(this.text);
        return this;
    }
    public TextBuilder replaceWithLetterTah() {
        this.text = Corpus.replaceWithLetterTah(this.text);
        return this;
    }
    public TextBuilder replaceWithLetterAin() {
        this.text = Corpus.replaceWithLetterAin(this.text);
        return this;
    }
    public TextBuilder replaceWithLetterGhain() {
        this.text = Corpus.replaceWithLetterGhain(this.text);
        return this;
    }
    public TextBuilder replaceWithLetterFeh() {
        this.text = Corpus.replaceWithLetterFeh(this.text);
        return this;
    }
    public TextBuilder replaceWithLetterQaf() {
        this.text = Corpus.replaceWithLetterQaf(this.text);
        return this;
    }
    public TextBuilder replaceWithLetterKaf() {
        this.text = Corpus.replaceWithLetterKaf(this.text);
        return this;
    }
    public TextBuilder replaceWithLetterLam() {
        this.text = Corpus.replaceWithLetterLam(this.text);
        return this;
    }
    public TextBuilder replaceWithLetterMeem() {
        this.text = Corpus.replaceWithLetterMeem(this.text);
        return this;
    }
    public TextBuilder replaceWithLetterNoon() {
        this.text = Corpus.replaceWithLetterNoon(this.text);
        return this;
    }
    public TextBuilder replaceWithLetterHeh() {
        this.text = Corpus.replaceWithLetterHeh(this.text);
        return this;
    }
    public TextBuilder replaceWithLetterWaw() {
        this.text = Corpus.replaceWithLetterWaw(this.text);
        return this;
    }
    public TextBuilder replaceWithLetterYeh() {
        this.text = Corpus.replaceWithLetterYeh(this.text);
        return this;
    }


    public TextBuilder replaceUnderAlefWithUpperAlef() {
        this.text = Corpus.replaceUnderAlefWithUpperAlef(this.text);
        return this;
    }
    public TextBuilder replaceTehMarbutaWithHeh() {
        this.text = Corpus.replaceTehMarbutaWithHeh(this.text);
        return this;
    }

    

    public String build() {
        return this.text;
    }
}
