/*
 * The MIT License
 * Copyright 2016 Oliver Vinn.
 * http://opensource.org/licenses/MIT
 */

package org.jenkinsci.remotediff.ui;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.ProminentProjectAction;
import org.jenkinsci.remotediff.core.RemoteDiffAction;

/**
 * This is the project page presentation
 */
public class RemoteDiffReport implements ProminentProjectAction {

    public final AbstractProject<?, ?> project;
    AbstractBuild<?, ?> build;

    public RemoteDiffReport(AbstractProject<?, ?> project) {
        this.project = project;
    }

    @Override
    public String getIconFileName() {
        return "/plugin/remotediff/images/16x16/logo.png";
    }

    @Override
    public String getUrlName() {
        return "remotediff";
    }

    @Override
    public String getDisplayName() {
        RemoteDiffAction r = getLastBuildAction();
        if (r == null) {
            return "Remote UnChecked";
        } else {
            int num = r.getChangesBehindRemoteHead();
            if (num == 0) return "Remote Not Ahead";
            else {
                return String.format("Remote Ahead By %s Changes", num);
            }
        }
    }

    public AbstractBuild<?, ?> getBuild() {
        return project.getLastCompletedBuild();
    }

    public RemoteDiffAction getLastBuildAction() {
        for (AbstractBuild<?, ?> b = project.getLastCompletedBuild(); b != null; b = b.getPreviousBuild()) {
            RemoteDiffAction action = b.getAction(RemoteDiffAction.class);
            if (action != null) {
                return action;
            }
        }
        return null;
    }
}
