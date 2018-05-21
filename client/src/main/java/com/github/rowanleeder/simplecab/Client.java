package com.github.rowanleeder.simplecab;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.helper.HelpScreenException;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class Client {

    public static void main(String[] args) throws IOException, URISyntaxException, ArgumentParserException {
        Client client = new Client();

        try {
            String response = client.parse(client.spec().parseArgs(args));
            System.out.println(response);
        } catch (HelpScreenException ignored) {

        }
    }

    /**
     * Create an cli argument parser.
     *
     * @return The argument parser.
     */
    protected ArgumentParser spec() {
        ArgumentParser parser = ArgumentParsers.newFor("SimpleCab").build()
                .defaultHelp(true)
                .description("Cli for SimpleCab");

        parser.addArgument("--host")
                .nargs("?")
                .type(String.class)
                .setDefault("http://localhost:8080")
                .help("SimpleCab host");

        final Subparsers subparsers = parser.addSubparsers()
                .dest("command")
                .title("Commands");

        Subparser count = subparsers.addParser("counts")
                .help("Fetch cab trip counts");

        count.addArgument("--ignore-cache")
                .action(Arguments.storeConst())
                .setConst(true)
                .type(Boolean.class)
                .setDefault(false)
                .help("Tell the host to ignore the count cache");

        count.addArgument("date")
                .required(true)
                .help("The date")
                .type(String.class);

        count.addArgument("medallions")
                .required(true)
                .help("The cab medallion ids")
                .type(String.class)
                .nargs("+");

        Subparser clear = subparsers.addParser("clear-cache")
                .help("Tell the host to clear its count cache");

        return parser;
    }

    /**
     * @param ns The parsed arguments.
     * @return The response from the SimpleCab host.
     * @throws IOException If there was an error communicating with the host, or there was a request error.
     * @throws URISyntaxException If the host URI is invalid.
     */

    protected String parse(Namespace ns) throws IOException, URISyntaxException {
        SimpleCabService service = new SimpleCabService(ns.get("host"));

        if (ns.getString("command").equals("counts")) {
            List<String> medallions = ns.getList("medallions");

            return service.getMedallionsSummary(
                    medallions.toArray(new String[]{}),
                    LocalDate.parse(ns.getString("date")),
                    ns.getBoolean("ignore_cache")
            );
        }

        if (ns.getString("command").equals("clear-cache")) {
            service.deleteCache();
            return "\"Cache cleared\"";
        }

        throw new IllegalArgumentException("Invalid command");
    }
}
