package utils;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class Utils {

    /**
     * Opens an existing repository.
     */
    public static Repository openRepository(File repoFile) throws IOException {
        System.out.println(repoFile.toPath().toString());
        return new FileRepositoryBuilder().setGitDir(repoFile).readEnvironment().findGitDir().build();
    }

    /**
     * Clones a repository from URI to a path.
     */
    public static Git cloneRepository(String uri, File path)
            throws InvalidRemoteException, TransportException, GitAPIException {
        return Git.cloneRepository().setURI(uri).setDirectory(path).call();
    }

}