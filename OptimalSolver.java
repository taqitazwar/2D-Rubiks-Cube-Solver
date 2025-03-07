import java.util.*;

public class OptimalSolver {
    // Maximum depth for IDA* search
    private static final int MAX_DEPTH = 20;
    
    // Perform IDA* search
    public String solve(RubiksCube cube) {
        if (cube.isSolved()) {
            return "Cube is already solved!";
        }
        
        HashSet<String> visited = new HashSet<>();
        Stack<String> moveSequence = new Stack<>();
        
        // Initial threshold is the heuristic value of the initial state
        int threshold = cube.getHeuristic();
        
        while (threshold <= MAX_DEPTH) {
            System.out.println("Searching with threshold: " + threshold);
            visited.clear();
            
            int result = idaStar(cube.copy(), moveSequence, 0, threshold, "", visited);
            
            if (result == -1) {
                // Solution found
                return String.join(" ", moveSequence);
            } else if (result == Integer.MAX_VALUE) {
                // No solution exists within the threshold
                return "No solution found within " + MAX_DEPTH + " moves.";
            }
            
            // Update threshold for next iteration
            threshold = result;
        }
        
        return "No solution found within " + MAX_DEPTH + " moves.";
    }
    
    private int idaStar(RubiksCube cube, Stack<String> moveSequence, int g, int threshold, String lastMove, HashSet<String> visited) {
        int f = g + cube.getHeuristic();
        
        if (f > threshold) {
            return f;
        }
        
        if (cube.isSolved()) {
            return -1; // Solution found
        }
        
        // Add current state to visited set to avoid cycles
        String cubeState = Arrays.deepToString(cube.getState());
        if (visited.contains(cubeState)) {
            return Integer.MAX_VALUE;
        }
        visited.add(cubeState);
        
        int minCost = Integer.MAX_VALUE;
        
        for (String move : getPruningMoves(lastMove)) {
            RubiksCube newCube = cube.copy();
            newCube.applyMove(move);
            
            moveSequence.push(move);
            int result = idaStar(newCube, moveSequence, g + 1, threshold, move, visited);
            
            if (result == -1) {
                return -1; // Solution found
            }
            
            if (result < minCost) {
                minCost = result;
            }
            
            moveSequence.pop(); // Backtrack
        }
        
        // Remove from visited set when backtracking
        visited.remove(cubeState);
        
        return minCost;
    }
    
    // Get moves for pruning search space
    private List<String> getPruningMoves(String lastMove) {
        List<String> moves = new ArrayList<>(Arrays.asList(RubiksCube.MOVES));
        
        if (lastMove.isEmpty()) {
            return moves;
        }
        
        // Prune redundant moves
        List<String> prunedMoves = new ArrayList<>();
        char lastFace = lastMove.charAt(0);
        
        for (String move : moves) {
            char face = move.charAt(0);
            
            // Skip moves on the same face
            if (face == lastFace) {
                continue;
            }
            
            // Skip moves on opposite faces if the last move was on one of these faces
            if ((lastFace == 'U' && face == 'D') ||
                (lastFace == 'D' && face == 'U') ||
                (lastFace == 'F' && face == 'B') ||
                (lastFace == 'B' && face == 'F') ||
                (lastFace == 'L' && face == 'R') ||
                (lastFace == 'R' && face == 'L')) {
                continue;
            }
            
            prunedMoves.add(move);
        }
        
        return prunedMoves;
    }
}