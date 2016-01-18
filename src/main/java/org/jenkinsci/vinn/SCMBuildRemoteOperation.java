package org.jenkinsci.vinn;

import hudson.FilePath;
import hudson.remoting.Callable;
import hudson.remoting.VirtualChannel;
import jenkins.security.Roles;
import org.jenkinsci.remoting.RoleChecker;

import java.io.*;

/**
 * Parses data
 */
public class SCMBuildRemoteOperation implements FilePath.FileCallable<SCMBuildAction.SCMBuildInfo> {

    final String cmd;

    public SCMBuildRemoteOperation(String cmd)
    {
        this.cmd = cmd;
    }

    @Override
    public SCMBuildAction.SCMBuildInfo invoke(File f, VirtualChannel channel) throws IOException, InterruptedException {
        SCMBuildAction.SCMBuildInfo systemProperties = null;

        systemProperties = channel.call(new Callable<SCMBuildAction.SCMBuildInfo, RuntimeException>() {
            @Override
            public void checkRoles(RoleChecker roleChecker) throws SecurityException {
                roleChecker.check(this, Roles.SLAVE);
            }

            public SCMBuildAction.SCMBuildInfo call() {
                Runtime rt = Runtime.getRuntime();
                int ee = 0;
                String log = "";
                try {
                    Process pr = rt.exec(cmd);
                     ee = pr.waitFor();
                    InputStream stdin = pr.getInputStream();
                    InputStreamReader isr = new InputStreamReader(stdin);

                    BufferedReader br = new BufferedReader(isr);
                    String line = null;
                    while ( (line = br.readLine()) != null)
                        log += line + "<br/>";
                    Object x = pr.getErrorStream();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return new SCMBuildAction.SCMBuildInfo(log, ee);
            }
        });
        return systemProperties;
    }

    @Override
    public void checkRoles(RoleChecker roleChecker) throws SecurityException {

    }
}
