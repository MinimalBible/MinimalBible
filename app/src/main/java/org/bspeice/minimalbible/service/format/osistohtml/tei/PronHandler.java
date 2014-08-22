package org.bspeice.minimalbible.service.format.osistohtml.tei;

import org.bspeice.minimalbible.service.format.osistohtml.HiHandler;
import org.bspeice.minimalbible.service.format.osistohtml.HtmlTextWriter;
import org.bspeice.minimalbible.service.format.osistohtml.OsisToHtmlParameters;
import org.xml.sax.Attributes;


/**
 * Handle orth tag very similarly to hi tag
 * <orth>?????????</orth>
 * <orth rend="bold" type="trans">aneuthetos</orth>
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class PronHandler extends HiHandler {

    private final static String DEFAULT = "italic";

    public PronHandler(OsisToHtmlParameters parameters, HtmlTextWriter writer) {
        super(parameters, writer);
    }

    public String getTagName() {
        return "pron";
    }

    public void start(Attributes attrs) {
        String rend = attrs.getValue(TEIUtil.TEI_ATTR_REND);
        start(rend, DEFAULT);
    }
}
