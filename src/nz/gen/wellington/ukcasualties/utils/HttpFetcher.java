package nz.gen.wellington.ukcasualties.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpFetcher {

	public String fetchContent(String pageURL, String pageCharacterEncoding) {
		try {
			StringBuilder output = new StringBuilder();
			URL url = new URL(pageURL);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					url.openStream()));
			String line;

			while ((line = reader.readLine()) != null) {
				output.append(line);
			}
			reader.close();
			return output.toString();

		} catch (MalformedURLException e) {
			// ...
		} catch (IOException e) {
			// ...
		}
		return null;
	}

}