package de.tum.bgu.msm.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.apache.log4j.Logger;


public abstract class JavaScriptCalculator<T> {
    protected static final Logger logger = Logger.getLogger(JavaScriptCalculator.class);
    protected CompiledScript compiledScript;
    protected Invocable invocable;

    protected JavaScriptCalculator(Reader reader) {
        logger.debug("Reading script...");
        String script = this.readScript(reader);
        logger.debug("Compiling script: " + script);
        this.compileScript(script);
    }

    private String readScript(Reader reader) {
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuilder scriptBuilder = new StringBuilder();

        try {
            for(String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                scriptBuilder.append(line).append("\n");
            }

            bufferedReader.close();
        } catch (IOException e) {
            logger.fatal("Error in reading script!", e);
        }

        return scriptBuilder.toString();
    }

    private void compileScript(String script) {
        // Set system properties to configure GraalVM behavior
        System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");
        System.setProperty("graal.CompilationFailureAction", "Silent");

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = null;

        // Try GraalVM JavaScript first with options to suppress warnings
        engine = manager.getEngineByName("graal.js");
        if (engine != null) {
            // Configure GraalVM options to suppress compilation warnings
            try {
                engine.eval("Polyglot.evalFile = undefined;"); // Disable some features that trigger warnings
                // Set engine options to suppress warnings
                engine.getContext().setAttribute("polyglot.engine.WarnInterpreterOnly", false, 100);
            } catch (Exception e) {
                // Ignore if options are not available
                logger.debug("Could not set GraalVM options: " + e.getMessage());
            }
        }

        if (engine == null) {
            logger.warn("GraalVM JavaScript engine not available, trying alternative engines...");
            // Fallback to other JavaScript engines
            String[] fallbackEngines = {"js", "javascript", "nashorn", "rhino"};
            for (String engineName : fallbackEngines) {
                engine = manager.getEngineByName(engineName);
                if (engine != null) {
                    logger.info("Using fallback engine: " + engine.getFactory().getEngineName());
                    break;
                }
            }
        } else {
            logger.info("Using GraalVM JavaScript engine: " + engine.getFactory().getEngineName());
        }

        if (engine == null) {
            logger.fatal("No JavaScript engine available!");
            throw new RuntimeException("No JavaScript engine available!");
        }

        // Check if engine supports compilation
        if (!(engine instanceof Compilable)) {
            logger.fatal("Script engine does not support compilation!");
            throw new RuntimeException("Script engine does not support compilation!");
        }

        Compilable compileEngine = (Compilable)engine;

        try {
            this.compiledScript = compileEngine.compile(script);
            this.compiledScript.eval();
            this.invocable = (Invocable)this.compiledScript.getEngine();
        } catch (ScriptException e) {
            logger.fatal("Error in input script!", e);
            e.printStackTrace();
            throw new RuntimeException("Failed to compile script", e);
        }
    }

    protected final T calculate(String function, Object... args) {
        try {
            return (T)this.invocable.invokeFunction(function, args);
        } catch (NoSuchMethodException | ScriptException e) {
            throw new RuntimeException(e);
        }
    }
}