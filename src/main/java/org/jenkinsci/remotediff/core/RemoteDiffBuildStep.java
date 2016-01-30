/*
 * The MIT License
 * Copyright 2016 Oliver Vinn.
 * http://opensource.org/licenses/MIT
 */

package org.jenkinsci.remotediff.core;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BatchFile;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.tasks.Shell;
import hudson.util.FormValidation;
import org.jenkinsci.remotediff.ui.RemoteDiffBadge;
import org.jenkinsci.remotediff.ui.RemoteDiffReport;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a build action
 */
public class RemoteDiffBuildStep extends Builder {

    static final String DISPLAY_NAME = "Remote Diff Check";

    public final String cmd;
    public final String regex;
    public final int emailThreshold;
    public final int abortThreshold;

    @DataBoundConstructor
    public RemoteDiffBuildStep(final String cmd, final String regex, final int emailThreshold, final int abortThreshold) {
        this.cmd = cmd;
        this.regex = regex;
        this.emailThreshold = emailThreshold;
        this.abortThreshold = abortThreshold;
    }

    @Nonnull
    @Override
    public Collection<? extends Action> getProjectActions(AbstractProject<?, ?> project) {
        return Collections.singleton(new RemoteDiffReport(project));
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {

        ByteArrayOutputStream outputparser = new ByteArrayOutputStream();
        StreamBuildListener tasklistener = new StreamBuildListener(outputparser);

        String[] log;
        if (launcher.isUnix()) {
            Shell sh = new Shell(this.cmd);
            boolean ok = sh.perform(build, launcher, tasklistener);
            log = outputparser.toString().split("\n");
        } else {
            BatchFile bat = new BatchFile(this.cmd);
            boolean ok = bat.perform(build, launcher, tasklistener);
            log = outputparser.toString().split("\r\n");
        }

        int number_of_changes = 0;
        Pattern p = Pattern.compile(this.regex);
        for (String l : log) {
            Matcher matcher = p.matcher(l);
            if (matcher.matches()) {
                number_of_changes = Integer.parseInt(matcher.group(1));
                break;
            }
        }

        RemoteDiffAction.RemoteDiffInfo info = new RemoteDiffAction.RemoteDiffInfo(
                log,
                number_of_changes,
                number_of_changes >= emailThreshold,
                number_of_changes >= abortThreshold);

        listener.getLogger().println(String.format("%s - Behind %d (Email:%s, Abort:%s)",
                DISPLAY_NAME,
                info.getChangesBehindRemote(),
                info.getEmail(),
                info.getAbort()));

        RemoteDiffAction action = build.getAction(RemoteDiffAction.class);
        if (action != null) {
            action.addInfo(info);
        } else {
            action = new RemoteDiffAction(build);
            action.addInfo(info);
        }

        // Add Record and UI
        build.addAction(action);
        build.addAction(new RemoteDiffBadge(info.getChangesBehindRemote()));

        // Abort the remaining build steps
        if (info.getAbort()) {
            throw new InterruptedException();
        }

        return true;
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Builder> {

        @Nonnull
        @Override
        public String getDisplayName() {
            return DISPLAY_NAME;
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        public FormValidation doCheckRegex(@AncestorInPath final AbstractProject<?, ?> project,
                                           @QueryParameter("regex") final String regex) throws IOException {
            try {
                Pattern p = Pattern.compile(regex);
                if (!regex.contains("(\\d+)")) {
                    return FormValidation.error("Invalid must contain (\\d+) to get the number");
                }
                return FormValidation.ok();
            } catch (Exception e) {
                return FormValidation.error("Invalid Regex");
            }
        }
    }
}
