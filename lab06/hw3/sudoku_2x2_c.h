#ifndef sudoku_2x2_c_h
#define sudoku_2x2_c_h

bool check(char *board, int location, char c) {
    int row = location / 4;
    int col = location % 4;
    // check row
    for (int i = 0; i < 4; i++) {
        if (board[row * 4 + i] == c) return false;
    }

    // check col
    for (int i = 0; i < 4; i++) {
        if (board[col + i * 4] == c) return false;
    }
    
    // check block
    row = row - row % 2;
    col = col - col % 2;

    if (board[row*4 + col] == c) return false;
    if (board[row*4 + col + 1] == c) return false;
    if (board[(row+1)*4 + col] == c) return false;
    if (board[(row+1)*4 + col+1] == c) return false;
    
    return true;
}

bool solve(char *board, int location) {
    if (location == 16) return true;
    if (board[location] == 0) {
        for (char i = 1; i < 5; i++) {
            if (check(board, location, i)) {
                board[location] = i;
                if (solve(board, location + 1)) return true;
                board[location] = 0;
            }
        }
    }
    else return solve(board, location + 1);
    return false;
}

void sudoku_2x2_c(char *test_c_data){
    solve(test_c_data, 0);
}

#endif