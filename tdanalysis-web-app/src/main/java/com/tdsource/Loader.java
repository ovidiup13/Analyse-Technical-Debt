package com.tdsource;

import com.tdsource.vcs.GitProcessor;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;

public class Loader {

    private static Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
         String tmp = Paths.get(System.getProperty("user.dir"), "tmp").toString();
         File repoPath = new File(Paths.get(tmp, "zeppelin").toString());
         String uri = "https://github.com/apache/zeppelin.git";

         try (GitProcessor zeppelin = new GitProcessor(repoPath)) {
             System.out.println(zeppelin.getCommits().size());
//         } catch (InvalidRemoteException e) {
//             LOGGER.error("InvalidRemoteException", e);
//         } catch (TransportException e) {
//             LOGGER.error("TransportException", e);
//         } catch (GitAPIException e) {
//             LOGGER.error("GitAPIException", e);
         } catch (JGitInternalException e) { // destination exists
             LOGGER.error("JGitInternalException", e);
             // open repository
         } catch (Exception e) {
             LOGGER.error("Exception", e);
         }
    }
}
