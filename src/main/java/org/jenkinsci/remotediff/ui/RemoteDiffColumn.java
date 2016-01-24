/*
 * The MIT License
 * Copyright 2016 Oliver Vinn.
 * http://opensource.org/licenses/MIT
 */

package org.jenkinsci.remotediff.ui;

import hudson.Extension;
import hudson.model.Job;
import hudson.model.Run;
import hudson.views.ListViewColumn;
import hudson.views.ListViewColumnDescriptor;
import org.jenkinsci.remotediff.core.RemoteDiffAction;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;

public class RemoteDiffColumn extends ListViewColumn {

    @DataBoundConstructor
    public RemoteDiffColumn() {
        super();
    }

    public String getHeader() {
        return "Remote Diff";
    }

    public String getChangeCount(Job job) {
        Run r = job.getLastCompletedBuild();
        RemoteDiffAction action = r.getAction(RemoteDiffAction.class);
        if (action != null) {
            return String.format("%s", action.getChangesBehindRemoteHead());
        }
        return "N/A";
    }

    @Extension
    public static class DescriptorImpl extends ListViewColumnDescriptor {

        public DescriptorImpl() {
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "SCM Remote Diff";
        }

        public boolean shownByDefault() {
            return true;
        }
    }
}