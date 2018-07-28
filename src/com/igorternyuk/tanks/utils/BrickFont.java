package com.igorternyuk.tanks.utils;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetIdentifier;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetManager;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author igor
 */
public class BrickFont {

    private static final String[][] DIGITS = {
        {
            "XXXX",
            "X  X",
            "X  X",
            "X  X",
            "XXXX"
        },
        {
            "   X",
            "   X",
            "   X",
            "   X",
            "   X"
        },
        {
            "XXXX",
            "   X",
            "XXXX",
            "X   ",
            "XXXX "
        },
        {
            "XXXX",
            "   X",
            "XXXX",
            "   X",
            "XXXX"
        },
        {
            "X  X",
            "X  X",
            "XXXX",
            "   X",
            "   X"
        },
        {
            "XXXX",
            "X   ",
            "XXXX",
            "   X",
            "XXXX"
        },
        {
            "XXXX",
            "X   ",
            "XXXX",
            "X  X",
            "XXXX"
        },
        {
            "XXXX",
            "   X",
            "   X",
            "   X",
            "   X"
        },
        {
            "XXXX",
            "X  X",
            "XXXX",
            "X  X",
            "XXXX"
        },
        {
            "XXXX",
            "X  X",
            "XXXX",
            "   X",
            "XXXX"
        }
    };
    private static final String[][] ALPHABET = {
        //A 1
        {
            " XX ",
            "X  X",
            "XXXX",
            "X  X",
            "X  X"
        },
        //B 2
        {
            "XXX ",
            "X  X",
            "XXX ",
            "X  X",
            "XXX "
        },
        //C 3
        {
            " XX ",
            "X  X",
            "X   ",
            "X  X",
            " XX "
        },
        //D 4
        {
            "XXX ",
            "X  X",
            "X  X",
            "X  X",
            "XXX "
        },
        //E 5 
        {
            " XX ",
            "X  X",
            "XXXX",
            "X   ",
            " XXX"
        },
        //F 6
        {
            "XXXX",
            "X   ",
            "XXXX",
            "X   ",
            "X   "
        },
        //G 7
        {
            " XXX",
            "X   ",
            "X XX",
            "X  X",
            " XX "
        },
        //H 8
        {
            "X  X",
            "X  X",
            "XXXX",
            "X  X",
            "X  X"
        },
        //I 9
        {
            "XXX",
            " X ",
            " X ",
            " X ",
            "XXX"
        },
        //J 10
        {
            "   X",
            "   X",
            "   X",
            "X  X",
            " XX "
        },
        //K 11
        {
            "X  X",
            "X X ",
            "XX  ",
            "X X ",
            "X  X"
        },
        //L 12
        {
            "X   ",
            "X   ",
            "X   ",
            "X   ",
            "XXXX"
        },
        //M 13
        {
            "X   X",
            "XX XX",
            "X X X",
            "X   X",
            "X   X"
        },
        //N 14
        {
            "X   X",
            "XX  X",
            "X X X",
            "X  XX",
            "X   X"
        },
        //O 15 
        {
            " XX ",
            "X  X",
            "X  X",
            "X  X",
            " XX "
        },
        //P 16
        {
            "XXX ",
            "X  X",
            "XXX ",
            "X   ",
            "X   "
        },
        //Q 17
        {
            " XX  ",
            "X  X ",
            "X  X ",
            "X XX ",
            " XX X"
        },
        //R 18
        {
            "XXX",
            "X  X",
            "XXX ",
            "X  X",
            "X  X"
        },
        //S 19
        {
            " XXXX",
            "X    ",
            "  X  ",
            "    X",
            "XXXX "
        },
        //T 20
        {
            "XXXXX",
            "X X X",
            "  X  ",
            "  X  ",
            " XXX "
        },
        //U 21
        {
            "X  X",
            "X  X",
            "X  X",
            "X  X",
            " XX "
        },
        //V 22
        {
            "X  X",
            "X  X",
            "X  X",
            "X X ",
            "XX  "
        },
        //W 23
        {
            "X   X",
            "X   X",
            "X X X",
            "X X X",
            " X X "
        },
        //X 24
        {"X    X",
            " X X ",
            "  X  ",
            " X X ",
            "X   X"},
        //Y 25
        {
            "X   X",
            "X   X",
            " XXX ",
            "  X  ",
            "  X  "
        },
        //Z 26
        {
            "XXXXX",
            "   X ",
            "  X  ",
            " X   ",
            "XXXXX"
        }
    };

    private static final Map<Character, String[]> ALPHABET_MAP =
            createAlphabet();

    private static Map<Character, String[]> createAlphabet() {
        Map<Character, String[]> alphabetMap = new HashMap<>();
        for (int i = 65; i <= 90; ++i) {
            alphabetMap.put((char) i, ALPHABET[i - 65]);
        }
        return alphabetMap;
    }

    public static int getLetterWidth(Character letter) {
        if(Character.isDigit(letter)){
            return DIGITS[Character.getNumericValue(letter)].length;
        } else {
            return ALPHABET_MAP.get(letter)[0].length();
        }
    }

    public static void drawWithBricksCentralized(Graphics2D g, String word,
            int topLeftY) {
        BufferedImage brickImage = Images.resizeImage(SpriteSheetManager.
                getInstance().get(SpriteSheetIdentifier.BRICK), 0.75
                * Game.SCALE);

        int BRICK_SIZE = brickImage.getWidth();
        char[] wordLetters = word.toCharArray();

        int width = 0;
        for (char letter : wordLetters) {
            width += (getLetterWidth(letter) + 1) * BRICK_SIZE;
        }

        width -= BRICK_SIZE;

        int topLeftX = (Game.WIDTH - width) / 2;

        drawWithBricks(g, word, topLeftX, topLeftY);

    }

    public static void drawWithBricks(Graphics2D g, String word, int topLeftX,
            int topLeftY) {

        word = word.toUpperCase();

        BufferedImage brickImage = Images.resizeImage(SpriteSheetManager.
                getInstance().get(SpriteSheetIdentifier.BRICK), 0.75
                * Game.SCALE);

        int BRICK_SIZE = brickImage.getWidth();

        char[] wordLetters = word.toCharArray();

        int right = topLeftX;

        for (int k = 0; k < wordLetters.length; ++k) {
            char currLetter = wordLetters[k];
            int index = Character.getNumericValue(currLetter);
            String[] letter = Character.isDigit(currLetter) ? DIGITS[index] :
                    ALPHABET_MAP.get(wordLetters[k]);
            for (int y = 0; y < letter.length; ++y) {
                String row = letter[y];
                char[] lettersInRow = row.toCharArray();
                for (int x = 0; x < lettersInRow.length; ++x) {
                    char currChar = letter[y].charAt(x);
                    if (currChar == 'X') {
                        g.drawImage(brickImage,
                                right + x * BRICK_SIZE,
                                topLeftY + y * BRICK_SIZE,
                                null);
                    }
                }
            }
            right += (getLetterWidth(wordLetters[k]) + 1) * BRICK_SIZE;
        }
    }
}
