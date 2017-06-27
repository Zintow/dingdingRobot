package com.zintow.dingding;


import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

public class DingdingNotifier extends Notifier {

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

    public DingdingService newDingdingService(AbstractBuild build, TaskListener listener) {
        return new DingdingServiceImpl(accessToken, jsonFilePath, listener, build);
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        return true;
    }


    @Override
    public DingdingNotifierDescriptor getDescriptor() {
        return (DingdingNotifierDescriptor) super.getDescriptor();
    }

    @Extension
    public static class DingdingNotifierDescriptor extends BuildStepDescriptor<Publisher> {


        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "钉钉推送配置";
        }

    }
}
