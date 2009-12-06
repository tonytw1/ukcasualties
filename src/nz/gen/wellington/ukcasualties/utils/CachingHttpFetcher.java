package nz.gen.wellington.ukcasualties.utils;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;


public class CachingHttpFetcher extends HttpFetcher {

	private static final int DEFAULT_TTL = 3600;
	
	MemcacheService cache;
	
	public CachingHttpFetcher() {	
			cache = MemcacheServiceFactory.getMemcacheService();
			cache.clearAll();
	}
	

	@Override
	public String fetchContent(String pageURL, String pageCharacterEncoding) {		
		return fetchContent(pageURL, pageCharacterEncoding, DEFAULT_TTL);
	}
	
	
    public String fetchContent(String pageURL, String pageCharacterEncoding, int TTL) {		
		final String cachedContent = (String) cache.get(pageURL);
		if (cachedContent != null) {
			return cachedContent;
		}
		
        final String fetchedContent = super.fetchContent(pageURL, pageCharacterEncoding);
        if (fetchedContent != null) {
        	Expiration oneMinute = Expiration.byDeltaSeconds(DEFAULT_TTL);
        	cache.put(pageURL, fetchedContent, oneMinute);     	        	
        }
        return fetchedContent;
    }
	
}
