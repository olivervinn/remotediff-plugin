/*
 * The MIT License
 * Copyright 2016 Oliver Vinn.
 * http://opensource.org/licenses/MIT
 */

package org.jenkinsci.remotediff.ui;

import hudson.model.BuildBadgeAction;

public class RemoteDiffBadge implements BuildBadgeAction {

    final int count;

    public RemoteDiffBadge(int changes_behind_remote_head) {
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
        return "/plugin/remotediff/images/16x16/logo.png";
    }

    public int getCount() {
        return this.count;
    }
}
