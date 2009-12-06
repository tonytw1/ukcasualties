    package nz.gen.wellington.ukcasualties.servlets;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nz.gen.wellington.ukcasualties.screenscrapers.AfghanistanScraper;
import nz.gen.wellington.ukcasualties.screenscrapers.CasualtiesDAO;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;


public class FeedController implements Controller {
	
    private CasualtiesDAO feedDataService;
    
    public FeedController(CasualtiesDAO feedDataService) {       
        this.feedDataService = feedDataService;     
    }

        
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {        
        List <SyndEntry> feedItems = feedDataService.getCasualties();
        if (!(feedItems.size() > 0)) {
            //log.warn("Feed data service returned 0 feed items");
        }
        ModelAndView mv = new ModelAndView(new PlainTextView());
        mv.addObject("text", createFeedText(feedItems, AfghanistanScraper.MOD_URL_PREFIX));              
        return mv;
    }

    
	protected String createFeedText(List<SyndEntry> newsItems, String linkURL) throws IOException, FeedException {		
		String feedContent = null;		
		SyndFeed feed = new SyndFeedImpl();	
		feed.setTitle("Operations in Afghanistan: British Fatalities");
		feed.setFeedType("rss_2.0");
		feed.setLink(linkURL);
		feed.setDescription("It is with very deep regret that the Ministry of Defence has confirmed the following fatalities suffered during operations in Afghanistan.");
	
		try {
			feed.setEntries(newsItems);			
		} catch (Exception e) {
			//log.error("An unknown exception occured", e);
		}
    
		StringWriter writer = new StringWriter();
		SyndFeedOutput output = new SyndFeedOutput();
		output.output(feed, writer);
		feedContent = writer.toString();				
		return feedContent;			
	}

}
