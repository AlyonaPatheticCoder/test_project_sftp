package ru.test.sftpclient;

import ru.test.sftpclient.model.DomainIp;
import ru.test.sftpclient.repository.DomainRepository;
import ru.test.sftpclient.repository.SftpDomainRepository;
import ru.test.sftpclient.service.DomainService;
import ru.test.sftpclient.sftp.SftpClient;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("SFTP Client");

        System.out.print("SFTP host: ");
        String host = scanner.nextLine();

        int port = readPort(scanner);

        System.out.print("Login: ");
        String login = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        SftpClient client = new SftpClient(host, port, login, password);

        try {
            client.connect();
            System.out.println("SFTP connection successful");

            System.out.print("Remote file path: ");
            String filePath = scanner.nextLine();

            DomainRepository repository = new SftpDomainRepository(client, filePath);

            DomainService service = new DomainService(repository);

            menu(scanner, service);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            client.disconnect();
            scanner.close();
        }
    }

    private static void menu(Scanner scanner, DomainService service) {

        while (true) {
            System.out.println();
            System.out.println("1. Show all domains");
            System.out.println("2. Find ip by domain");
            System.out.println("3. Find domain by ip");
            System.out.println("4. Add domain");
            System.out.println("5. Delete by domain");
            System.out.println("6. Delete by ip");
            System.out.println("0. Exit");
            System.out.print("Choose action: ");

            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1":
                        showAll(service);
                        break;

                    case "2":
                        findIp(scanner, service);
                        break;

                    case "3":
                        findDomain(scanner, service);
                        break;

                    case "4":
                        addDomain(scanner, service);
                        break;

                    case "5":
                        deleteByDomain(scanner, service);
                        break;

                    case "6":
                        deleteByIp(scanner, service);
                        break;

                    case "0":
                        return;

                    default:
                        System.out.println("Unknown action");
                }

            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void showAll(DomainService service) {

        List<DomainIp> domainIps = null;
        try {
            domainIps = service.getAll();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        if (domainIps.isEmpty()) {
            System.out.println("No domains found");
            return;
        }

        for (DomainIp pair : domainIps) {
            System.out.println(pair.getDomain() + " - " + pair.getIp());
        }
    }

    private static void findIp(Scanner scanner, DomainService service) {

        System.out.print("Domain: ");
        String domain = scanner.nextLine();

        Optional<String> ip = null;
        try {
            ip = service.findIpByDomain(domain);
            System.out.println(
                    ip.map(value -> "Ip: " + value).orElse("Domain not found")
            );
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }


    }

    private static void findDomain(Scanner scanner, DomainService service) {

        System.out.print("Ip: ");
        String ip = scanner.nextLine();

        Optional<String> domain = null;
        try {
            domain = service.findDomainByIp(ip);
            System.out.println(
                    domain.map(value -> "Domain: " + value).orElse("Ip not found")
            );
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }


    }

    private static void addDomain(Scanner scanner, DomainService service) {

        System.out.print("Domain: ");
        String domain = scanner.nextLine();

        System.out.print("Ip: ");
        String ip = scanner.nextLine();

        try {
            service.add(ip, domain);
            System.out.println("Pair added successfully");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void deleteByDomain(Scanner scanner, DomainService service) {

        System.out.print("Domain: ");
        String domain = scanner.nextLine();

        boolean removed = false;
        try {
            removed = service.deleteByDomain(domain);
            System.out.println(
                    removed ? "Pair deleted successfully" : "Domain not found"
            );
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }


    }

    private static void deleteByIp(Scanner scanner, DomainService service) {

        System.out.print("Ip: ");
        String ip = scanner.nextLine();

        boolean removed = false;
        try {
            removed = service.deleteByIp(ip);
            System.out.println(
                    removed ? "Pair deleted successfully." : "Ip not found"
            );
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }


    }

    private static int readPort(Scanner scanner) {

        while (true) {
            System.out.print("SFTP port: ");

            String input = scanner.nextLine();

            try {
                int port = Integer.parseInt(input);
                if (port > 0 && port <= 65535) {
                    return port;
                }

                System.out.println("Port must be between 1 and 65535");

            } catch (NumberFormatException e) {
                System.out.println("Invalid port");
            }
        }
    }
}