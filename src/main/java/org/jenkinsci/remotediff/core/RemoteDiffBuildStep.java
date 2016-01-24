/*
 * The MIT License
 * Copyright 2016 Oliver Vinn.
 * http://opensource.org/licenses/MIT
 */

package org.jenkinsci.remotediff.core;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import org.jenkinsci.remotediff.ui.RemoteDiffBadge;
import org.jenkinsci.remotediff.ui.RemoteDiffReport;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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

        // API recommends throwing error rather than catching
        Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec(this.cmd);
        int number_of_changes = pr.waitFor();

        // Get process output
        InputStream stdin = pr.getInputStream();
        InputStreamReader isr = new InputStreamReader(stdin);
        BufferedReader br = new BufferedReader(isr);

        // Match
        Pattern p = Pattern.compile(this.regex);
        List<String> log = new ArrayList<String>();

        String line;
        while ((line = br.readLine()) != null) {
            Matcher matcher = p.matcher(line);
            if (matcher.matches()) {
                number_of_changes = Integer.parseInt(matcher.group(1));
            }
            log.add(line);
        }

        // Record
        RemoteDiffAction.RemoteDiffInfo info = new RemoteDiffAction.RemoteDiffInfo(
                log.toArray(new String[log.size()]),
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
