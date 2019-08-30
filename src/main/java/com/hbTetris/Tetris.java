package com.hbTetris;

import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class Tetris extends JPanel implements Runnable, ActionListener
{

    public static int sqlength = 17;         // �뵾�뒪 理쒖냼�떒�쐞 議곌컖�쓽 �겕湲�
    public static final int xoffset = 120;      // 寃����깋�쑝濡� 梨꾩썙吏��뒗 諛곌꼍�쓽 �떆�옉�젏
    static int cols = 10;            // 諛곌꼍�쓽 媛�濡쒕쾾�쐞
    static int rows = 20;            // 諛곌꼍�쓽 �꽭濡쒕쾾�쐞
    public static int[][] f;                     // �룄�삎�씠 �궡�젮�삤�뒗 寃뚯엫�씠 �떎�뻾�릺�뒗 �솕硫�
    public static int privewnum;   // �뵾�뒪瑜� 留뚮뱶�뒗 寃쎌슦�쓽 �닔
    public int curpiecenum;                     // 誘몃━蹂닿린 �뵾�뒪�쓽 寃쎌슦�쓽 �닔
    public static int holdpiecenum=9;                     // hold�쓽 寃쎌슦�쓽 �닔
    public static Square[] curpiece = new Square[4];
    public static boolean lost;
    public boolean neednewpiece = true;   // �깉濡쒖슫 �뵾�뒪媛� �븘�슂�븳吏� �뙋�떒
    public boolean change = true;          // Hold�뿉�꽌 �쁽�옱 HOLD媛� 媛��뒫�븳 �긽�깭�씤吏� �뙋�떒
    public static Thread Player1_t = null;         // Thread
    public Color[] colors;
    public static int  score = 0;            //珥앹젏�쓣 珥덇린�솕
    public static int level;            //吏꾪뻾 �젅踰�
    public static int removeline;
    public static int removelinecnt;   //吏��슫 �씪�씤�쓽 媛쒖닔
    public static int hold=0; // hold踰꾪듉 �겢由� �뿬遺�
    public boolean pause = true; //pause踰꾪듉 �엯�젰 �뿬遺�
    public boolean needpieceP = true;
    public static boolean update =false;
    public boolean holdrepaint =true;
    
    public Tetris()
    {
        //addKeyListener(new KeyHandler());
    	System.out.println("1 : "+curpiecenum);
        this.colors = new Color[8];               // �깋�꽕�젙 遺�遺�
        this.colors[0] = new Color(40,40,40);      // 寃����깋
        this.colors[1] = new Color(255,0,0);         // 鍮④컙�깋
        this.colors[2] = new Color(0,255,0);         // 珥덈줉�깋
        this.colors[3] = new Color(0,200,255);      // �끂���깋
        this.colors[4] = new Color(255,255,0);      // �븯�뒛�깋
        this.colors[5] = new Color(255,150,0);      // �솴�넗�깋
        this.colors[6] = new Color(210,0,240);      // 蹂대씪�깋
        this.colors[7] = new Color(40,0,240);      // �뙆���깋

        f = new int[cols][rows + 4];
        start();
    }

    public void start()
    {
        for(int i=0; i< cols; i++)
        {
            for(int j=0; j< rows+4; j++)
            {
                f[i][j] = 0;         // 珥덇린諛곌꼍�쓣 寃����깋�쑝濡� �꽕�젙
            }
        }

        level = 1;                  // 媛� 蹂��닔珥덇린�솕
        score = 0;
        removeline = 0;
        removelinecnt=0;
        neednewpiece = true;
        lost= false;
        pause=true;
        holdpiecenum=9;
        privewnum = (int) (Math.random()*7);
        repaint();
        //tetris2.repaint();
        (Player1_t = new Thread(this)).start();   // �벐�젅�뱶 �샇異�
        //requestFocus();                     // 留덉슦�뒪 �룷而ㅼ뒪 �슂泥��븯�뿬 �떆�옉�긽�깭瑜� �븣由�
    }

    public synchronized void stop()
    {
        pause = false;
        if(Player1_t != null)  Player1_t.stop();
        Player1_t = null;
    }

    public synchronized void restart()
    {
        (Player1_t = new Thread(this)).start();   // �벐�젅�뱶 �샇異�
        pause=true;

    }

    @Override
    public void run()
    {
        f = new int[cols][rows + 4];

        while (!lost)
        {
            int tim=level;

            // update
            if (neednewpiece)
            {
                level = 1 + removeline/10;   // �젅踰� �긽�듅 議곗젅(�궘�젣�릺�뒗 �씪�씤湲곗�)
                removelines();
                newpiece();
                privewnum = (int) (Math.random()*7); //誘몃━蹂닿린 �뵾�뒪�뿉 蹂댁뿬以� �룄�삎�쓽 媛믪쓣 �옖�뜡�쑝濡� �꽔�뼱以�
                neednewpiece = false;
            }
            else
            {
                neednewpiece = !movecurpiece(0,-1,false);   // �떎�젣濡� �벐�젅�뱶瑜� �븘�옒濡� �씠�룞
            }

            // render
            repaint(); //update瑜� �슂泥� update �넂 paint �샇異�
            //tetris2.repaint();
            try
            {
                if(neednewpiece)
                {
                    tim=100;
                    Thread.sleep(tim);
                }
                Thread.sleep(1000/tim);   // �벐�젅�뱶瑜� �씠�슜�븳 �냽�룄 議곗젅
            }
            catch (InterruptedException e){   }
        }
    }

    public void switchPieceShape(int piecenum, int m) {
        switch (piecenum) //�쁽�옱 �궡�젮�삱 �뵾�뒪�쓽 媛믪뿉 �뵲�씪 curpiece諛곗뿴�뿉 �룄�삎 議곌컖�쓣 �꽔�뼱以�
        {
            case 0:
                //  �뀅�뀅m�뀅
                curpiece[0] = new Square(m  , rows-1, 1);      // 0踰� 諛곗뿴�� 湲곗� 議곌컖
                curpiece[1] = new Square(m-1, rows-1, 1);
                curpiece[2] = new Square(m+1, rows-1, 1);
                curpiece[3] = new Square(m+2, rows-1, 1);
                break;
            case 1:
                //   �뀅m�뀅
                //     m
                curpiece[0] = new Square(m  , rows-1, 2);
                curpiece[1] = new Square(m-1, rows-1, 2);
                curpiece[2] = new Square(m  , rows-2, 2);
                curpiece[3] = new Square(m+1, rows-1, 2);
                break;
            case 2:
                //    m�뀅
                //  �뀅m
                curpiece[0] = new Square(m  , rows-2, 3 );
                curpiece[1] = new Square(m-1, rows-2, 3);
                curpiece[2] = new Square(m  , rows-1, 3);
                curpiece[3] = new Square(m+1, rows-1, 3);
                break;
            case 3:
                // �뀅m
                //   m�뀅
                curpiece[0] = new Square( m  , rows-2, 4);
                curpiece[1] = new Square( m+1, rows-2, 4);
                curpiece[2] = new Square( m-1, rows-1, 4);
                curpiece[3] = new Square( m  , rows-1, 4);
                break;
            case 4:
                //  �뀅m
                //  �뀅m
                curpiece[0] = new Square( m-1, rows-2, 5);
                curpiece[1] = new Square( m  , rows-2, 5);
                curpiece[2] = new Square( m-1, rows-1, 5);
                curpiece[3] = new Square( m  , rows-1, 5);
                break;
            case 5:
                //    �뀅
                // �뀅m�뀅
                curpiece[0] = new Square( m  , rows-2, 6);
                curpiece[1] = new Square( m-1, rows-2, 6);
                curpiece[2] = new Square( m+1, rows-2, 6);
                curpiece[3] = new Square( m+1, rows-1, 6);
                break;
            case 6:
                // �뀅
                // �뀅m�뀅
                curpiece[0] = new Square( m  , rows-2, 7);
                curpiece[1] = new Square( m-1, rows-2, 7);
                curpiece[2] = new Square( m+1, rows-2, 7);
                curpiece[3] = new Square( m-1, rows-1, 7);
                break;
        }
    }

    public void newpiece()         // �깉�뵾�뒪 �깮�꽦 遺�遺�
    {
        Square[] old = new Square[4];   // �뵾�뒪瑜� �깮�꽦�쓣 �쐞�븳 �뵒�뤃�듃 議곌컖 援ъ꽦
        old[0]  = old[1] = old[2] = old[3] = new Square(-1, -1, 0);
        change=true; //hold媛��뒫
        int m = cols/2;  //�뵾�뒪瑜� 媛��슫�뜲 �쐞移섏떆耳� �궡�젮�삤寃� �븿
        needpieceP = true;
        update =true;
        curpiecenum = privewnum;            // 誘몃━蹂닿린�뿉�꽌 蹂댁뿬以� �뵾�뒪瑜� 寃뚯엫�뿉 �씠�슜�릺�뒗 �뵾�뒪�뿉 �쟻�슜
        switchPieceShape(curpiecenum, m);
        lost = !movepiece(old, curpiece);      // �뵾�뒪媛� �떎�뙎�뿬�꽌 ��吏곸씪 �닔 �뾾�쑝硫� 寃뚯엫�쓣 醫낅즺�릺寃� �븿
    }

    public void holdpiece()         // ���뱶 �꽑�깮 �떆 �깉�뵾�뒪 �깮�꽦 遺�遺�
    {

        Square[] old = new Square[4];   // �뵾�뒪瑜� �깮�꽦�쓣 �쐞�븳 �뵒�뤃�듃 議곌컖 援ъ꽦
        old[0]  = old[1] = old[2] = old[3] = new Square(-1, -1, 0);

        int m = cols/2;
        switchPieceShape(curpiecenum, m);
        lost = !movepiece(old, curpiece);      // �뵾�뒪媛� �떎�뙎�뿬�꽌 ��吏곸씪 �닔 �뾾�쑝硫� 寃뚯엫�쓣 醫낅즺�릺寃� �븿
    }

    public void hold()
    {
        hold++;
        if (hold==1) //hold媛� 寃뚯엫 以� 泥섏쓬 �떎�뻾 �릺�뿀�쓣 �븣
        {
            // �쁽�옱 吏뺥뻾以묒씤 �뵾�뒪 ���뱶�뿉 �씠�룞
            holdpiecenum = curpiecenum;

            // �쁽�옱 吏꾪뻾以묒씤 �뵾�뒪 �젣嫄�
            for(int i=0; i<4; i++) {
                f[curpiece[i].x][curpiece[i].y] = 0;
            }

            // �깉濡쒖슫 �뵾�뒪 �깮�꽦
            newpiece();
            hold=2;
        }
        else if(hold>=2) //holdpiecenumm�뿉 �씠誘� 媛믪씠 �엳�뒗 寃쎌슦
        {
            // �쁽�옱 �뵾�뒪�� hold�쓽 �뵾�뒪 媛믪쓣 蹂�寃쎌떆耳쒖쨲
            int tmp=holdpiecenum;
            holdpiecenum=curpiecenum;
            curpiecenum=tmp;

            // �쁽�옱 吏꾪뻾以묒씤 �뵾�뒪 �젣嫄�
            for(int i=0; i<4; i++) {
                f[curpiece[i].x][curpiece[i].y] = 0;
            }

            //蹂�寃쎈맂 媛믪쓽 �쁽�옱 �뵾�뒪瑜� �옱 �깮�꽦
            holdpiece();
            change=false; //�븳踰� hold媛� �씠琉꾩쭊 寃쎌슦 �떎�쓬 �뵾�뒪媛� �궡�젮�삱�븣 源뚯� �룞�옉 �븯吏� �븡�쓬
        }
    }

    public void drawPreview(int piece, int x, int y, Graphics g) {
        switch (piece)         // 誘몃━蹂닿린 �뵾�뒪
        {
            case 0:
                //  �뀅�뀅m�뀅
                g.setColor(colors[1]);
                g.fill3DRect(x, y+120 + sqlength, sqlength, sqlength, true);
                g.fill3DRect(x+sqlength, y+120 + sqlength, sqlength, sqlength, true);
                g.fill3DRect(x+sqlength*2, y+120 + sqlength, sqlength, sqlength, true);
                g.fill3DRect(x+sqlength*3, y+120 + sqlength, sqlength, sqlength, true);

                break;
            case 1:
                //   �뀅m�뀅4
                //      m
                g.setColor(colors[2]);
                g.fill3DRect(x, y+120, sqlength, sqlength, true);
                g.fill3DRect(x+sqlength, y+120, sqlength, sqlength, true);
                g.fill3DRect(x+sqlength*2, y+120, sqlength, sqlength, true);
                g.fill3DRect(x+sqlength, y+120 + sqlength, sqlength, sqlength, true);
                break;
            case 2:
                //    m�뀅
                //  �뀅m
                g.setColor(colors[3]);
                g.fill3DRect(x, y+120 + sqlength, sqlength, sqlength, true);
                g.fill3DRect(x+sqlength, y+120 + sqlength, sqlength, sqlength, true);
                g.fill3DRect(x+sqlength, y+120, sqlength, sqlength, true);
                g.fill3DRect(x+sqlength*2, y+120, sqlength, sqlength, true);
                break;
            case 3:
                // �뀅m
                //   m�뀅
                g.setColor(colors[4]);
                g.fill3DRect(x + sqlength*2, y+120 + sqlength, sqlength, sqlength, true);
                g.fill3DRect(x + sqlength, y+120 + sqlength, sqlength, sqlength, true);
                g.fill3DRect(x + sqlength, y+120, sqlength, sqlength, true);
                g.fill3DRect(x, y+120, sqlength, sqlength, true);
                break;
            case 4:
                //  m�뀅
                //  m�뀅
                g.setColor(colors[5]);
                g.fill3DRect(x, y+120, sqlength, sqlength, true);
                g.fill3DRect(x, y+120 + sqlength, sqlength, sqlength, true);
                g.fill3DRect(x + sqlength, y+120, sqlength, sqlength, true);
                g.fill3DRect(x + sqlength, y+120 + sqlength, sqlength, sqlength, true);
                break;
            case 5:
                //    �뀅
                // �뀅m�뀅
                g.setColor(colors[6]);
                g.fill3DRect(x+sqlength*2, y+120, sqlength, sqlength, true);
                g.fill3DRect(x, y+120 + sqlength, sqlength, sqlength, true);
                g.fill3DRect(x+sqlength, y+120 + sqlength, sqlength, sqlength, true);
                g.fill3DRect(x+sqlength*2, y+120 + sqlength, sqlength, sqlength, true);
                break;
            case 6:
                // �뀅
                // �뀅m�뀅
                g.setColor(colors[7]);
                g.fill3DRect(x, y+120, sqlength, sqlength, true);
                g.fill3DRect(x, y+120 + sqlength, sqlength, sqlength, true);
                g.fill3DRect(x+sqlength, y+120 + sqlength, sqlength, sqlength, true);
                g.fill3DRect(x+sqlength*2, y+120 + sqlength, sqlength, sqlength, true);
                break;
        }
    }

    @Override
    public synchronized void paint(Graphics g)
    {
        g.setFont(new java.awt.Font("impact",10, 16));      // 게임의 상태를 보여주는 글자들을 생성하는 부분
        int gx = sqlength;
        int gy = sqlength*rows/4;
        g.drawString("Score: " + score, gx, gy);
        g.drawString("Removeline: " + removeline, gx, gy+30);
        g.drawString("Level: " + level, gx, gy + 60);
        g.drawString("Next: ", gx, gy + 90);
        g.drawString("Hold: ", gx, gy + 190);
        g.clearRect(gx, gy-25, xoffset-20, 100);
        if(update) {
            
            g.drawString("Score: " + score, gx, gy);
            g.drawString("Removeline: " + removeline, gx, gy+30);
            g.drawString("Level: " + level, gx, gy + 60);
            g.drawString("Next: ", gx, gy + 90);
            g.drawString("Hold: ", gx, gy + 190); 
        }

        for(int i =0; i<cols; i++)         // 배경에 피스모양대로 색을 페인팅
        {
            for (int j = 0; j<rows; j++)
            {
                g.setColor(colors[f[i][rows-1-j]]);
                g.fill3DRect(xoffset+sqlength*i, 3*sqlength + sqlength*j, sqlength, sqlength,true);
            }
        }
        g.clearRect(gx, gy+100, xoffset-20, 70);  
//        if(needpieceP)
//        {
//            g.clearRect(gx, gy+100, xoffset-20, 70);         // 게임정보를 변경하기 위해서 지워주는 부분
//
//        }


//        if(!holdrepaint)
//            g.clearRect(gx, gy+200, xoffset-20, 300);
//        holdrepaint=true;

        drawPreview(privewnum, gx, gy, g);
        drawPreview(holdpiecenum, gx, gy+100, g);
    }

    public synchronized boolean movecurpiece(int byx, int byy, boolean rotate)
    {
        Square[] newpos = new Square[4];

        for(int i =0; i<4; i++)
        {
            if (rotate)      // �뵾�뒪�쓽 �쉶�쟾 遺�遺�
            {
                if(curpiecenum!=0)
                {
                    //�쁽�옱 �뵾�뒪�쓽 �쐞移� 醫뚰몴瑜� �씫�뼱 �샂
                    int dx = curpiece[i].x - curpiece[0].x;
                    int dy = curpiece[i].y - curpiece[0].y;
                    newpos[i] = new Square(curpiece[0].x - dy, curpiece[0].y + dx, curpiece[i].c);
                }
                else
                {
                    int dx = curpiece[i].x - curpiece[0].x;
                    int dy = curpiece[i].y - curpiece[0].y;
                    newpos[i] = new Square(curpiece[0].x + dy,curpiece[0].y - dx, curpiece[i].c);
                }
            }
            else      // �쉶�쟾�뾾�씠 諛붾줈 �븘�옒濡� �씠�룞
            {
                newpos[i] = new Square(curpiece[i].x + byx, curpiece[i].y + byy, curpiece[i].c);
            }

        }

        if(!movepiece(curpiece, newpos))         // �쁽�옱 �뵾�뒪�� �떎�쓬 �뵾�뒪�쓽 �씠�룞 媛��뒫�꽦�쓣 泥댄겕
            return false;

        curpiece = newpos;   // �쁽�옱 �뵾�뒪瑜� �씠�룞�맂 �뵾�뒪濡� 蹂�寃�
        return true;
    }

    public static boolean movepiece(Square[] from, Square[] to)
    {
        outerlabel:
        for (Square square1 : to) {
            if (!square1.InBounds())         // 寃쎄퀎媛� 泥댄겕
            {
                return false;
            }

            if (f[square1.x][square1.y] != 0)      // �뙎�씤 釉붾줉怨� 寃뱀튂�뒗媛�瑜� 泥댄겕
            {
                for (Square square : from) {
                    if (square1.IsEqual(square)) // �떎瑜� 釉붾줉 �떯吏� �븡�븯�쓣�븣
                    {
                        continue outerlabel;
                    }
                }
                return false;
            }
        }

        for (Square square : from) {
            if (square.InBounds()) {
                f[square.x][square.y] = 0;
            }
        }

        for (Square square : to) {
            f[square.x][square.y] = square.c;
        }
        return true;
    }

    public void removelines()
    {
        removelinecnt=0;
        outerlabel:
        for(int j=0; j<rows; j++)
        {
            for(int i=0; i<cols; i++)
            {
                if(f[i][j] == 0)      // �뙎�뿬吏� 釉붾줉以묒뿉 鍮꾩썙吏� 遺�遺꾩씠 �엳�쑝硫�
                {
                    continue outerlabel;
                }
            }
            for(int k=j; k< rows-1; k++)   // �뙎�뿬吏� 釉붾줉�뿉�꽌 �씪�씤�씠 �떎 梨꾩썙�졇 �엳�쓣 寃쎌슦
            {
                for(int i=0; i<cols; i++)
                {
                    f[i][k] = f[i][k + 1];
                }
            }
            j--;         // �떎吏덉쟻�씤 �씪�씤 �궘�젣
            removeline++;
            removelinecnt++;
        }
        if(removelinecnt == 1) {score += 50;}
        else if(removelinecnt == 2) {score += 120;}
        else if(removelinecnt == 3) {score += 190;}
        else if(removelinecnt == 4) {score += 250;}
    }

    KeyAdapter KeyHandler = new KeyAdapter() {
    	
    	
    };
   public class KeyHandler extends KeyAdapter
    {
    	private Tetris tetris;
    	
    	public KeyHandler(Tetris tetris) {
    		this.tetris = tetris;
    	}
    	
    	@Override
        public void keyPressed(KeyEvent e)
        {
    		
            switch (e.getKeyCode())
            {
                case  KeyEvent.VK_LEFT:                        // <-
                	tetris.movecurpiece(-1, 0, false);         //x 醫뚰몴瑜� �쇊履쎌쑝濡� -1 留뚰겮 ��吏곸엫.
                	tetris.neednewpiece = false;            //�깉 �뵾�뒪�뒗 �븘�슂�뾽怨�,
                	tetris.needpieceP = false;
                	tetris.repaint();                     //update �슂泥�
                    break;


                case KeyEvent.VK_RIGHT:                        //->
                	tetris.movecurpiece(1, 0, false);         //x 醫뚰몴瑜� �삤瑜몄そ�쑝濡� +1 �씠�룞
                	tetris.neednewpiece = false;
                	tetris.needpieceP = false;
                	tetris.repaint();
                    break;

                case KeyEvent.VK_UP:                        //rotate
                    if(!tetris.neednewpiece && tetris.curpiecenum != 4)
                    {                           //�쁽醫뚰몴瑜� �쑀吏��븳 �긽�깭�뿉�꽌 rotate
                    	tetris.movecurpiece(0,0,true);
                    	tetris.repaint();
                    	tetris.needpieceP = false;
                    	tetris.neednewpiece = false;
                    }
                    break;
                case KeyEvent.VK_SPACE:   //�뒪�럹�씠�뒪諛�
                    while(tetris.movecurpiece(0,-1,false));
                    tetris.repaint();
                    break;
                case KeyEvent.VK_DOWN:      // �넃
                	tetris.movecurpiece(0,-1,false);
                	tetris.neednewpiece = false;
                	tetris.needpieceP = false;
                	tetris.repaint();
                    break;
                case KeyEvent.VK_CONTROL://h
                    if(tetris.change)
                    {
                    	tetris.hold();
                    	tetris.neednewpiece = false;
                    	tetris.needpieceP = false;
                        tetris.holdrepaint =false;
                        tetris.repaint();
                    }
                    break;
                case KeyEvent.VK_P://p
                    if(tetris.pause) {
                    	tetris.stop();
                        break;
                    }
                    else
                    	tetris.restart();
                    break;
                case KeyEvent.VK_R: //s
                	tetris.stop();
                	tetris.start();
                    break;
            }
        }

	
    }

    public static void main(String[] args)
    {
    	Tetris tetris = new Tetris();
		Tetris2 tetris2 = new Tetris2();

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Tetris");
		frame.setResizable(false);
		frame.setLayout(new GridLayout(1, 2));
		
		frame.add(tetris);
		frame.add(tetris2);
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setSize((xoffset + sqlength * cols + 30) * 2, sqlength * (rows + 4) + 50);
		frame.setFocusable(true);
		
		frame.addKeyListener(tetris.new KeyHandler(tetris));
		frame.addKeyListener(tetris2.new KeyHandler(tetris2));
		frame.getContentPane().setBackground(Color.WHITE);    
		}

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub

    }
}
