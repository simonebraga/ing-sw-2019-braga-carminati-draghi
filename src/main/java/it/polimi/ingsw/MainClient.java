package it.polimi.ingsw;

//TODO Javadoc
public class MainClient {

    public static void main(String[] args) {

        String[] newArgs = new String[2];
        int interfaceType = -1;

        for (int i = 0 ; i < args.length ; i++) {
            if ((i+1 < args.length) && (args[i+1].toCharArray()[0] == '-')) {
                System.out.println("Error parsing arguments");
                System.exit(0);
            }
            switch (args[i]) {
                case "-serverIp": {
                    if (newArgs[0] == null) {
                        i++;
                        if (i < args.length)
                            newArgs[0] = args[i];
                        else {
                            System.out.println("Error parsing arguments");
                            System.exit(0);
                        }
                    } else {
                        System.out.println("Error parsing arguments");
                        System.exit(0);
                    }
                    break;
                }
                case "-clientIp": {
                    if (newArgs[2] == null) {
                        i++;
                        if (i < args.length)
                            newArgs[1] = args[i];
                        else {
                            System.out.println("Error parsing arguments");
                            System.exit(0);
                        }
                    } else {
                        System.out.println("Error parsing arguments");
                        System.exit(0);
                    }
                    break;
                }
                case "-interface": {

                    if (interfaceType != -1) {
                        System.out.println("Error parsing arguments");
                        System.exit(0);
                    }

                    i++;
                    if (i < args.length) {
                        switch (args[i].toLowerCase()) {
                            case "gui": {
                                interfaceType = 0;
                                break;
                            }
                            case "cli": {
                                interfaceType = 1;
                                break;
                            }
                        }
                    } else {
                        System.out.println("Error parsing arguments");
                        System.exit(0);
                    }
                    break;
                }
                default: {
                    System.out.println("Error parsing arguments");
                    System.exit(0);
                }
            }
        }
        switch (interfaceType) {
            case 1: {
                System.out.println("Started CLI with arguments:");
                for (String s : newArgs)
                    System.out.println(s);
                break;
            }
            case 0:
            default: {
                System.out.println("Started GUI with arguments:");
                for (String s : newArgs)
                    System.out.println(s);
                break;
            }
        }
    }
}
