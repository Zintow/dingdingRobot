package com.zintow.dingding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import hudson.FilePath;
import hudson.ProxyConfiguration;
import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;

public class DingdingServiceImpl implements DingdingService {

    private Logger logger = LoggerFactory.getLogger(DingdingService.class);

    
    private String jsonFilePath;
	
	private AbstractBuild build;


    private static final String apiUrl = "https://oapi.dingtalk.com/robot/send?access_token=";

    
    private String accessTokens;

    public DingdingServiceImpl(String token, String jsonFilePath, TaskListener listener, AbstractBuild build) {
        this.jsonFilePath = jsonFilePath;
        this.accessTokens = token;
		this.build = build;
    }


    @Override
    public void success() {
        notifyLinkMessageToAll(this.accessTokens);
    }
    
    private void notifyLinkMessageToAll(String accesstokens) {
    	String[] tokens = accesstokens.split(",");
    	for(String token: tokens){
    		sendMessage(token);
    	}
    }
    
    private void sendMessage(String accesstoken) {
        HttpClient client = getHttpClient();
        PostMethod post = new PostMethod(apiUrl + accesstoken);
        try {
        	JSONObject body = readJson();//readJsonFilePathWay(); read file with utf-8 encoding.
        	//writeJson(body.toJSONString());
        	//body = readJsonFilePathWay();
        	if(body != null){        		
        		post.setRequestEntity(new StringRequestEntity(body.toJSONString(), "application/json", "UTF-8"));
        	}else{
        		logger.error("Failed to parse json file");
        	}
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            logger.error("build request error", e);
        }
        try {
            client.executeMethod(post);
            logger.info(post.getResponseBodyAsString());
        } catch (IOException e) {
            logger.error("send msg error", e);
        }
        post.releaseConnection();
    }


    private HttpClient getHttpClient() {
        HttpClient client = new HttpClient();
        Jenkins jenkins = Jenkins.getInstance();
        if (jenkins != null && jenkins.proxy != null) {
            ProxyConfiguration proxy = jenkins.proxy;
            if (proxy != null && client.getHostConfiguration() != null) {
                client.getHostConfiguration().setProxy(proxy.name, proxy.port);
                String username = proxy.getUserName();
                String password = proxy.getPassword();
                // Consider it to be passed if username specified. Sufficient?
                if (username != null && !"".equals(username.trim())) {
                    logger.info("Using proxy authentication (user=" + username + ")");
                    client.getState().setProxyCredentials(AuthScope.ANY,
                            new UsernamePasswordCredentials(username, password));
                }
            }
        }
        return client;
    }
    
    private JSONObject readJson(){
    	JSONObject dataJson = null;
    	 try {
    		 BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(this.jsonFilePath), "utf-8"));
			String s = null; 
			StringBuffer tempStr = new StringBuffer();
			while ((s = br.readLine()) != null) { 
				tempStr.append(s);
			}
			dataJson = JSONObject.parseObject(tempStr.toString());
			br.close();
		} catch (Exception e) {
			logger.error("Read json error", e);
		}
    	return dataJson;
    }

    private JSONObject readJsonFilePathWay(){
    	JSONObject dataJson = null;
    	 try {
    		 FilePath workspace = build.getWorkspace();
    		 FilePath dingding = new FilePath(workspace, this.jsonFilePath);
			dataJson = JSONObject.parseObject(dingding.readToString());
		} catch (Exception e) {
			logger.error("Read json error through file path way", e);
		}
    	return dataJson;
    }

    private void writeJson(String s){
    	 try {
    		 FilePath workspace = build.getWorkspace();
    		 FilePath dingding = new FilePath(workspace, this.jsonFilePath);
    		 dingding.write(s, null);
		} catch (Exception e) {
			logger.error("Write json error", e);
		}
    }
	
}
