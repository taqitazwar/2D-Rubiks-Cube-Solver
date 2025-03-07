import java.util.Arrays;

public class RubiksCube {
    // Faces: 0=Up, 1=Down, 2=Front, 3=Back, 4=Left, 5=Right
    private char[][][] state;
    
    // Move representations
    public static final String[] MOVES = {"U", "U'", "U2", "D", "D'", "D2", 
                                         "F", "F'", "F2", "B", "B'", "B2", 
                                         "L", "L'", "L2", "R", "R'", "R2"};
    
    public RubiksCube() {
        // Initialize a solved cube
        state = new char[6][3][3];
        char[] colors = {'W', 'Y', 'G', 'B', 'O', 'R'};
        
        for (int face = 0; face < 6; face++) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    state[face][i][j] = colors[face];
                }
            }
        }
    }
    
    public RubiksCube(char[][][] state) {
        this.state = new char[6][3][3];
        for (int face = 0; face < 6; face++) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    this.state[face][i][j] = state[face][i][j];
                }
            }
        }
    }
    
    public char[][][] getState() {
        char[][][] copy = new char[6][3][3];
        for (int face = 0; face < 6; face++) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    copy[face][i][j] = state[face][i][j];
                }
            }
        }
        return copy;
    }
    
    public RubiksCube copy() {
        return new RubiksCube(this.state);
    }
    
    public boolean isSolved() {
        for (int face = 0; face < 6; face++) {
            char centerColor = state[face][1][1];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (state[face][i][j] != centerColor) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public void applyMove(String move) {
        switch (move) {
            case "U":  rotateU(1); break;
            case "U'": rotateU(3); break;
            case "U2": rotateU(2); break;
            case "D":  rotateD(1); break;
            case "D'": rotateD(3); break;
            case "D2": rotateD(2); break;
            case "F":  rotateF(1); break;
            case "F'": rotateF(3); break;
            case "F2": rotateF(2); break;
            case "B":  rotateB(1); break;
            case "B'": rotateB(3); break;
            case "B2": rotateB(2); break;
            case "L":  rotateL(1); break;
            case "L'": rotateL(3); break;
            case "L2": rotateL(2); break;
            case "R":  rotateR(1); break;
            case "R'": rotateR(3); break;
            case "R2": rotateR(2); break;
        }
    }
    
    public void applyMoves(String[] moves) {
        for (String move : moves) {
            applyMove(move);
        }
    }
    
    private void rotateFace(int face, int times) {
        for (int t = 0; t < times % 4; t++) {
            char[][] newFace = new char[3][3];
            
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    newFace[j][2-i] = state[face][i][j];
                }
            }
            
            state[face] = newFace;
        }
    }
    
    private void rotateU(int times) {
        for (int t = 0; t < times % 4; t++) {
            // Rotate the U face
            rotateFace(0, 1);
            
            // Rotate the adjacent faces
            char[] temp = new char[3];
            for (int i = 0; i < 3; i++) {
                temp[i] = state[2][0][i]; // Store front top row
            }
            
            // Move right to front
            for (int i = 0; i < 3; i++) {
                state[2][0][i] = state[5][0][i];
            }
            
            // Move back to right
            for (int i = 0; i < 3; i++) {
                state[5][0][i] = state[3][0][i];
            }
            
            // Move left to back
            for (int i = 0; i < 3; i++) {
                state[3][0][i] = state[4][0][i];
            }
            
            // Move temp (front) to left
            for (int i = 0; i < 3; i++) {
                state[4][0][i] = temp[i];
            }
        }
    }
    
    private void rotateD(int times) {
        for (int t = 0; t < times % 4; t++) {
            // Rotate the D face
            rotateFace(1, 1);
            
            // Rotate the adjacent faces
            char[] temp = new char[3];
            for (int i = 0; i < 3; i++) {
                temp[i] = state[2][2][i]; // Store front bottom row
            }
            
            // Move left to front
            for (int i = 0; i < 3; i++) {
                state[2][2][i] = state[4][2][i];
            }
            
            // Move back to left
            for (int i = 0; i < 3; i++) {
                state[4][2][i] = state[3][2][i];
            }
            
            // Move right to back
            for (int i = 0; i < 3; i++) {
                state[3][2][i] = state[5][2][i];
            }
            
            // Move temp (front) to right
            for (int i = 0; i < 3; i++) {
                state[5][2][i] = temp[i];
            }
        }
    }
    
    private void rotateF(int times) {
        for (int t = 0; t < times % 4; t++) {
            // Rotate the F face
            rotateFace(2, 1);
            
            // Rotate the adjacent faces
            char[] temp = new char[3];
            for (int i = 0; i < 3; i++) {
                temp[i] = state[0][2][i]; // Store up bottom row
            }
            
            // Move left to up
            for (int i = 0; i < 3; i++) {
                state[0][2][i] = state[4][2-i][2];
            }
            
            // Move down to left
            for (int i = 0; i < 3; i++) {
                state[4][i][2] = state[1][0][i];
            }
            
            // Move right to down
            for (int i = 0; i < 3; i++) {
                state[1][0][i] = state[5][2-i][0];
            }
            
            // Move temp (up) to right
            for (int i = 0; i < 3; i++) {
                state[5][i][0] = temp[i];
            }
        }
    }
    
    private void rotateB(int times) {
        for (int t = 0; t < times % 4; t++) {
            // Rotate the B face
            rotateFace(3, 1);
            
            // Rotate the adjacent faces
            char[] temp = new char[3];
            for (int i = 0; i < 3; i++) {
                temp[i] = state[0][0][i]; // Store up top row
            }
            
            // Move right to up
            for (int i = 0; i < 3; i++) {
                state[0][0][i] = state[5][2-i][2];
            }
            
            // Move down to right
            for (int i = 0; i < 3; i++) {
                state[5][i][2] = state[1][2][i];
            }
            
            // Move left to down
            for (int i = 0; i < 3; i++) {
                state[1][2][i] = state[4][2-i][0];
            }
            
            // Move temp (up) to left
            for (int i = 0; i < 3; i++) {
                state[4][i][0] = temp[i];
            }
        }
    }
    
    private void rotateL(int times) {
        for (int t = 0; t < times % 4; t++) {
            // Rotate the L face
            rotateFace(4, 1);
            
            // Rotate the adjacent faces
            char[] temp = new char[3];
            for (int i = 0; i < 3; i++) {
                temp[i] = state[0][i][0]; // Store up left column
            }
            
            // Move back to up
            for (int i = 0; i < 3; i++) {
                state[0][i][0] = state[3][2-i][2];
            }
            
            // Move down to back
            for (int i = 0; i < 3; i++) {
                state[3][i][2] = state[1][2-i][0];
            }
            
            // Move front to down
            for (int i = 0; i < 3; i++) {
                state[1][i][0] = state[2][i][0];
            }
            
            // Move temp (up) to front
            for (int i = 0; i < 3; i++) {
                state[2][i][0] = temp[i];
            }
        }
    }
    
    private void rotateR(int times) {
        for (int t = 0; t < times % 4; t++) {
            // Rotate the R face
            rotateFace(5, 1);
            
            // Rotate the adjacent faces
            char[] temp = new char[3];
            for (int i = 0; i < 3; i++) {
                temp[i] = state[0][i][2]; // Store up right column
            }
            
            // Move front to up
            for (int i = 0; i < 3; i++) {
                state[0][i][2] = state[2][i][2];
            }
            
            // Move down to front
            for (int i = 0; i < 3; i++) {
                state[2][i][2] = state[1][i][2];
            }
            
            // Move back to down
            for (int i = 0; i < 3; i++) {
                state[1][i][2] = state[3][2-i][0];
            }
            
            // Move temp (up) to back
            for (int i = 0; i < 3; i++) {
                state[3][i][0] = temp[2-i];
            }
        }
    }
    
    // Heuristic for IDA* algorithm
    public int getHeuristic() {
        // A simple heuristic: count misplaced stickers
        int misplaced = 0;
        for (int face = 0; face < 6; face++) {
            char centerColor = state[face][1][1];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (state[face][i][j] != centerColor) {
                        misplaced++;
                    }
                }
            }
        }
        return misplaced / 8; // Divide by 8 to make it admissible
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RubiksCube other = (RubiksCube) obj;
        return Arrays.deepEquals(state, other.state);
    }
    
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(state);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String[] faceNames = {"Up", "Down", "Front", "Back", "Left", "Right"};
        
        for (int face = 0; face < 6; face++) {
            sb.append(faceNames[face]).append(" face:\n");
            for (int i = 0; i < 3; i++) {
                sb.append("  ");
                for (int j = 0; j < 3; j++) {
                    sb.append(state[face][i][j]).append(" ");
                }
                sb.append("\n");
            }
        }
        
        return sb.toString();
    }
}