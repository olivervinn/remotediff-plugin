package org.jenkinsci.vinn;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.model.TaskListener;
import hudson.plugins.emailext.plugins.EmailTrigger;
import hudson.plugins.emailext.plugins.EmailTriggerDescriptor;
import hudson.plugins.emailext.plugins.RecipientProvider;
import hudson.plugins.emailext.plugins.recipients.DevelopersRecipientProvider;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.List;

public class SCMBuildEmailTrigger extends EmailTrigger {

    public static final String TRIGGER_NAME = "Remote Ahead Trigger";

    @DataBoundConstructor
    public SCMBuildEmailTrigger(List<RecipientProvider> recipientProviders, String recipientList, String replyTo,
                          String subject, String body, String attachmentsPattern, int attachBuildLog, String contentType) {
        super(recipientProviders, recipientList, replyTo, subject, body, attachmentsPattern, attachBuildLog, contentType);
    }

    @Deprecated
    public SCMBuildEmailTrigger(boolean sendToList, boolean sendToDevs, boolean sendToRequester, boolean sendToCulprits, String recipientList, String replyTo, String subject, String body, String attachmentsPattern, int attachBuildLog, String contentType) {
        super(sendToList, sendToDevs, sendToRequester, sendToCulprits, recipientList, replyTo, subject, body, attachmentsPattern, attachBuildLog, contentType);
    }

    @Override
    public boolean trigger(AbstractBuild<?, ?> build, TaskListener listener) {
        Result buildResult = build.getResult();
        SCMBuildAction sba = build.getAction(SCMBuildAction.class);

        if (sba != null && sba.getChangesBehindRemoteHead() > 100) {
            return true;
        }

        return false;
    }

    @Extension
    public static final class DescriptorImpl extends EmailTriggerDescriptor {

        public DescriptorImpl() {
            addDefaultRecipientProvider(new DevelopersRecipientProvider());
        }

        @Override
        public String getDisplayName() {
            return TRIGGER_NAME;
        }

        @Override
        public EmailTrigger createDefault() {
            return _createDefault();
        }
    }
}