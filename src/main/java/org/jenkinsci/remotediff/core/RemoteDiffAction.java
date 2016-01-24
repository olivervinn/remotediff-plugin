/*
 * The MIT License
 * Copyright 2016 Oliver Vinn.
 * http://opensource.org/licenses/MIT
 */

package org.jenkinsci.remotediff.core;

import hudson.model.AbstractBuild;
import hudson.model.Action;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides action to the captured information on the Build page
 */
public class RemoteDiffAction implements Action {

    AbstractBuild<?, ?> build;
    public List<RemoteDiffInfo> buildInfo;

    public RemoteDiffAction(AbstractBuild<?, ?> build) {
        buildInfo = new ArrayList<RemoteDiffInfo>();
        this.build = build;
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public String getUrlName() {
        return null;
    }

    public void addInfo(RemoteDiffInfo info) {
        buildInfo.add(info);
    }

    public AbstractBuild<?, ?> getBuild() {
        return this.build;
    }

    public int getChangesBehindRemoteHead() {
        int last_behind_count = 0;
        for (RemoteDiffInfo info : buildInfo) {
            last_behind_count = info.getChangesBehindRemote();
        }
        return last_behind_count;
    }

    public boolean getIsEmailThresholdTriggered() {
        for (RemoteDiffInfo info : buildInfo) {
            if (info.getEmail())
                return true;
        }
        return false;
    }

    public String[] getLog() {
        for (RemoteDiffInfo info : buildInfo) {
            return info.log;
        }
        return null;
    }

    /**
     * Data class to store data from slaves.
     */
    public static class RemoteDiffInfo {
        private int changes_behind_remote;
        public String[] log;
        private boolean trigger_email;
        private boolean trigger_abort;

        public RemoteDiffInfo(String[] log, int changes_behind_remote, boolean trigger_email, boolean trigger_abort) {
            this.log = log;
            this.changes_behind_remote = changes_behind_remote;
            this.trigger_email = trigger_email;
            this.trigger_abort = trigger_abort;
        }

        public int getChangesBehindRemote() {
            return this.changes_behind_remote;
        }

        public boolean getAbort() {
            return this.trigger_abort;
        }

        public boolean getEmail() {
            return this.trigger_email;
        }

        @Override
        public String toString() {
            return String.format("%s", changes_behind_remote);
        }
    }
}
