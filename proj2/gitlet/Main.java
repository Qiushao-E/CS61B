package gitlet;

import java.util.Objects;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Qiushao
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        checkMain(args);
        switch (args[0]) {
            case "init":
                checkArgsNumber(args, 1);
                Repository.init();
                break;
            case "add":
                checkArgsNumber(args, 2);
                Repository.add(args[1]);
                break;
            case "commit":
                checkCommit(args);
                Repository.commit(args[1]);
                break;
            case "rm":
                checkArgsNumber(args, 2);
                Repository.remove(args[1]);
                break;
            case "log":
                checkArgsNumber(args, 1);
                Repository.log();
                break;
            case "global-log":
                checkArgsNumber(args, 1);
                Repository.globalLog();
                break;
            case "find":
                checkArgsNumber(args, 2);
                Repository.find(args[1]);
                break;
            case "status":
                checkArgsNumber(args, 1);
                Repository.status();
                break;
            case "checkout":
                checkout(args);
                break;
            case "branch":
                checkArgsNumber(args, 2);
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                checkArgsNumber(args, 2);
                Repository.removeBranch(args[1]);
                break;
            case "reset":
                checkArgsNumber(args, 2);
                Repository.reset(args[1]);
                break;
            case "merge":
                checkArgsNumber(args, 2);
                Repository.merge(args[1]);
                break;
            case "add-remote":
                checkArgsNumber(args, 3);
                Repository.addRemote(args[1], args[2]);
                break;
            case "rm-remote":
                checkArgsNumber(args, 2);
                Repository.removeRemote(args[1]);
                break;
            case "push":
                checkArgsNumber(args, 3);
                Repository.push(args[1], args[2]);
                break;
            case "fetch":
                checkArgsNumber(args, 3);
                Repository.fetch(args[1], args[2]);
                break;
            case "pull":
                checkArgsNumber(args, 3);
                Repository.pull(args[1], args[2]);
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }

    private static void checkArgsNumber(String[] args, int number) {
        if (args.length != number) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    private static void checkMain(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
    }

    private static void checkout(String[] args) {
        if (args.length == 3) {
            if (Objects.equals(args[1], "--")) {
                Repository.checkoutHeadFile(args[2]);
            } else {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
        } else if (args.length == 4) {
            if (Objects.equals(args[2], "--")) {
                Repository.checkoutCommitFile(args[1], args[3]);
            } else {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
        } else if (args.length == 2) {
            Repository.checkoutBranch(args[1]);
        } else {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    private static void checkCommit(String[] args) {
        if (args.length == 1 || args.length == 2 && args[1].isEmpty()) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        checkArgsNumber(args, 2);
    }
}
