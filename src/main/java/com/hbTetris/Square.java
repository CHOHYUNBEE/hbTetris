package com.hbTetris;

public class Square {
    int x, y, c;

    Square(int x, int y, int c)         // 피스생성의 최소단위인 사각형 생성
    {
        this.x = x;
        this.y = y;
        this.c = c;
    }

    boolean InBounds()               // 경계값 체크
    {
        return ( x >= 0 && x < Tetris.cols && y >= 0 && y <= Tetris.rows );
    }

    boolean IsEqual(Square s)         // 쌓여있는 피스의 경계값 체크
    {
        return x == s.x && y == s.y && c == s.c;
    }
}
