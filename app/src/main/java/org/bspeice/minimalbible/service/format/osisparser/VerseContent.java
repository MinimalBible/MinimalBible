package org.bspeice.minimalbible.service.format.osisparser;

import org.apache.commons.lang3.NotImplementedException;

import java.util.List;

/**
 * Created by bspeice on 9/9/14.
 */
public class VerseContent {
    private int id;
    private String content;
    private String chapterTitle;
    private String paraTitle;
    private List<VerseReference> references;

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

    public String toJson() {
        throw new NotImplementedException("JSON conversion not implemented yet!");
    }
}
