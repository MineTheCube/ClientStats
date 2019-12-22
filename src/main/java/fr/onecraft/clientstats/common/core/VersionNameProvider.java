package fr.onecraft.clientstats.common.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class VersionNameProvider {

    public static final String versionURL = "https://raw.githubusercontent.com/PrismarineJS/minecraft-data/master/data/pc/common/protocolVersions.json";

    private static HashMap<Integer, String> versionMap = new HashMap<>();
    private static CloseableHttpClient httpClient = HttpClients.createDefault();
    private static JsonParser parser = new JsonParser();

    public static void reload(boolean silent, Logger logger) throws IOException {
	logger.log(Level.INFO, "Reloading version names...");
	versionMap.clear();
	if (!silent)
	    logger.log(Level.INFO, "Retrieving latest version list...");
	CloseableHttpResponse response = httpClient.execute(new HttpGet(VersionNameProvider.versionURL));
	JsonArray versions = parser.parse(EntityUtils.toString(response.getEntity())).getAsJsonArray();
	if (!silent)
	    logger.log(Level.INFO, "Parsing version list...");
	Iterator<JsonElement> it = versions.iterator();
	while (it.hasNext()) {
	    JsonObject obj = it.next().getAsJsonObject();
	    if (!obj.get("usesNetty").getAsBoolean())
		continue;
	    versionMap.put(obj.get("version").getAsInt(),
		    (versionMap.get(obj.get("version").getAsInt()) != null
			    ? (versionMap.get(obj.get("version").getAsInt()) + "/")
			    : "") + obj.get("minecraftVersion").getAsString());
	    if (!silent)
		logger.log(Level.INFO,
			obj.get("version").getAsInt() + " -> " + versionMap.get(obj.get("version").getAsInt()));
	}
	logger.log(Level.INFO, "Reloaded version names.");
    }

    public static String get(int version) {
	return versionMap.get(version);
    }

}
