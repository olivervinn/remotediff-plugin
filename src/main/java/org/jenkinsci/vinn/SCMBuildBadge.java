package org.jenkinsci.vinn;

import hudson.model.BuildBadgeAction;

/**
 * Created by ovinn on 17/01/2016.
 */
public class SCMBuildBadge implements BuildBadgeAction {

    final int count;

    public SCMBuildBadge(int changes_behind_remote_head) {
        this.count = changes_behind_remote_head;
    }

    @Override
    public String getUrlName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "Remote Ahead By " + count;
    }

    @Override
    public String getIconFileName() {
        return "/plugin/ovinn/images/16x16/scminfo.png";
    }

    public int getCount() {
        return this.count;
    }
}
