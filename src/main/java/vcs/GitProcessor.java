package vcs;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

public class GitProcessor implements AutoCloseable {

    private Git gitProject;

    /***
     * Instantiate a new GitProcessor by using an existing repository.
     */
    public GitProcessor(File path) throws IOException {
        this.gitProject = openProject(path);
    }

    /***
     * Instantiate a new GitProcessor by cloning a repository from a URI.
     */
    public GitProcessor(String uri, File path) throws InvalidRemoteException, TransportException, GitAPIException {
        this.gitProject = cloneProject(uri, path);
    }

    /**
    * Opens an existing repository.
    */
    private Git openProject(File repoFile) throws IOException {
        return new Git(new FileRepositoryBuilder().setGitDir(repoFile).readEnvironment().findGitDir().build());
    }

    /**
     * Clones a repository from URI to a path.
     */
    private Git cloneProject(String uri, File path) throws InvalidRemoteException, TransportException, GitAPIException {
        return Git.cloneRepository().setURI(uri).setDirectory(path).call();
    }

    public void listDiff(String oldCommit, String newCommit) throws GitAPIException, IOException {
        Repository repository = gitProject.getRepository();
        final List<DiffEntry> diffs = gitProject.diff().setOldTree(prepareTreeParser(repository, oldCommit))
                .setNewTree(prepareTreeParser(repository, newCommit)).call();

        System.out.println("Found: " + diffs.size() + " differences");
        for (DiffEntry diff : diffs) {
            System.out.println("Diff: " + diff.getChangeType() + ": "
                    + (diff.getOldPath().equals(diff.getNewPath()) ? diff.getNewPath()
                            : diff.getOldPath() + " -> " + diff.getNewPath()));
        }
    }

    private static AbstractTreeIterator prepareTreeParser(Repository repository, String objectId) throws IOException {
        // from the commit we can build the tree which allows us to construct the TreeParser
        //noinspection Duplicates
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(repository.resolve(objectId));
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, tree.getId());
            }

            walk.dispose();

            return treeParser;
        }
    }

    @Override
    public void close() throws Exception {
        this.gitProject.close();
    }

}