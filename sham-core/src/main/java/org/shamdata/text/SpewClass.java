package org.shamdata.text;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
* Created by IntelliJ IDEA.
* User: tom
* Date: 01/02/2012
* Time: 09:21
* To change this template use File | Settings | File Templates.
*/
class SpewClass {
    String name;
    List<Character> variants = Collections.singletonList(null);
    List<SpewInstance> instances;
    SpewGenerator generator;
    int totalWeight = -1;

    String render(Character variant) {
        return render(variant, null);
    }

    String render(Character variant, Map<String,SpewClass> extraClasses) {
        calculateTotalWeight();
        return findRandomInstance().render(variant, extraClasses);
    }

    private SpewInstance findRandomInstance() {
        // probably not the fastest way to calculate this, but other solutions would require
        // more in-memory structure I think
        int targetPlace = generator.random.nextInt(totalWeight) + 1;
        int currentPlace = 0;
        for(SpewInstance instance : instances) {
            currentPlace += instance.weight;
            if(currentPlace >= targetPlace) {
                return instance;
            }
        }
        throw new IllegalStateException("Weights have been adjusted since total weight calculated");
    }

    private void calculateTotalWeight() {
        if(totalWeight == -1) {
            totalWeight = 0;
            for(SpewInstance instance : instances) {
                totalWeight += instance.weight;
            }
        }
    }
}
