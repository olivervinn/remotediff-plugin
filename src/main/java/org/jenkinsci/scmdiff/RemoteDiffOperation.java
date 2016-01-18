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

import com.sun.jna.StringArray;
import hudson.FilePath;
import hudson.remoting.Callable;
import hudson.remoting.VirtualChannel;
import jenkins.security.Roles;
import org.jenkinsci.remoting.RoleChecker;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses data
 */
public class RemoteDiffOperation implements FilePath.FileCallable<RemoteDiffAction.RemoteDiffInfo> {

    final String cmd;

    public RemoteDiffOperation(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public RemoteDiffAction.RemoteDiffInfo invoke(File f, VirtualChannel channel) throws IOException, InterruptedException {
        RemoteDiffAction.RemoteDiffInfo info;
        info = channel.call(new Callable<RemoteDiffAction.RemoteDiffInfo, RuntimeException>() {
            @Override
            public void checkRoles(RoleChecker roleChecker) throws SecurityException {
                roleChecker.check(this, Roles.SLAVE);
            }

            public RemoteDiffAction.RemoteDiffInfo call() {
                int number_of_changes = 0;
                List<String> log = new ArrayList<String>();
                Runtime rt = Runtime.getRuntime();
                try {
                    Process pr = rt.exec(cmd);
                    number_of_changes = pr.waitFor();
                    InputStream stdin = pr.getInputStream();
                    InputStreamReader isr = new InputStreamReader(stdin);
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    while ((line = br.readLine()) != null)
                        log.add(line);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return new RemoteDiffAction.RemoteDiffInfo(log.toArray(new String[log.size()]), number_of_changes);
            }
        });
        return info;
    }

    @Override
    public void checkRoles(RoleChecker roleChecker) throws SecurityException {

    }
}
