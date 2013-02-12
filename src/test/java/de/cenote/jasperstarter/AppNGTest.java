/*
 * Copyright 2013 Cenote GmbH.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cenote.jasperstarter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author Volker Voßkämper <vvo at cenote.de>
 */
public class AppNGTest {

    public AppNGTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }

    /**
     * Test of private createArgumentParser method, of class App
     */
    @Test
    public void testCreateArgumentParser() throws NoSuchMethodException,
            IllegalAccessException, IllegalArgumentException {
        System.out.println("createArgumentParser");
        App app = new App();
        Config config = null;
        try {
            config = new Config();
        } catch (IOException ex) {
            fail(ex.getMessage(), ex);
        }
        Method method = app.getClass().getDeclaredMethod(
                "createArgumentParser", Config.class);
        method.setAccessible(true);
        ArgumentParser parser = null;
        try {
            parser = (ArgumentParser) method.invoke(app, config);
        } catch (InvocationTargetException ex) {
            fail(ex.getCause().getMessage(), ex.getCause());
        }
        assertNotNull(parser);
    }

    /**
     * Test of private parseArgumentParser method, of class App
     */
    @Test(dependsOnMethods = {"testCreateArgumentParser"})
    public void testParseArgumentParser() throws NoSuchMethodException, IllegalAccessException {
        System.out.println("parseArgumentParser");
        App app = new App();
        String[] args = {};
        Config config = null;
        try {
            config = new Config();
        } catch (IOException ex) {
            fail(ex.getMessage(), ex);
        }
        Method methodCreateArgumentParser = app.getClass().getDeclaredMethod(
                "createArgumentParser", Config.class);
        methodCreateArgumentParser.setAccessible(true);
        ArgumentParser parser = null;
        try {
            parser = (ArgumentParser) methodCreateArgumentParser.invoke(app, config);
        } catch (InvocationTargetException ex) {
            fail(ex.getCause().getMessage(), ex.getCause());
        }
        Method method = app.getClass().getDeclaredMethod(
                "parseArgumentParser", String[].class, ArgumentParser.class,
                Config.class);
        method.setAccessible(true);
        // empty args
        try {
            method.invoke(app, args, parser, config);
        } catch (InvocationTargetException ex) {
            assertEquals("too few arguments", ex.getCause().getMessage());
        }
        // one wrong arg (space)
        args = "".split(" ");
        try {
            method.invoke(app, args, parser, config);
        } catch (InvocationTargetException ex) {
            assertEquals("invalid choice: '' (choose from 'compile', 'cp',"
                    + " 'process', 'pr', 'list_printers', 'printers', 'lpr',"
                    + " 'list_parameters', 'params', 'lpa')",
                    ex.getCause().getMessage());
        }
    }

    /**
     * Test of private parseArgumentParser method, of class App
     *
     * detailed tests for command process
     */
    @Test(dependsOnMethods = {"testCreateArgumentParser"})
    public void testParseArgumentParserCommandProcess() throws NoSuchMethodException, IllegalAccessException {
        System.out.println("parseArgumentParser");
        App app = new App();
        String[] args = {};
        Config config = null;
        try {
            config = new Config();
        } catch (IOException ex) {
            fail(ex.getMessage(), ex);
        }
        Method methodCreateArgumentParser = app.getClass().getDeclaredMethod(
                "createArgumentParser", Config.class);
        methodCreateArgumentParser.setAccessible(true);
        ArgumentParser parser = null;
        try {
            parser = (ArgumentParser) methodCreateArgumentParser.invoke(app, config);
        } catch (InvocationTargetException ex) {
            fail(ex.getCause().getMessage(), ex.getCause());
        }
        Method method = app.getClass().getDeclaredMethod(
                "parseArgumentParser", String[].class, ArgumentParser.class,
                Config.class);
        method.setAccessible(true);
        // just the process command
        args = "process".split(" ");
        try {
            method.invoke(app, args, parser, config);
        } catch (InvocationTargetException ex) {
            assertEquals("argument -f is required", ex.getCause().getMessage());
        }
        // now the alias pr
        args = "pr".split(" ");
        try {
            method.invoke(app, args, parser, config);
        } catch (InvocationTargetException ex) {
            assertEquals("argument -f is required", ex.getCause().getMessage());
        }
        // try and error - follow the help message for next input
        args = "pr -f".split(" ");
        try {
            method.invoke(app, args, parser, config);
        } catch (InvocationTargetException ex) {
            assertEquals("argument -f: expected 1 argument(s)", ex.getCause().getMessage());
        }
        // try and error - follow the help message for next input
        args = "pr -f ''".split(" ");
        try {
            method.invoke(app, args, parser, config);
        } catch (InvocationTargetException ex) {
            StringBuilder sb = new StringBuilder();
            sb.append("argument -f: could not convert '''' (choose from ");
            sb.append("{view,print,jrprint,pdf,rtf,docx,odt,html,xml,xls,");
            sb.append("xlsx,csv,ods,pptx,xhtml})");
            assertEquals(sb.toString(), ex.getCause().getMessage());
        }
        // try and error - follow the help message for next input
        args = "pr -f pdf".split(" ");
        try {
            method.invoke(app, args, parser, config);
        } catch (InvocationTargetException ex) {
            assertEquals("argument -i is required", ex.getCause().getMessage());
        }
        // try and error - follow the help message for next input
        args = "pr -f pdf -i".split(" ");
        try {
            method.invoke(app, args, parser, config);
        } catch (InvocationTargetException ex) {
            assertEquals("argument -i: expected one argument", ex.getCause().getMessage());
        }
        // try and error - follow the help message for next input
        // this one shoud be complete (no db report):
        args = "pr -f pdf -i fakefile".split(" ");
        try {
            method.invoke(app, args, parser, config);
        } catch (InvocationTargetException ex) {
            fail(ex.getCause().getMessage(), ex.getCause());
        }
        // starting with db reports
        // try and error - follow the help message for next input
        args = "pr -f pdf -i fakefile -t".split(" ");
        try {
            method.invoke(app, args, parser, config);
        } catch (InvocationTargetException ex) {
            assertEquals("argument -t: expected one argument", ex.getCause().getMessage());
        }
        // try and error - follow the help message for next input
        args = "pr -f pdf -i fakefile -t ''".split(" ");
        try {
            method.invoke(app, args, parser, config);
        } catch (InvocationTargetException ex) {
            StringBuilder sb = new StringBuilder();
            sb.append("argument -t: could not convert '''' (choose from ");
            sb.append("{none,mysql,postgres,oracle,generic})");
            assertEquals(sb.toString(), ex.getCause().getMessage());
        }
        // this one shoud be complete (no db report):
        args = "pr -f pdf -i fakefile -t none".split(" ");
        try {
            method.invoke(app, args, parser, config);
        } catch (InvocationTargetException ex) {
            fail(ex.getCause().getMessage(), ex.getCause());
        }
        // starting with db mysql ( first modification of parser)
        // try and error - follow the help message for next input
        args = "pr -f pdf -i fakefile -t mysql".split(" ");
        try {
            method.invoke(app, args, parser, config);
        } catch (InvocationTargetException ex) {
            assertEquals("argument -H is required", ex.getCause().getMessage());
        }
        // try and error - follow the help message for next input
        args = "pr -f pdf -i fakefile -t mysql -H".split(" ");
        try {
            method.invoke(app, args, parser, config);
        } catch (InvocationTargetException ex) {
            assertEquals("argument -H: expected one argument", ex.getCause().getMessage());
        }
        // try and error - follow the help message for next input
        args = "pr -f pdf -i fakefile -t mysql -H myhost".split(" ");
        try {
            method.invoke(app, args, parser, config);
        } catch (InvocationTargetException ex) {
            assertEquals("argument -u is required", ex.getCause().getMessage());
        }
        // try and error - follow the help message for next input
        args = "pr -f pdf -i fakefile -t mysql -H myhost -u".split(" ");
        try {
            method.invoke(app, args, parser, config);
        } catch (InvocationTargetException ex) {
            assertEquals("argument -u: expected one argument", ex.getCause().getMessage());
        }
        // try and error - follow the help message for next input
        args = "pr -f pdf -i fakefile -t mysql -H myhost -u myuser".split(" ");
        try {
            method.invoke(app, args, parser, config);
        } catch (InvocationTargetException ex) {
            assertEquals("argument -n is required", ex.getCause().getMessage());
        }
        // try and error - follow the help message for next input
        args = "pr -f pdf -i fakefile -t mysql -H myhost -u myuser -n".split(" ");
        try {
            method.invoke(app, args, parser, config);
        } catch (InvocationTargetException ex) {
            assertEquals("argument -n: expected one argument", ex.getCause().getMessage());
        }
        // try and error - follow the help message for next input
        args = "pr -f pdf -i fakefile -t mysql -H myhost -u myuser -n dbname".split(" ");
        try {
            method.invoke(app, args, parser, config);
        } catch (InvocationTargetException ex) {
            // this is the minimal mysql argument set
            fail(ex.getCause().getMessage(), ex.getCause());
        }
        // checking the mysql default port
        assertEquals(config.getDbPort(), new Integer("3306"));

        // starting with db postgres (next modification of parser)
        // create a fresh parser
        try {
            parser = (ArgumentParser) methodCreateArgumentParser.invoke(app, config);
        } catch (InvocationTargetException ex) {
            fail(ex.getCause().getMessage(), ex.getCause());
        }
        // try and error - follow the help message for next input
        args = "pr -f pdf -i fakefile -t postgres".split(" ");
        try {
            method.invoke(app, args, parser, config);
        } catch (InvocationTargetException ex) {
            assertEquals("argument -H is required", ex.getCause().getMessage());
        }
        // try and error - follow the help message for next input
        // argument -H is tested before, so fulfill immediate with argument
        args = "pr -f pdf -i fakefile -t postgres -H myhost".split(" ");
        try {
            method.invoke(app, args, parser, config);
        } catch (InvocationTargetException ex) {
            assertEquals("argument -u is required", ex.getCause().getMessage());
        }
        // try and error - follow the help message for next input
        // argument -u is tested before, so fulfill immediate with argument
        args = "pr -f pdf -i fakefile -t postgres -H myhost -u myuser".split(" ");
        try {
            method.invoke(app, args, parser, config);
        } catch (InvocationTargetException ex) {
            assertEquals("argument -n is required", ex.getCause().getMessage());
        }
        // try and error - follow the help message for next input
        // argument -u is tested before, so fulfill immediate with argument
        args = "pr -f pdf -i fakefile -t postgres -H myhost -u myuser -n dbname".split(" ");
        try {
            method.invoke(app, args, parser, config);
        } catch (InvocationTargetException ex) {
            // this is the minimal postgres argument set
            fail(ex.getCause().getMessage(), ex.getCause());
        }
        // checking the postgres default port
        assertEquals(config.getDbPort(), new Integer("5432"));

        // starting with db oracle (next modification of parser)
        // create a fresh parser
        try {
            parser = (ArgumentParser) methodCreateArgumentParser.invoke(app, config);
        } catch (InvocationTargetException ex) {
            fail(ex.getCause().getMessage(), ex.getCause());
        }
        // try and error - follow the help message for next input
        args = "pr -f pdf -i fakefile -t oracle".split(" ");
        try {
            method.invoke(app, args, parser, config);
        } catch (InvocationTargetException ex) {
            assertEquals("argument -H is required", ex.getCause().getMessage());
        }
        // try and error - follow the help message for next input
        // argument -H is tested before, so fulfill immediate with argument
        args = "pr -f pdf -i fakefile -t oracle -H myhost".split(" ");
        try {
            method.invoke(app, args, parser, config);
        } catch (InvocationTargetException ex) {
            assertEquals("argument -u is required", ex.getCause().getMessage());
        }
        // try and error - follow the help message for next input
        // argument -u is tested before, so fulfill immediate with argument
        args = "pr -f pdf -i fakefile -t oracle -H myhost -u myuser".split(" ");
        try {
            method.invoke(app, args, parser, config);
        } catch (InvocationTargetException ex) {
            assertEquals("argument -p is required", ex.getCause().getMessage());
        }
        // try and error - follow the help message for next input
        args = "pr -f pdf -i fakefile -t oracle -H myhost -u myuser -p".split(" ");
        try {
            method.invoke(app, args, parser, config);
        } catch (InvocationTargetException ex) {
            assertEquals("argument -p: expected one argument", ex.getCause().getMessage());
        }
        // try and error - follow the help message for next input
        args = "pr -f pdf -i fakefile -t oracle -H myhost -u myuser -p passwd".split(" ");
        try {
            method.invoke(app, args, parser, config);
        } catch (InvocationTargetException ex) {
            assertEquals("argument --db-sid is required", ex.getCause().getMessage());
        }
        // try and error - follow the help message for next input
        args = "pr -f pdf -i fakefile -t oracle -H myhost -u myuser -p passwd --db-sid".split(" ");
        try {
            method.invoke(app, args, parser, config);
        } catch (InvocationTargetException ex) {
            assertEquals("argument --db-sid: expected one argument", ex.getCause().getMessage());
        }
        // try and error - follow the help message for next input
        args = "pr -f pdf -i fakefile -t oracle -H myhost -u myuser -p passwd --db-sid orcl".split(" ");
        try {
            method.invoke(app, args, parser, config);
        } catch (InvocationTargetException ex) {
            // this is the minimal oracle argument set
            fail(ex.getCause().getMessage(), ex.getCause());
        }
        // checking the oracle default port
        assertEquals(config.getDbPort(), new Integer("1521"));
        // create a fresh parser
//        try {
//            parser = (ArgumentParser) methodCreateArgumentParser.invoke(app, config);
//        } catch (InvocationTargetException ex) {
//            fail(ex.getCause().getMessage(), ex.getCause());
//        }

    }
}