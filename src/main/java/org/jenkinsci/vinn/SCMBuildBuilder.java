package org.jenkinsci.vinn;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 * This is a build action
 */
public class SCMBuildBuilder extends Builder {

    static final String DISPLAY_NAME = "Remote Ahead Check";
    public final String cmd;
    public final int threshold;

    @Extension
    public static class FirstBuilderImpl extends BuildStepDescriptor<Builder> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> proj) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return DISPLAY_NAME;
        }
    }

    @DataBoundConstructor
    public SCMBuildBuilder(final String cmd, final int threshold) {
        this.cmd = cmd;
        this.threshold = threshold;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        listener.getLogger().println(DISPLAY_NAME);

        SCMBuildAction.SCMBuildInfo info = build.getWorkspace().act(new SCMBuildRemoteOperation(this.cmd));
        listener.getLogger().println("Output: " + info.log);
        listener.getLogger().println("Captured value of: " + info.changes_behind_remote_head);

        SCMBuildAction action = build.getAction(SCMBuildAction.class);
        if (action != null) {
            action.addInfo(info);
        } else {
            action = new SCMBuildAction();
            action.addInfo(info);
            build.addAction(action);
            build.addAction(new SCMBuildBadge(info.changes_behind_remote_head));
        }
        return true;
    }

    @Override
    public Action getProjectAction(AbstractProject<?, ?> project) {
        return new SCMBuildProjectAction(project);
    }
}
