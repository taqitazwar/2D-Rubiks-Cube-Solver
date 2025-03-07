import java.util.*;

public class BruteForceSolver {
    // Maximum depth for brute force search
    private static final int MAX_DEPTH = 20;
    
    // Performs Iterative Deepening Depth-First Search (IDDFS)
    public String solve(RubiksCube cube) {
        if (cube.isSolved()) {
            return "Cube is already solved!";
        }
        
        for (int depth = 0; depth <= MAX_DEPTH; depth++) {
            System.out.println("Searching at depth: " + depth);
            ArrayList<String> moves = new ArrayList<>();
            if (dfs(cube.copy(), moves, depth, null)) {
                return String.join(" ", moves);
            }
        }
        
        return "No solution found within " + MAX_DEPTH + " moves.";
    }
    
    private boolean dfs(RubiksCube cube, ArrayList<String> moves, int depth, String lastMove) {
        if (depth == 0) {
            return cube.isSolved();
        }
        
        for (String move : RubiksCube.MOVES) {
            // Avoid redundant moves
            if (isRedundant(lastMove, move)) {
                continue;
            }
            
            RubiksCube newCube = cube.copy();
            newCube.applyMove(move);
            moves.add(move);
            
            if (dfs(newCube, moves, depth - 1, move)) {
                return true;
            }
            
            moves.remove(moves.size() - 1); // Backtrack
        }
        
        return false;
    }
    
    // Check if a move is redundant based on the last move
    private boolean isRedundant(String lastMove, String currentMove) {
        if (lastMove == null) {
            return false;
        }
        
        // Same face moves in sequence can be optimized
        char lastFace = lastMove.charAt(0);
        char currentFace = currentMove.charAt(0);
        
        if (lastFace == currentFace) {
            return true;
        }
        
        // Opposite face moves like U followed by D can often be reordered
        if ((lastFace == 'U' && currentFace == 'D') ||
            (lastFace == 'D' && currentFace == 'U') ||
            (lastFace == 'F' && currentFace == 'B') ||
            (lastFace == 'B' && currentFace == 'F') ||
            (lastFace == 'L' && currentFace == 'R') ||
            (lastFace == 'R' && currentFace == 'L')) {
            return false; // Not strictly redundant, but pruning these can help efficiency
        }
        
        return false;
    }
}