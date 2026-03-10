package AI;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Unit {
    private final String tier;
    private final String name;
    private final List<String> synergies;

    public Unit(String tier, String name, String... synergies) {
        this.tier = tier;
        this.name = name;
        this.synergies = Arrays.asList(synergies);
    }

    public String getTier() {
        return tier;
    }

    public String getName() {
        return name;
    }

    public List<String> getSynergies() {
        return synergies;
    }

    public String getSynergyText() {
        return synergies.stream().collect(Collectors.joining(", "));
    }

    @Override
    public String toString() {
        return "[" + tier + "] " + name + " - " + getSynergyText();
    }
}