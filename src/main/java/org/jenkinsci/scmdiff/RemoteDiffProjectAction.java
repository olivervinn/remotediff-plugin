/*
 * The MIT License
 *
 * Copyright 2016 Oliver Vinn.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jenkinsci.scmdiff;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.ProminentProjectAction;

/**
 * This is the project page presentation
 */
public class RemoteDiffProjectAction implements ProminentProjectAction {

    public final AbstractProject<?, ?> project;

    /**
     * @return Null means not shown otherwise display name shown with icon
     */
    @Override
    public String getIconFileName() {
        return "/plugin/ovinn/images/16x16/scminfo.png";
    }

    @Override
    public String getDisplayName() {
        RemoteDiffAction r = getLastBuildAction();
        if (r == null) {
            return "Remote Not Yet Checked";
        } else {
            int num = r.getChangesBehindRemoteHead();
            if (num == 0) return "Remote Not Ahead";
            else {
                return String.format("Remote Ahead By %s Changes", num);
            }
        }
    }

    /**
     * @return Null means no link otherwise provide jelly files to render
     */
    @Override
    public String getUrlName() {
        return null;
    }

    public RemoteDiffProjectAction(AbstractProject<?, ?> project) {
        this.project = project;
    }

    /**
     * @return the last build action associated with this project.
     */
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
