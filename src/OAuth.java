import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OAuth {
	private SFDCOAuthToken token;

	/**
	 * Note that we are working with a single user at this time.
	 * For multi-user application, things are going to change
	 * 
	 * @return SFDCOAuthToken
	 */
	public synchronized SFDCOAuthToken getOAuthToken() {
		if (token != null)
			return token;
		else {
			generateToken();
			return token;
		}
	}

	private synchronized void generateToken() {
		Client client = ClientBuilder.newClient();

		StringBuilder content = new StringBuilder();
		content.append("https://login.salesforce.com/services/oauth2/token");
		content.append("?grant_type=password");
		content.append("&client_id=");
		content.append(Properties.CLIENT_ID);
		content.append("&client_secret=");
		content.append(Properties.CLIENT_SECRET);
		content.append("&username=");
		content.append(Properties.USERNAME);
		content.append("&password=");
		content.append(Properties.PASSWORD);
		content.append(Properties.PASSWORD_TOKEN);

		WebTarget target = client.target(content.toString());

		Response r = target.request(MediaType.APPLICATION_JSON_TYPE).post(
				Entity.entity("", MediaType.APPLICATION_FORM_URLENCODED));

		try {
			ObjectMapper om = new ObjectMapper();
			String s = r.readEntity(String.class);
			token = om.readValue(s, SFDCOAuthToken.class);

		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		OAuth oauth = new OAuth();
		SFDCOAuthToken token= oauth.getOAuthToken();
		System.out.println(token.getAccess_token());
		System.out.println(token.getInstance_url());
		System.out.println(token.getToken_type());
	}
}
