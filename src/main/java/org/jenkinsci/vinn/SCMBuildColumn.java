package org.jenkinsci.vinn;

import hudson.Extension;
import hudson.model.Job;
import hudson.model.Run;
import hudson.views.ListViewColumn;
import hudson.views.ListViewColumnDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

public class SCMBuildColumn extends ListViewColumn {

    @DataBoundConstructor
    public SCMBuildColumn() {
        super();
    }

    public String getHeader() {
        return "SCM Remote Ahead";
    }

    public String getSCMBuildData(Job job) {
        Run r = job.getLastCompletedBuild();
        SCMBuildAction action = r.getAction(SCMBuildAction.class);
        if (action != null) {
            return String.format("%s", action.getChangesBehindRemoteHead());
        }
        return String.format("N/A");
    }

    @Extension
    public static class DescriptorImpl extends ListViewColumnDescriptor {

        public DescriptorImpl() {
        }

        @Override
        public String getDisplayName() {
            return "SCM Remote Head Diff";
        }

        public boolean shownByDefault() {
            return true;
        }
    }
}