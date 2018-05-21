package com.github.rowanleeder.simplecab;

import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ClientTest {

    /**
     * Test count collection with explicit optionals parses.
     *
     * @throws ArgumentParserException
     */
    @Test
    public void testSpecCountsFull() throws ArgumentParserException {
        ArgumentParser spec = (new Client()).spec();

        String[] args = {
                "--host=123.123.123.123:8081",
                "counts",
                "--ignore-cache",
                "2013-12-30",
                "00FD1D146C1899CEDB738490659CAD30",
                "84F1B1B17DA76D79A1C908AD330D97B8"
        };

        Namespace ns = spec.parseArgs(args);

        assertEquals("123.123.123.123:8081", ns.getString("host"));
        assertEquals("counts", ns.getString("command"));
        assertEquals(true, ns.getBoolean("ignore_cache"));
        assertEquals(
                Arrays.asList("00FD1D146C1899CEDB738490659CAD30", "84F1B1B17DA76D79A1C908AD330D97B8"),
                ns.getList("medallions")
        );
    }

    /**
     * Test count collection parses.
     *
     * @throws ArgumentParserException
     */
    @Test
    public void testSpecCountsMinimal() throws ArgumentParserException {
        ArgumentParser spec = (new Client()).spec();

        String[] args = {
                "counts",
                "2013-12-31",
                "00FD1D146C1899CEDB738490659CAD30"
        };

        Namespace ns = spec.parseArgs(args);

        assertEquals("http://localhost:8080", ns.getString("host"));
        assertEquals("counts", ns.getString("command"));
        assertEquals(false, ns.getBoolean("ignore_cache"));
        assertEquals(
                Arrays.asList("00FD1D146C1899CEDB738490659CAD30"),
                ns.getList("medallions")
        );
    }

    /**
     * Test failure case. Syntactic not semantic as relying on server to generate input validation messages.
     *
     * @throws ArgumentParserException
     */
    @Test(expected = ArgumentParserException.class)
    public void testSpecCountsIncomplete() throws ArgumentParserException {
        (new Client()).spec().parseArgs(new String[]{"counts", "2013-12-31"});
    }

    /**
     * Test cache clearing parses.
     *
     * @throws ArgumentParserException
     */
    @Test
    public void testSpecCacheClear() throws ArgumentParserException {
        ArgumentParser spec = (new Client()).spec();

        String[] args = {"clear-cache"};

        Namespace ns = spec.parseArgs(args);

        assertEquals("http://localhost:8080", ns.getString("host"));
        assertEquals("clear-cache", ns.getString("command"));
    }
}
