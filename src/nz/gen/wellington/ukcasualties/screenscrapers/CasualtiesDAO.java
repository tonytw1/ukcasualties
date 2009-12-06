package nz.gen.wellington.ukcasualties.screenscrapers;

import java.util.List;

import com.sun.syndication.feed.synd.SyndEntry;

public class CasualtiesDAO {
        
    private AfghanistanScraper scraper;
   
    public CasualtiesDAO(AfghanistanScraper scraper) {       
        this.scraper = scraper;       
    }

    public List<SyndEntry> getCasualties() {       
        return scraper.parseContent();
    }

}
