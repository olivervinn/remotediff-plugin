package org.jenkinsci.scmdiff;
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

import hudson.model.Action;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides action to the captured information on the Build page
 */
public class RemoteDiffAction implements Action {

    public List<RemoteDiffInfo> buildInfo;

    public RemoteDiffAction() {
        buildInfo = new ArrayList<RemoteDiffInfo>();
    }

    public void addInfo(RemoteDiffInfo info) {
        buildInfo.add(info);
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
    public static class RemoteDiffInfo {
        public int changes_behind_remote_head;
        public String[] log;

        public RemoteDiffInfo(String[] log, int changes_behind_remote_head) {
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
        for (RemoteDiffInfo info : buildInfo) {
            last_behind_count = info.changes_behind_remote_head;
        }
        return last_behind_count;
    }
}
