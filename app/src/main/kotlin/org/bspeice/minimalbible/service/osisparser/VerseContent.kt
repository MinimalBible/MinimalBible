package org.bspeice.minimalbible.service.osisparser

import java.util.ArrayList

/**
 * Created by bspeice on 9/7/14.
 */

class VerseContent() {
    public var id: Int = 0
    public var content: String = ""
    public var chapterTitle: String = ""
    public var paraTitle: String = ""
    public var references: List<VerseReference> = ArrayList()

    public fun appendContent(content: String) {
        this.content += content
    }

    public fun toJson() {

    }
}