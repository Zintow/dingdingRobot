
package com.zintow.dingding;

import hudson.Extension;
import javaposse.jobdsl.dsl.helpers.publisher.PublisherContext;
import javaposse.jobdsl.plugin.ContextExtensionPoint;
import javaposse.jobdsl.plugin.DslExtensionMethod;

@Extension(optional = true)
public class DingdingDSLExtension extends ContextExtensionPoint {
	@DslExtensionMethod(context = PublisherContext.class)
    public Object dingdingPusher(String accessToken, String jsonFilePath) {
        return new DingdingNotifier(accessToken, jsonFilePath);
    }
}
