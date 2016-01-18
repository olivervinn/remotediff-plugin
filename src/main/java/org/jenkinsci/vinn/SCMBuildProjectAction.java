package org.jenkinsci.vinn;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.ProminentProjectAction;

/**
 * This is the project page presentation
 */
public class SCMBuildProjectAction implements ProminentProjectAction {

    public final AbstractProject<?, ?> project;

    /**
     * @return Null means not shown otherwise display name shown with icon
     */
    @Override
    public String getIconFileName() {
        return "/plugin/ovinn/images/16x16/scminfo.png";
        //return "/plugin/ovinn/images/64x64/one-icon.png";
    }

    @Override
    public String getDisplayName() {
        int num = getLastBuildAction().getChangesBehindRemoteHead();
        if (num == 0) {
            return String.format("Remote Not Ahead");
        }
        else {
            return String.format("Remote Ahead By %s Changes", num);
        }
    }

    /**
     * @return Null means no link otherwise provide jelly files to render
     */
    @Override
    public String getUrlName() {
        return null;
    }

    public SCMBuildProjectAction(AbstractProject<?, ?> project) {
        this.project = project;
    }

    /**
     * @return the last build action associated with this project.
     */
    public SCMBuildAction getLastBuildAction() {
        for (AbstractBuild<?, ?> b = project.getLastCompletedBuild(); b != null; b = b.getPreviousBuild()) {
            SCMBuildAction action = b.getAction(SCMBuildAction.class);
            if (action != null) {
                return action;
            }
        }
        return null;
    }
}
