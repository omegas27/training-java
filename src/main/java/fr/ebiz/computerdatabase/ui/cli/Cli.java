package fr.ebiz.computerdatabase.ui.cli;

import fr.ebiz.computerdatabase.dto.ComputerDto;
import fr.ebiz.computerdatabase.dto.paging.Page;
import fr.ebiz.computerdatabase.dto.paging.Pageable;
import fr.ebiz.computerdatabase.model.Company;
import fr.ebiz.computerdatabase.service.CompanyService;
import fr.ebiz.computerdatabase.service.ComputerService;
import fr.ebiz.computerdatabase.service.impl.CompanyServiceImpl;
import fr.ebiz.computerdatabase.service.impl.ComputerServiceImpl;
import fr.ebiz.computerdatabase.ui.cli.printer.factory.PrettyPrintFactory;
import fr.ebiz.computerdatabase.utils.StringUtils;

import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;

public class Cli {

    private static final int DEFAULT_PAGE = 1;
    private static final int MAX_ELEMENTS = 20;
    private final ComputerService computerService;
    private final CompanyService companyService;
    private final Scanner scanner;

    /**
     * Constructor.
     */
    private Cli() {
        this.computerService = ComputerServiceImpl.getInstance();
        this.companyService = CompanyServiceImpl.getInstance();
        this.scanner = new Scanner(System.in);
    }

    /**
     * CLI application main entry point.
     *
     * @param args The application execution parameters
     */
    public static void main(String... args) {
        new Cli().start();
    }

    /**
     * Start the CLI application and wait for input from the user.
     */
    private void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("\nThanks for using the Computer Database CLI application")));
        Command command;

        System.out.println("Hello, welcome to the Computer Database CLI application");
        help();

        do {
            System.out.print("\nAction : ");
            command = Command.getCommand(scanner.nextLine());

            if (command != null) {
                handleCommand(command);
            } else {
                System.out.println("Invalid command. Please refer to the \"help\" command");
            }

        } while (Command.QUIT != command);
    }

    /**
     * Handle the command depending on what the user inputted.
     *
     * @param command The command
     */
    private void handleCommand(Command command) {
        switch (command) {
            default:
            case HELP:
                help();
                break;
            case QUIT:
                System.exit(0);
            case LIST_COMPANIES:
                System.out.println("Here is the list of companies");
                System.out.println(PrettyPrintFactory.getInstance()
                        .make(Company.class).printList(companyService.getAll(Pageable.builder().page(DEFAULT_PAGE).elements(Integer.MAX_VALUE).build()).getElements()));
                break;
            case LIST_COMPUTERS:
                listComputers();
                break;
            case SHOW_COMPUTER:
                showComputerDetail();
                break;
            case ADD_COMPUTER:
                addComputer();
                break;
            case UPDATE_COMPUTER:
                updateComputer();
                break;
            case DELETE_COMPUTER:
                deleteComputer();
                break;
        }
    }

    /**
     * Handle the LIST_COMPUTERS command.
     */
    private void listComputers() {
        int currentPage = DEFAULT_PAGE;
        Page<ComputerDto> page = null;

        System.out.println("Here is the list of computers");
        do {
            if (page == null || page.getCurrentPage() != currentPage) {
                page = computerService.getAll(null, Pageable.builder().page(currentPage).elements(MAX_ELEMENTS).build());
            }
            System.out.println(PrettyPrintFactory.getInstance()
                    .make(ComputerDto.class).printList(page.getElements()));

            System.out.print("Choose a page between 1 and " + page.getTotalPages() + " or \"quit\" to go back : ");
            String input = scanner.nextLine();
            if (Command.QUIT.getCommandString().equals(StringUtils.cleanString(input))) {
                return;
            } else {
                if (StringUtils.isNumeric(input)) {
                    int readPage = Integer.parseInt(input);
                    if (readPage < 1 || readPage > page.getTotalPages()) {
                        System.out.println("Page is out of range. It must be between 1 and " + page.getTotalPages());
                    } else {
                        currentPage = readPage;
                    }
                } else {
                    System.out.println("Page must be an integer !");
                }
            }
        } while (true);
    }

    /**
     * Handle the HELP command by displaying the list of available commands.
     */
    private void help() {
        System.out.println("Here is the list of command you can use :");
        Arrays.stream(Command.values()).forEach(command ->
                System.out.println("\t" + command.getCommandString() + " : " + command.getHelpMessage()));
    }

    /**
     * Handle the SHOW_COMPUTER command.
     */
    private void showComputerDetail() {
        ComputerDto computer = readComputer("Id of the computer to show : ");
        System.out.println(PrettyPrintFactory.getInstance().make(ComputerDto.class).printDetail(computer));
    }

    /**
     * Handle the ADD_COMPUTER command.
     */
    private void addComputer() {

        ComputerDto.ComputerDtoBuilder computerBuilder = ComputerDto.builder()
                .name(PrinterUtils.readString(scanner,
                        "Name of the computer* : ",
                        true))
                .introduced(PrinterUtils.readDate(scanner,
                        "Introduced in (dd/mm/yyyy) or empty (no date) : ",
                        false))
                .discontinued(PrinterUtils.readDate(scanner,
                        "Discontinued in (dd/mm/yyyy) or empty (no date) : ",
                        false));

        Company company = readCompany(false);
        ComputerDto computer = computerBuilder.companyId(company == null ? null : company.getId()).build();
        computerService.insert(computer);
        System.out.println(computer.toString() + " was inserted successfully");
    }

    /**
     * Handle the UPDATE_COMPUTER command.
     */
    private void updateComputer() {
        ComputerDto computer = readComputer("Id of the computer to update : ");

        ComputerDto.ComputerDtoBuilder computerBuilder = ComputerDto.builder()
                .id(computer.getId())
                .name(PrinterUtils.readString(scanner,
                        "Name of the computer* [" + computer.getName() + "]: ",
                        true))
                .introduced(PrinterUtils.readDate(scanner,
                        "Introduced in (dd/mm/yyyy) or empty (no date) [" + computer.getIntroduced() + "] : ",
                        false))
                .discontinued(PrinterUtils.readDate(scanner,
                        "Discontinued in (dd/mm/yyyy) or empty (no date) [" + computer.getDiscontinued() + "] : ",
                        false));

        Company company = readCompany(false);

        ComputerDto computerToUpdate = computerBuilder.companyId(company == null ? null : company.getId()).build();
        computerService.update(computerToUpdate);
        System.out.println(computerToUpdate.toString() + " was updated successfully");
    }

    /**
     * Handle the DELETE_COMPUTER command.
     */
    private void deleteComputer() {
        ComputerDto computer = readComputer("Id of the computer to delete : ");
        computerService.delete(computer);
        System.out.println(computer.toString() + " was deleted successfully");
    }

    /**
     * Read a computer from the scanner.
     * This method checks the computer exists in the database
     *
     * @param label The label to display till the input is valid
     * @return the read computer
     */
    private ComputerDto readComputer(String label) {
        do {
            System.out.print(label);
            String input = scanner.nextLine();
            if (StringUtils.isNumeric(input)) {
                int computerId = Integer.parseInt(input);
                Optional<ComputerDto> computer = computerService.get(computerId);
                if (computer.isPresent()) {
                    return computer.get();
                } else {
                    System.out.println("Computer does not exist !");
                }
            } else {
                System.out.println("Invalid computer id !");
            }
        } while (true);
    }

    /**
     * Read a company from the scanner.
     * This method checks the company id exists in the database and return it
     *
     * @param mandatory Is the company id mandatory ?
     * @return the read company or null if it is not mandatory
     */
    private Company readCompany(boolean mandatory) {
        do {
            System.out.print("Company id : ");
            String input = scanner.nextLine();

            if (mandatory && StringUtils.isBlank(input)) {
                System.out.println("Company id is empty !");
            } else if (!mandatory && StringUtils.isBlank(input)) {
                // We accept null because it's not mandatory
                return null;
            } else {
                if (StringUtils.isNumeric(input)) {
                    int companyId = Integer.parseInt(input);
                    Optional<Company> company = companyService.get(companyId);
                    if (company.isPresent()) {
                        return company.get();
                    } else {
                        System.out.println("Company does not exist !");
                    }
                } else {
                    System.out.println("Invalid company id !");
                }
            }
        } while (true);
    }

}