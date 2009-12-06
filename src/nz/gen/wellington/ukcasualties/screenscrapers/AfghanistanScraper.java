package nz.gen.wellington.ukcasualties.screenscrapers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import nz.gen.wellington.ukcasualties.utils.CachingHttpFetcher;

import org.apache.commons.lang.StringEscapeUtils;
import org.htmlparser.Node;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.sun.syndication.feed.module.mediarss.MediaEntryModuleImpl;
import com.sun.syndication.feed.module.mediarss.types.MediaContent;
import com.sun.syndication.feed.module.mediarss.types.Metadata;
import com.sun.syndication.feed.module.mediarss.types.Thumbnail;
import com.sun.syndication.feed.module.mediarss.types.UrlReference;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;

public class AfghanistanScraper extends ScreenScraperImpl {
		
    public static final String MOD_URL_PREFIX = "http://www.mod.uk";
    public static final String CASUALTIES_PAGE = MOD_URL_PREFIX + "/DefenceInternet/FactSheets/OperationsFactsheets/OperationsInAfghanistanBritishFatalities.htm";
	private static final String THUMBNAIL_PREFIX = "http://www.mod.uk/DefenceInternet/Templates/";
	private static final int MAX_ITEMS = 12;

	private CachingHttpFetcher httpFetcher;

	
	public AfghanistanScraper(CachingHttpFetcher httpFetcher) {
		this.httpFetcher = httpFetcher;
	}

	
	public String getPageURL() {
        return CASUALTIES_PAGE;	 
	}

    
	public List<SyndEntry> parseContent() {
        List<SyndEntry> newsitems = new ArrayList<SyndEntry>();
                
        final String content = httpFetcher.fetchContent(getPageURL(), getCharacterEncoding());        
        if (content == null) {           
            return newsitems;
        }
        
        try {
            Node mainContent = this.extractTagByClassName(content, "factsheet");       
            NodeList nodes = extractLinks(mainContent.toHtml(), "/DefenceInternet/DefenceNews/MilitaryOperations/|/NR/exeres/BA78EF33-64D7-4156-A22E-9AA33196C0C3,frameless.htm");
    
            int numberOfItems = nodes.size();
            if (numberOfItems > MAX_ITEMS) {
            	numberOfItems = MAX_ITEMS;
            }
    		for (int i = 0; i < numberOfItems; i++) {       	
            	SyndEntry newsitem = new SyndEntryImpl();
            	Tag tag = (Tag) nodes.elementAt(i);
            	
            	newsitem.setTitle(tag.toPlainTextString().trim());
                
            	String url = tag.getAttribute("href");                
                if (!url.startsWith(MOD_URL_PREFIX)) {
                    url = MOD_URL_PREFIX + url;
                }
                newsitem.setLink(url);
                
                final String slug = newsitem.getTitle().toLowerCase().replaceAll("\\s|,|'", "");
                final String guid = newsitem.getLink() + "#" + slug;
                newsitem.setUri(guid);
                
            	fetchDetailsFromPage(newsitem);
            	
            	boolean isUnique = isUnique(newsitems, newsitem);        	        	
            	if (isUnique) {
            		newsitems.add(newsitem);
            	}
            }   
        } catch (ParserException e) {           
        }       
           
        return newsitems;
    }


	private boolean isUnique(List<SyndEntry> newsitems, SyndEntry newsitem) {
		boolean notDuplicate = true;
		Iterator<SyndEntry> iterator = newsitems.iterator();
		while (iterator.hasNext() && notDuplicate) {        	
			SyndEntry next = iterator.next();
			if (newsitem.getTitle().equals(next.getTitle())) {
				return false;
			}
		}
		return true;
	}

	
	private void fetchDetailsFromPage(SyndEntry newsitem) throws ParserException {
	
		String content = httpFetcher.fetchContent(newsitem.getLink(), this.getCharacterEncoding(), 3600 * 48);
		
		Tag dateTag = (Tag) this.extractTagByName(content, "DC.date.created");
		String dateString = dateTag.getAttribute("content");
		newsitem.setPublishedDate(this.parseDate(dateString, "dd/MM/yyyy"));
		
		Tag summaryTag = (Tag) this.extractTagByName(content, "DC.description");
		String summaryText = summaryTag.getAttribute("content");
		
        SyndContent description = new SyndContentImpl();
        description.setType("text/plain");
        description.setValue(summaryText);
        newsitem.setDescription(description);
        
		Node pageMainContent = this.extractTagById(content, "left-column");
		final String body = pageMainContent.toHtml();		
		final String imageUrl = extractImageUrlFromBody(body);
		if (imageUrl != null) {
			MediaContent image = createPictureMediaContentItem(imageUrl, newsitem.getTitle());
            MediaEntryModuleImpl mediaModule = new MediaEntryModuleImpl();    
            MediaContent[] m= {image};
            mediaModule.setMediaContents(m);
            newsitem.getModules().add(mediaModule);                   
		}
	}
	
	
		
	private String extractImageUrlFromBody(String body) throws ParserException {	
		NodeList images = this.extractTagsByType(body, "img");
		if (images.size() > 0) {
			Tag tag = (Tag) images.elementAt(0);
			String imageSrc = tag.getAttribute("src");
            imageSrc = StringEscapeUtils.unescapeHtml(imageSrc);
            return THUMBNAIL_PREFIX + imageSrc;			
		}
		return null;		
	}
	
	
	private MediaContent createPictureMediaContentItem(String image, String altText) {
		MediaContent mediaContent = null;
		try {
			mediaContent = createPictureMediaContentFromImage(image);
			Thumbnail thumbnail = new Thumbnail(new URL(image));

			Metadata metadata = new Metadata();
			metadata.setDescription("");
			metadata.setThumbnail(new Thumbnail[] { thumbnail });
			metadata.setDescription(altText);
			mediaContent.setMetadata(metadata);
			
		} catch (MalformedURLException e) {
		}
		return mediaContent;
	}
	
	
	
	private MediaContent createPictureMediaContentFromImage(String image) throws MalformedURLException {
		MediaContent mediaContent = new MediaContent(new UrlReference(image));
		mediaContent.setType("image/jpeg");
		return mediaContent;
	}

}
