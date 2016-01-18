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

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.io.IOException;

/**
 * This is a build action
 */
public class RemoteDiffBuilder extends Builder {

    static final String DISPLAY_NAME = "Remote Diff Check";
    public final String cmd;
    public final int threshold;

    @Extension
    public static class FirstBuilderImpl extends BuildStepDescriptor<Builder> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> proj) {
            return true;
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return DISPLAY_NAME;
        }
    }

    @DataBoundConstructor
    public RemoteDiffBuilder(final String cmd, final int threshold) {
        this.cmd = cmd;
        this.threshold = threshold;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        listener.getLogger().println(DISPLAY_NAME);

        RemoteDiffAction.RemoteDiffInfo info = build.getWorkspace().act(new RemoteDiffOperation(this.cmd));
        Pattern p = Pattern.compile(".*Total Behind (\\d+).*");
        for (String s:
            info.log) {
            listener.getLogger().println(s);
        }
        Matcher matcher = p.matcher(Arrays.toString(info.log));
        if (matcher.matches()) {
            info.changes_behind_remote_head = Integer.parseInt(matcher.group(1));
            listener.getLogger().println("Captured value of: " + info.changes_behind_remote_head);
        }

        RemoteDiffAction action = build.getAction(RemoteDiffAction.class);
        if (action != null) {
            action.addInfo(info);
        } else {
            action = new RemoteDiffAction();
            action.addInfo(info);
            build.addAction(action);
            build.addAction(new RemoteDiffBadge(info.changes_behind_remote_head));
        }
        return true;
    }

    @Override
    public Action getProjectAction(AbstractProject<?, ?> project) {
        return new RemoteDiffProjectAction(project);
    }
}
