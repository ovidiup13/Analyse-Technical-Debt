import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vcs.GitProcessor;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class App {

    private static Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {

        String tmp = Paths.get(System.getProperty("user.dir"), "tmp").toString();
        File repoPath = new File(Paths.get(tmp, "zeppelin", ".git").toString());
        String uri = "https://github.com/apache/zeppelin.git";

        try (GitProcessor zeppelin = new GitProcessor(uri, repoPath)) {
            // zeppelin = new GitProcessor(uri, repoPath);
        } catch (InvalidRemoteException e) {
            LOGGER.error("InvalidRemoteException", e);
        } catch (TransportException e) {
            LOGGER.error("TransportException", e);
        } catch (GitAPIException e) {
            LOGGER.error("GitAPIException", e);
        } catch (JGitInternalException e) { // destination exists
            LOGGER.error("JGitInternalException", e);
            // open repository
        }

        // try {
        //     repository = GitProcessor.openProject(repoPath);
        //     // System.out.println(zeppelin.getRepository().getDirectory().toPath().toString());
        //     // System.out.println(zeppelin.getRepository().getBranch());

        //     zeppelin = new Git(repository);
        //     zeppelin.checkout().setStartPoint("7af4fab420ed42edbe9f97c1c4d63823ff321c2d").call();
        //     // Ref head = repository.exactRef("refs/heads/master");
        //     // System.out.println("Ref of refs/heads/master: " + head);
        //     // List<Ref> call = zeppelin.branchList().call();
        //     // System.out.println(call);
        //     // for (Ref ref : call) {
        //     //     System.out.println("Branch: " + ref + " " + ref.getName() + " " + ref.getObjectId().getName());
        //     //     break;
        //     // }
        // } catch (IOException | GitAPIException e) {
        //     // TODO Auto-generated catch block
        //     LOGGER.error("IOException", e);
        // } finally {
        //     zeppelin.close();
        // }

        // try {
        //     Iterable<RevCommit> commits = zeppelin.log().all().call();
        //     int count = 0;
        //     for (RevCommit commit : commits) {
        //         System.out.println("LogCommit: " + commit);
        //         // listDiff(repository, zeppelin, commit.getName() + "^", commit.getName());
        //         count++;
        //         if (count == 10) {
        //             break;
        //         }
        //     }
        //     System.out.println(count);
        // } catch (Exception e) {
        //     LOGGER.error("Exception", e);
        // }
    }

}
