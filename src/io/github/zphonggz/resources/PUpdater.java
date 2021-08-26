package io.github.zphonggz.resources;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;
import com.fasterxml.jackson.databind.*;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.logging.Logger;

/**
 * @author PzikTeam
 *
 */

public class PUpdater {
    /**
     * field
     */
    private String resourceid;
    private UpdateAction action;
    private boolean autocheck;
    private int timetocheck;
    private Plugin plugin;
    private File path;
    private String updatedverson;
    private boolean isChecking = false;
    /**
     * 
     * @param plugin
     */
    public PUpdater(Plugin plugin) {
	this.plugin = plugin;
	this.updatedverson = plugin.getDescription().getVersion();
    }

    /**
     * @param plugin
     * @param resourceid
     * @param pluginversion
     * @param action
     * @param autocheck
     * @param timetocheck
     */
    public PUpdater(Plugin plugin, String resourceid, UpdateAction action, boolean autocheck,
	    int timetocheck, File path) {
	this.plugin = plugin;
	this.resourceid = resourceid;
	this.action = action;
	this.autocheck = autocheck;
	this.timetocheck = timetocheck;
	this.path = path;
	this.updatedverson = plugin.getDescription().getVersion();
    }

    /**
     * 
     * @param resourceid
     * @param pluginversion
     * 
     */
    public PUpdater(Plugin plugin, String resourceid) {
	this(plugin, resourceid, UpdateAction.ONLYCHECK, false, 0, null);
    }

    /**
     * @param plugin
     * @param resourceid
     * @param pluginversion
     * @param action
     * 
     */
    public PUpdater(Plugin plugin, String resourceid, UpdateAction action) {
	this(plugin, resourceid, action, false, 0, null);
    }

    /**
     * 
     * @param resourceid
     * 
     *                   Set ResourceId
     */
    public void setResourceId(String resourceid) {
	this.resourceid = resourceid;
    }

    /**
     * 
     * @return
     * 
     *         Get ResourceId
     */
    public String getResourceId() {
	return this.resourceid;
    }


    /**
     * 
     * @return
     * 
     *         Get Plugin Version
     */
    public String getUpdatedVersion() {
	return this.updatedverson;
    }

    /**
     * 
     * @param action
     * 
     *               Set Action
     */
    public void setAction(UpdateAction action) {
	this.action = action;
    }

    /**
     * 
     * @return
     * 
     *         Get Action
     */
    public UpdateAction getAction() {
	return this.action;
    }

    /**
     * 
     * @param autocheck
     * 
     *                  Set Plugin AutoCheck ?
     */
    public void setAutoCheck(boolean autocheck) {
	this.autocheck = autocheck;
    }

    /**
     * 
     * @return
     */
    public boolean IsAutoCheck() {
	return this.autocheck;
    }

    /**
     * 
     * @param time
     * 
     *             Set time for each autocheck
     */
    public void setTimetocheck(int time) {
	this.timetocheck = time;
    }

    /**
     * 
     * @return
     * 
     *         get Time for each autocheck
     */
    public int getTimetocheck() {
	return this.timetocheck;
    }

    /**
     * 
     * @param plugin
     */
    public void setPlugin(Plugin plugin) {
	this.plugin = plugin;
    }

    /**
     * 
     * @return
     * 
     */
    public Plugin getPlugin() {
	return this.plugin;
    }

    /**
     * 
     * @param file
     * 
     *             Set Download Path
     */
    public void setPath(File path) {
	this.path = path;
    }

    /**
     * 
     * @return
     * 
     *         Get Download Path
     */
    public File getPath() {
	return this.path;
    }
    
    /**
     * 
     * @return updateversion Get updated version
     * 
     */
    public String GetUVersion() {
	return this.updatedverson;
    }
    
    public boolean isCheckings() {
	return this.isChecking;
    }
    /**
     * 
     */
    public void RunCheck() {
	this.isChecking = true;
	Logger log = plugin.getServer().getLogger();
	log.info(ChatColor.YELLOW + "Checking the latest version...");
	try {
	    HttpURLConnection http = (HttpURLConnection)(
		    new URL("https://api.spiget.org/v2/resources/"+resourceid+"/versions/latest").openConnection());
	    SetRequest(http, "GET", 20000, 100000, "application/json");
	    http.connect();
	    int statuscode = http.getResponseCode();
	    if (statuscode == 200) {
		String res = GetResponse(http);
		if (res.isEmpty() && res != null) {
		    log.info(ChatColor.RED+"An error occurred while read data response from server! Please contact author to fix.");
		}
		ObjectMapper map = new ObjectMapper();
		String vs = map.readTree(res).get("name").asText();
		if (vs == plugin.getDescription().getVersion()) {
		    log.info(ChatColor.YELLOW+"Your plugin is the latest version");
		    return;
		}
		http.disconnect();
		String id = map.readTree(res).get("id").asText();
		String downloadurl = "https://api.spiget.org/v2/resources/"+this.resourceid+"/versions/"+id+"/download";
		boolean isdown = DownloadFile(downloadurl, plugin.getName(), vs);
		if (!isdown) {
		    log.info(ChatColor.RED+"Plugin download failed!");
		} else {
		    log.info(ChatColor.GREEN+"Plugin download successful!");
		}
		this.updatedverson = vs;
		
	    } else {
		log.info(ChatColor.RED+"Get bad response from server. Please try again later!");
	    }
	} catch (IOException e) {
	    log.info(ChatColor.RED + "Cannot connect to the server");
	} catch (IllegalArgumentException e) {
	    
	}
	this.isChecking = false;
	
    }

    /**
     * 
     * @param http
     * @param conType
     * @param contime
     * @param readtime
     * @param contentType
     */
    private void SetRequest(HttpURLConnection http, String conType, int contime, int readtime, String contentType) {
	try {
	    http.setRequestMethod(conType);
	    http.setRequestProperty("Content-Type", contentType);
	    http.setConnectTimeout(contime);
	    http.setReadTimeout(readtime);
	} catch (ProtocolException e) {

	}
    }

    private String GetResponse(HttpURLConnection http) {
	BufferedReader in;
	String scontent = "";
	try {
	    in = new BufferedReader(new InputStreamReader(http.getInputStream()));
	    String inputLine;
	    StringBuffer content = new StringBuffer();
	    while ((inputLine = in.readLine()) != null) {
		content.append(inputLine);
	    }
	    in.close();
	    scontent = content.toString();
	} catch (IOException e) {
	    
	}

	return scontent;
    }

    private boolean DownloadFile(String url, String fname, String version) {
	File f = new File(path.getPath()+"\\"+fname+"v"+version+".jar");     
	try {
	    if(!f.createNewFile()) {
	        plugin.getLogger().info(ChatColor.GOLD+ "Detected file's name already exists. The download progress will be stoped!");
	        return false;
	    }
	} catch (IOException e1) {
	    plugin.getLogger().info(ChatColor.RED+"An error occurred while write plugin data from server to file");
	    return false;
	}
	try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
		FileOutputStream fileOutputStream = new FileOutputStream(
			plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getFile())) {
	    byte dataBuffer[] = new byte[1024];
	    int bytesRead;
	    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
		fileOutputStream.write(dataBuffer, 0, bytesRead);
	    }
	} catch (IOException e) {
	    plugin.getLogger().info(ChatColor.RED+"An error occurred while write plugin data from server to file");	    
	}
	return true;
    }
    
    public void AutoCheck() {
	BukkitRunnable runs = new BukkitRunnable() {
	    @Override 
	    public void run() {
		if(!IsAutoCheck()) {
		    this.cancel();
		}
		RunCheck();
	    }
	};
	runs.runTaskTimer(plugin, 0, timetocheck*20);
    }
    
    
}
