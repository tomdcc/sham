package org.shamdata.text;

import org.shamdata.ShamGenerator;
import org.shamdata.util.ResourceUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpewGenerator implements ShamGenerator {

    private final static String MAIN_CLASS_NAME = "MAIN";

    Random random;
    String bundleName = "headline";
    Map<String,SpewClass> spewClasses;
    SpewClass mainClass;

    public String nextLine() {
        return nextLine(null);
    }

    public String nextLine(Map<String,Object> extraClasses) {
        return mainClass.render(null, preprocessExtraClasses(extraClasses));
    }

    private Map<String,SpewClass> preprocessExtraClasses(Map<String,Object> extraClasses) {
        Map<String,SpewClass> extra = null;
        if(extraClasses != null) {
            extra = new HashMap<String, SpewClass>();
            for(Map.Entry<String,Object> entry : extraClasses.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if(value instanceof SpewClass) {
                    SpewClass cls = (SpewClass) value;
                    cls.generator = this;
                    extra.put(key, cls);
                } else if(value instanceof String) {
                    SpewClass cls = new SpewClass();
                    cls.generator = this;
                    SpewInstance instance = new SpewInstance();
                    instance.spewClass = cls;
                    instance.text = (String) value;
                    cls.instances = Collections.singletonList(instance);
                    extra.put(key, cls);
                } else {
                    throw new IllegalArgumentException("Unknown extra class type of " + value.getClass());
                }
            }
        }
        return extra;
    }

    public void init() {
        if(random == null) {
            random = new Random();
        }
        parse();
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public void setBundleName(String name) {
        bundleName = name;
    }

    void parse() {
        parse(ResourceUtil.readResource(this.getClass(), bundleName, "spew"));
    }

    void parse(InputStream stream) {
        // remove weights, counts
        Pattern weightPattern = Pattern.compile("^\\((\\d+)\\)(.*)$");
        Pattern parameterPattern = Pattern.compile("^(.*)\\{([a-zA-Z]+)\\}$");
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        try {
            SpewClass spewClass = null;
            spewClasses = new HashMap<String, SpewClass>();
            for(String line = reader.readLine(); line != null; line = reader.readLine()) {
                // strip comments
                line = stripComments(line);
                line = line.trim();
                if(line.isEmpty()) {
                    continue;
                }

                // stop if we hit %%
                if(line.equals("%%")) {
                    break;
                }

                // is this a class?
                if(line.startsWith("%")) {
                    spewClass = parseClass(parameterPattern, line.substring(1));
                } else {
                    parseInstance(weightPattern, spewClass, line);

                }
            }
        } catch(IOException e) {
            throw new IllegalArgumentException(e);
        } finally {
            try { reader.close(); } catch(IOException e) { /* swallow */ }
        }

        // sanity checks
        if(mainClass == null) {
            throw new IllegalArgumentException("No main spew class with name " + MAIN_CLASS_NAME + " found");
        }
        for(SpewClass spewClass : spewClasses.values()) {
            if(spewClass.instances.isEmpty()) {
                throw new IllegalArgumentException("Spew class " + spewClass.name + " is empty");
            }
        }
    }

    private void parseInstance(Pattern weightPattern, SpewClass currentClass, String line) {
        // must be an instance
        if(currentClass == null) {
            // instance before any class declarations in file, barf
            throw new IllegalArgumentException("Spew file must start with class declaration");
        }

        SpewInstance instance = new SpewInstance();
        instance.spewClass = currentClass;
        currentClass.instances.add(instance);

        // does it have a weight?
        Matcher weightMatcher = weightPattern.matcher(line);
        if(weightMatcher.matches()) {
            instance.text = weightMatcher.group(2);
            instance.weight = Integer.parseInt(weightMatcher.group(1));
        } else {
            // no explicit weight
            instance.text = line;
        }
    }

    private SpewClass parseClass(Pattern parameterPattern, String line) {
        SpewClass spewClass = new SpewClass();
        spewClass.generator = this;
        spewClass.instances = new ArrayList<SpewInstance>();

        // parameterized?
        Matcher paramMatcher = parameterPattern.matcher(line);
        if(paramMatcher.matches()) {
            spewClass.name = paramMatcher.group(1);
            spewClass.variants = new ArrayList<Character>();
            spewClass.variants.add(null);
            for(Character c : paramMatcher.group(2).toCharArray()) {
                spewClass.variants.add(c);
            }
        } else {
            // no explicit weight
            spewClass.name = line;
        }

        spewClasses.put(spewClass.name, spewClass);
        if(spewClass.name.equals(MAIN_CLASS_NAME)) {
            mainClass = spewClass;
        }
        return spewClass;
    }

    private String stripComments(String line) {
        return line.replaceFirst("\\\\\\*.*", "");
    }

}
