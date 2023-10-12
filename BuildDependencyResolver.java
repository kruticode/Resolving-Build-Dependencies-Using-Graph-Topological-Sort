import java.util.*;

public class BuildDependencyResolver {
    private Map<String, List<String>> dependencyGraph;

    public BuildDependencyResolver() {
        dependencyGraph = new HashMap<>();
    }

    public void addDependency(String target, List<String> dependencies) {
        dependencyGraph.put(target, dependencies);
    }

    public List<String> resolveBuildOrder() {
        List<String> buildOrder = new ArrayList<>();
        Map<String, Integer> incomingEdges = new HashMap<>();

        // Initialize incoming edge counts
        for (String target : dependencyGraph.keySet()) {
            incomingEdges.put(target, 0);
        }

        // Count incoming edges for each target
        for (List<String> dependencies : dependencyGraph.values()) {
            for (String dependency : dependencies) {
                incomingEdges.put(dependency, incomingEdges.getOrDefault(dependency, 0) + 1);
            }
        }

        // Find targets with no incoming edges
        Queue<String> readyQueue = new LinkedList<>();
        for (String target : dependencyGraph.keySet()) {
            if (incomingEdges.get(target) == 0) {
                readyQueue.add(target);
            }
        }

        // Process targets in the topological order
        while (!readyQueue.isEmpty()) {
            String target = readyQueue.poll();
            buildOrder.add(target);

            List<String> dependencies = dependencyGraph.get(target);
            if (dependencies != null) {
                for (String dependency : dependencies) {
                    incomingEdges.put(dependency, incomingEdges.get(dependency) - 1);
                    if (incomingEdges.get(dependency) == 0) {
                        readyQueue.add(dependency);
                    }
                }
            }
        }

        // Check for cyclic dependencies
        if (buildOrder.size() != dependencyGraph.size()) {
            throw new IllegalStateException("Cyclic dependency detected. Build cannot be resolved.");
        }

        return buildOrder;
    }

    public static void main(String[] args) {
        BuildDependencyResolver resolver = new BuildDependencyResolver();

        // Add build targets and their dependencies
        resolver.addDependency("A", Arrays.asList("B", "C"));
        resolver.addDependency("B", Arrays.asList("D"));
        resolver.addDependency("C", Arrays.asList("D", "E"));
        resolver.addDependency("D", Arrays.asList("E"));
        resolver.addDependency("E", Arrays.asList());

        // Resolve build order
        List<String> buildOrder = resolver.resolveBuildOrder();

        // Print the build order
        System.out.println("Build Order:");
        for (String target : buildOrder) {
            System.out.println(target);
        }
    }
}