package org.bspeice.minimalbible.service.format.osisparser;

import com.google.gson.Gson;

import org.crosswire.jsword.passage.Verse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bspeice on 9/9/14.
 */
public class VerseContent {
    private int id;
    private String bookName = "";
    private int chapter;
    private int verseNum;
    private String content = "";
    private String chapterTitle = "";
    private String paraTitle = "";
    private List<VerseReference> references = new ArrayList<VerseReference>();

    public VerseContent() {
    }

    public VerseContent(Verse v) {
        this.id = v.getOrdinal();
        this.bookName = v.getBook().toString();
        this.chapter = v.getChapter();
        this.verseNum = v.getVerse();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public String getParaTitle() {
        return paraTitle;
    }

    public void setParaTitle(String paraTitle) {
        this.paraTitle = paraTitle;
    }

    public List<VerseReference> getReferences() {
        return references;
    }

    public void setReferences(List<VerseReference> references) {
        this.references = references;
    }

    public void appendContent(String content) {
        this.content += content;
    }

    public void appendReference(VerseReference reference) {
        this.references.add(reference);
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public int getVerseNum() {
        return verseNum;
    }

    public void setVerseNum(int verseNum) {
        this.verseNum = verseNum;
    }

    public String toJson() {
        // Lazy load Gson - not likely that we'll call this method multiple times, so
        // don't have to worry about a penalty there.
        return new Gson().toJson(this);
    }
}
