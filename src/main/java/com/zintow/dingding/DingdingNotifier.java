package com.zintow.dingding;


import java.io.IOException;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import jenkins.tasks.SimpleBuildStep;

public class DingdingNotifier extends Notifier implements SimpleBuildStep{

	private String accessToken;

    private String jsonFilePath;

    public String getAccessToken() {
        return accessToken;
    }

    public String getJsonFilePath() {
		return jsonFilePath;
	}

    @DataBoundConstructor
    public DingdingNotifier(String accessToken, String jsonFilePath) {
        super();
        this.accessToken = accessToken;
        this.jsonFilePath = jsonFilePath;
    }

    public DingdingService newDingdingService(FilePath workspace, TaskListener listener) {
        return new DingdingServiceImpl(accessToken, jsonFilePath, listener, workspace);
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

	@Override
	public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener)
			throws InterruptedException, IOException {
		System.out.println("here");
		Result result = run.getResult();
        if (null != result && result.equals(Result.SUCCESS)) {
        	this.newDingdingService(workspace, listener).success();
        }
		
	}


    @Override
    public DingdingNotifierDescriptor getDescriptor() {
        return (DingdingNotifierDescriptor) super.getDescriptor();
    }

    @Extension
    @Symbol("dingding")
    public static class DingdingNotifierDescriptor extends BuildStepDescriptor<Publisher> {


        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Dingding Json Pusher";
        }

    }
}
