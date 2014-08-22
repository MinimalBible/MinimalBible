package org.bspeice.minimalbible.service.format.osistohtml;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.crosswire.jsword.book.OSISUtil;
import org.xml.sax.Attributes;

import java.util.Stack;

import static org.bspeice.minimalbible.service.format.Constants.HTML;

/**
 * This can either signify a quote or Red Letter
 * Example from ESV Prov 19:1
 * <l sID="x9938"/>...<l eID="x9938" type="x-br"/><l sID="x9939" type="x-indent"/>..<l eID="x9939" type="x-br"/>
 * <p/>
 * Apparently quotation marks are not supposed to appear in the KJV (https://sites.google.com/site/kjvtoday/home/Features-of-the-KJV/quotation-marks)
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class LHandler {

    private static String indent_html = HTML.NBSP + HTML.NBSP;
    private HtmlTextWriter writer;
    private Stack<LType> stack = new Stack<LType>();

    public LHandler(OsisToHtmlParameters parameters, HtmlTextWriter writer) {
        this.writer = writer;
        int indentCharCount = 4; // TODO: Set a standard value for this
        indent_html = StringUtils.repeat(HTML.NBSP, indentCharCount);
    }

    public String getTagName() {
        return "l";
    }

    public void startL(Attributes attrs) {
        // Refer to Gen 3:14 in ESV for example use of type=x-indent
        String type = attrs.getValue(OSISUtil.OSIS_ATTR_TYPE);
        int level = TagHandlerHelper.getAttribute(OSISUtil.OSIS_ATTR_LEVEL, attrs, 1);
        // make numIndents default to zero
        int numIndents = Math.max(0, level - 1);

        LType ltype = LType.IGNORE;
        if (StringUtils.isNotEmpty(type)) {
            if (type.contains("indent")) {
                // this tag is specifically for indenting so ensure there is an indent
                numIndents = numIndents + 1;
                writer.write(StringUtils.repeat(indent_html, numIndents));
                ltype = LType.INDENT;
            } else if (type.contains("br")) {
                writer.write(HTML.BR);
                ltype = LType.BR;
            } else {
                ltype = LType.IGNORE;
                Log.d("LHandler", "Unknown <l> tag type:" + type);
            }
        } else if (TagHandlerHelper.isAttr(OSISUtil.OSIS_ATTR_SID, attrs)) {
            writer.write(StringUtils.repeat(indent_html, numIndents));
            ltype = LType.IGNORE;
        } else if (TagHandlerHelper.isAttr(OSISUtil.OSIS_ATTR_EID, attrs)) {
            // e.g. Isaiah 40:12
            writer.write(HTML.BR);
            ltype = LType.BR;
        } else {
            //simple <l>
            writer.write(StringUtils.repeat(indent_html, numIndents));
            ltype = LType.END_BR;
        }
        stack.push(ltype);
    }

    public void endL() {
        LType type = stack.pop();
        if (LType.END_BR.equals(type)) {
            writer.write(HTML.BR);
        }
    }

    enum LType {INDENT, BR, END_BR, IGNORE}
}
