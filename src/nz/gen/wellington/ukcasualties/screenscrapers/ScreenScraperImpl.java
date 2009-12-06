package nz.gen.wellington.ukcasualties.screenscrapers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.LinkRegexFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public abstract class ScreenScraperImpl {

    
    private static final String DEFAULT_ENCODING = "ISO-8859-1";
    
    public String getCharacterEncoding() {
        return DEFAULT_ENCODING;
    }

    protected Date parseDate(String dateString, String dateFormat) {
        if (dateString != null) {
            Date date = null;         
            SimpleDateFormat sdfInput = new SimpleDateFormat(dateFormat);
            try {
                date = sdfInput.parse(dateString);
                return date;
            } catch (ParseException e) {
                //log.warn("Invalid date input (" + dateString + ") for date format '" + dateFormat + "'");
                return null;
            }
        } else {
            //log.warn("Expected date field was not seen on request");
            return null;
        }
    }


    protected Tag extractTagByClassName(String content, String classname) throws ParserException {
        Parser parser = new Parser();
        parser.setInputHTML(content);
        NodeFilter commentFilterNode = new HasAttributeFilter("class", classname);    
        NodeList tagList = parser.extractAllNodesThatMatch(commentFilterNode);
        if (tagList.size() > 0) {
            Tag tag = (Tag) tagList.elementAt(0);        
            return tag;
        }
        return null;
    }


    
    protected NodeList extractLinks(String content, String linkRegex) throws ParserException {
    	 Parser parser = new Parser();
         parser.setInputHTML(content);
         return this.extractNewsLinks(parser, linkRegex);
    }


    protected NodeList extractTagsByType(String content, String type) throws ParserException {
        Parser parser = new Parser();
        parser.setInputHTML(content);
        NodeFilter commentFilterNode = new TagNameFilter("img");
        NodeList tagList = parser.extractAllNodesThatMatch(commentFilterNode);      
        return tagList;
    }


    protected Tag extractTagById(String content, String id) throws ParserException {
        Parser parser = new Parser();
        parser.setInputHTML(content);
        NodeFilter commentFilterNode = new HasAttributeFilter("id", id);    
        NodeList tagList = parser.extractAllNodesThatMatch(commentFilterNode);
        if (tagList.size() == 1) {
            Tag tag = (Tag) tagList.elementAt(0);        
            return tag;
        } 
        return null;
    }
    
    
    
    protected Tag extractTagByName(String content, String name) throws ParserException {
        Parser parser = new Parser();
        parser.setInputHTML(content);
        NodeFilter commentFilterNode = new HasAttributeFilter("name", name);    
        NodeList tagList = parser.extractAllNodesThatMatch(commentFilterNode);
        if (tagList.size() == 1) {
            Tag tag = (Tag) tagList.elementAt(0);        
            return tag;
        } 
        return null;
    }


	protected NodeList extractNewsLinks(Parser parser, String linkRegex) throws ParserException {		
		NodeFilter filterNode = new LinkRegexFilter(linkRegex);
		NodeFilter filter = new AndFilter(filterNode, new NodeClassFilter(
				Tag.class));
		NodeList list = parser.extractAllNodesThatMatch(filter);
		return list;
	}
    
}
