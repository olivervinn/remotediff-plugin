package org.jenkinsci.vinn;

import hudson.model.Action;

import java.util.ArrayList;
import java.util.List;

/**
 * This provides action to the captured information on the Build page
 */
public class SCMBuildAction implements Action {

    public List<SCMBuildInfo> buildInfo;

    public SCMBuildAction() {
        buildInfo = new ArrayList<SCMBuildInfo>();
    }

    public void addInfo(SCMBuildInfo info) {
        buildInfo.add(info);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (SCMBuildInfo info : buildInfo) {
            builder.append(info + "<br/>");
        }
        return builder.toString();
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

    /**
     * Data class to store data from slaves.
     */
    public static class SCMBuildInfo {
        public int changes_behind_remote_head;
        public String log;

        public SCMBuildInfo(String log, int changes_behind_remote_head) {
            this.log = log;
            this.changes_behind_remote_head = changes_behind_remote_head;
        }

        @Override
        public String toString() {
            return String.format("%s", changes_behind_remote_head);
        }
    }

    public int getChangesBehindRemoteHead() {
        int last_behind_count = 0;
        for (SCMBuildInfo info : buildInfo) {
            last_behind_count = info.changes_behind_remote_head;
        }
        return last_behind_count;
    }
}
