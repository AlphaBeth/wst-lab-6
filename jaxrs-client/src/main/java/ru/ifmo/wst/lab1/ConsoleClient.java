package ru.ifmo.wst.lab1;

import lombok.SneakyThrows;
import ru.ifmo.wst.lab.Box;
import ru.ifmo.wst.lab.ExterminatusInfo;
import ru.ifmo.wst.lab.Pair;
import ru.ifmo.wst.lab1.command.Command;
import ru.ifmo.wst.lab1.command.CommandArg;
import ru.ifmo.wst.lab1.command.CommandInterpreter;
import ru.ifmo.wst.lab1.command.NoLineFoundException;
import ru.ifmo.wst.lab1.command.args.DateArg;
import ru.ifmo.wst.lab1.command.args.EmptyStringToNull;
import ru.ifmo.wst.lab1.command.args.LongArg;
import ru.ifmo.wst.lab1.command.args.StringArg;
import ru.ifmo.wst.lab1.model.ExterminatusEntity;
import ru.ifmo.wst.lab1.model.Filter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import static java.util.Arrays.asList;

public class ConsoleClient {
    @SneakyThrows
    public static void main(String[] args) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String endpointUrl = "http://localhost:8080";
        System.out.print("Enter base exterminatus url (or empty string for default " + endpointUrl + ")\n> ");
        String line = bufferedReader.readLine();
        if (line == null) {
            return;
        }
        if (!line.trim().isEmpty()) {
            endpointUrl = line.trim();
        }

        ExterminatusResourceClient service = new ExterminatusResourceClient(endpointUrl);

        Command<Void> infoCommand = new Command<>("info", "Print help for commands");
        Command<Box<String>> changeEndpointAddressCommand = new Command<>("endpoint", "Changes endpoint address",
                asList(
                        new StringArg<>("Not a string", "url", "New exterminatus endpoint url", Box::setValue)
                ), Box::new
        );
        Command<Void> findAllCommand = new Command<>("findAll", "Return list of all exterminatus entities");
        Command<Filter> filterCommand = new Command<>("filter",
                "Filter exterminatus entities by column values (ignore case contains for strings), empty values are ignored",
                asList(
                        toNull(new LongArg<>("id", "Exterminatus id", Filter::setId)),
                        toNull(new StringArg<>("initiator", "Initiator name", Filter::setInitiator)),
                        toNull(new StringArg<>("reason", "Reason of exterminatus", Filter::setReason)),
                        toNull(new StringArg<>("method", "Method of exterminatus", Filter::setMethod)),
                        toNull(new StringArg<>("planet", "Exterminated planet", Filter::setPlanet)),
                        toNull(new DateArg<>("date", "Date of exterminatus", Filter::setDate))
                ),
                Filter::new);
        Command<ExterminatusInfo> createCommand = new Command<>("create",
                "Create new exterminatus entity",
                asList(
                        toNull(new StringArg<>("initiator", "Initiator name", ExterminatusInfo::setInitiator)),
                        toNull(new StringArg<>("reason", "Reason of exterminatus", ExterminatusInfo::setReason)),
                        toNull(new StringArg<>("method", "Method of exterminatus", ExterminatusInfo::setMethod)),
                        toNull(new StringArg<>("planet", "Exterminated planet", ExterminatusInfo::setPlanet)),
                        toNull(new DateArg<>("date", "Date of exterminatus", ExterminatusInfo::setDate))
                ), ExterminatusInfo::new);
        Command<ExterminatusEntity> updateCommand = new Command<>("update",
                "Update exterminatus by id",
                asList(
                        new LongArg<>("id", "Exterminatus id", ExterminatusEntity::setId),
                        toNull(new StringArg<>("initiator", "Initiator name", ExterminatusEntity::setInitiator)),
                        toNull(new StringArg<>("reason", "Reason of exterminatus", ExterminatusEntity::setReason)),
                        toNull(new StringArg<>("method", "Method of exterminatus", ExterminatusEntity::setMethod)),
                        toNull(new StringArg<>("planet", "Exterminated planet", ExterminatusEntity::setPlanet)),
                        toNull(new DateArg<>("date", "Date of exterminatus", ExterminatusEntity::setDate))
                ), ExterminatusEntity::new
        );
        Command<Box<Long>> deleteCommand = new Command<>("delete", "Delete exterminatus by id",
                asList(
                        new LongArg<>("id", "Exterminatus id", Box::setValue)
                ), Box::new);
        Command<Void> exitCommand = new Command<>("exit", "Exit application");


        CommandInterpreter commandInterpreter = new CommandInterpreter(() -> readLine(bufferedReader),
                System.out::print,
                asList(
                        infoCommand, changeEndpointAddressCommand, findAllCommand, filterCommand,
                        createCommand, updateCommand, deleteCommand, exitCommand
                ),
                "No command found",
                "Enter command", "> ");

        commandInterpreter.info();

        while (true) {
            Pair<Command, Object> withArg;
            try {
                withArg = commandInterpreter.readCommand();
            } catch (NoLineFoundException exc) {
                return;
            }
            if (withArg == null) {
                continue;
            }
            Command command = withArg.getLeft();
            try {
                if (command.equals(findAllCommand)) {
                    List<ExterminatusEntity> all = service.findAll();
                    System.out.println("Result of operation:");
                    all.forEach(System.out::println);
                } else if (command.equals(filterCommand)) {
                    Filter filterArg = (Filter) withArg.getRight();
                    List<ExterminatusEntity> filterRes = service.filter(filterArg);
                    System.out.println("Result of operation:");
                    filterRes.forEach(System.out::println);
                } else if (command.equals(infoCommand)) {
                    commandInterpreter.info();
                } else if (command.equals(exitCommand)) {
                    break;
                } else if (command.equals(changeEndpointAddressCommand)) {
                    @SuppressWarnings("unchecked")
                    Box<String> arg = (Box<String>) withArg.getRight();
                    String newUrl = arg.getValue();
                    service = new ExterminatusResourceClient(newUrl);
                } else if (command.equals(createCommand)) {
                    ExterminatusInfo arg = (ExterminatusInfo) withArg.getRight();
                    long id = service.create(arg);
                    System.out.printf("Exterminatus with id %d was created\n", id);
                } else if (command.equals(deleteCommand)) {
                    @SuppressWarnings("unchecked")
                    Box<Long> arg = (Box<Long>) withArg.getRight();
                    Long deleteId = arg.getValue();
                    int deletedCount = service.delete(deleteId);
                    System.out.printf("%d rows were deleted by id %d\n", deletedCount, deleteId);
                } else if (command.equals(updateCommand)) {
                    ExterminatusEntity arg = (ExterminatusEntity) withArg.getRight();
                    int updatedCount = service.update(arg);
                    System.out.printf("%d rows updated by id %d\n", updatedCount, arg.getId());
                }
            } catch (Exception exc) {
                System.out.println("Unknown error");
                exc.printStackTrace();
            }

        }
    }

    private static <T, C> CommandArg<T, C> toNull(CommandArg<T, C> commandArg) {
        return new EmptyStringToNull<>(commandArg);
    }

    @SneakyThrows
    private static String readLine(BufferedReader reader) {
        return reader.readLine();
    }

}
