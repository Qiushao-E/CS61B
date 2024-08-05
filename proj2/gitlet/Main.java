package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Qiushao
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                checkArgsNumber(args,1);
                Repository.init();
                break;
            case "add":
                checkArgsNumber(args,2);
                Repository.add(args[1]);
                break;
            case "commit":
                if (args.length == 1) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                checkArgsNumber(args, 2);
                Repository.commit(args[1]);
                break;
            case "rm":
                checkArgsNumber(args, 2);
                Repository.remove(args[1]);
                break;
            case "log":
                // TODO: handle the `log` command
                break;
            case "global-log":
                // TODO: handle the `global-log` command
                break;
            case "find":
                // TODO: handle the `find` command
                break;
            case "status":
                // TODO: handle the `status` command
                break;
            case "checkout":
                // TODO: handle the `checkout` command
                break;
            case "branch":
                // TODO: handle the `branch` command
                break;
            case "rm-branch":
                // TODO: handle the `rm-branch` command
                break;
            case "reset":
                // TODO: handle the `reset` command
                break;
            case "merge":
                // TODO: handle the `merge` command
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
}
