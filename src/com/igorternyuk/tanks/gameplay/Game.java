package com.igorternyuk.tanks.gameplay;

import com.igorternyuk.tanks.graphics.Display;
import com.igorternyuk.tanks.input.KeyboardState;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import com.igorternyuk.tanks.utils.Time;

/**
 *
 * @author igor
 */
public class Game implements Runnable {
    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;
    private static final String TITLE = "Tanks";
    private static final int CLEAR_COLOR = 0xff000000;
    private static final int NUM_BUFFERS = 4;
    private static final float FPS = 60.0f;
    private static final float FRAME_TIME = Time.SECOND / FPS;
    private static final long IDLE_TIME = 1;
    
    private boolean running = false;
    private Thread gameThread;
    private Display display;
    private Graphics2D graphics;
    private KeyboardState keyboardState;
    
    public Game(){
        this.display = Display.create(WIDTH, HEIGHT, TITLE, NUM_BUFFERS,
                CLEAR_COLOR);
        this.graphics = this.display.getGraphics();
        this.keyboardState = new KeyboardState();
        this.display.addInputListener(this.keyboardState);
        this.display.getWindow().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e){
                onWindowCloseRequest();
            }
        });
    }
    
    public void onWindowCloseRequest(){
        stop();
        System.exit(0);
    }
    
    public synchronized void start(){
        if(this.running)
            return;
        this.running = true;
        this.gameThread = new Thread(this);
        this.gameThread.start();
    }
    
    public synchronized void stop(){
        if(!this.running)
            return;
        this.running = false;
        try {
            this.gameThread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
        cleanUp();
    }
    
    public void update(){
        
    }
    
    public void render(){
        this.display.clear();
        
        this.display.swapBuffers();
    }
    
    @Override
    public void run() {
        int fps = 0;
        int updates = 0;
        int auxillaryUpdates = 0;
        long auxillaryTimer = 0;
        long timeSinceLastUpdate = 0;
        long lastTime = Time.get();
        System.out.println("FrameTime = " + FRAME_TIME);
        while(this.running){
            long currentTime = Time.get();
            long elapsedTime = currentTime - lastTime;
            lastTime = currentTime;
            timeSinceLastUpdate += elapsedTime;
            auxillaryTimer += elapsedTime;
            boolean needToRender = false;            
            while(timeSinceLastUpdate > FRAME_TIME){
                timeSinceLastUpdate -= FRAME_TIME;
                update();
                ++updates;
                if(needToRender){
                    ++auxillaryUpdates;
                } else {
                    needToRender = true;
                }                
            }
            
            if(needToRender){
                render();
                ++fps;
            } else {
                try {
                    Thread.sleep(IDLE_TIME);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if(auxillaryTimer >= Time.SECOND){
                this.display.setTitle(TITLE + " || FPS: " + fps + " | Upd: "
                        + updates + " | Updl: " + auxillaryUpdates);
                fps = 0;
                updates = 0;
                auxillaryUpdates = 0;
                auxillaryTimer = 0;
            }
        }
    }
    
    public void cleanUp(){
        this.display.destroy();
    }
}

/*
Игровой процесс

Игра состоит из 35 основных уровней. Уровень — квадратное поле из 169 секторов (13×13). При прохождении 35 уровней они начинаются заново, но пройти их сложнее из-за того, что изменяется набор вражеских танков (набор 30-го уровня), а также из-за более быстрого респауна врагов.
Препятствия

На поле находятся различные типы преград и ландшафта:
Кирпичная стена 	Разрушается от одного или нескольких выстрелов танка. Танк без бонусов или с одной-двумя «звёздами» уничтожает снарядом четверть стены; танк с тремя «звёздами» уничтожает снарядом сразу половину стены.
Бетонная стена 	Можно разрушить, только собрав три бонуса, улучшающие танк (в виде пятиконечной звезды).
Кусты 	Резко снижают видимость бронетехники и снарядов.
Лёд 	Снижает управляемость передвижения танка.
Вода 	Блокирует передвижение, но пропускает снаряды. 

Улучшение танка игрока

    без звезд — малый танк с минимальной скоростью полета снаряда.
    1 звезда — лёгкий танк с высокой скоростью полета снаряда.
    2 звезды — средний танк с возможностью стрелять очередями.
    3 звезды — тяжёлый танк, способный пробивать бетон.

Несмотря на улучшения, танк игрока после уничтожения перерождается около базы в виде малого танка.
Вражеские танки

В игре имеется четыре типа вражеских танков, которые различаются скоростью и прочностью:

    обычный танк (100 очков);
    бронетранспортёр, который отличается повышенной скоростью хода (200 очков);
    скорострельный танк (300 очков);
    тяжёлый танк (броневик), уничтожить который можно четырьмя попаданиями (танк меняет цвет в зависимости от оставшейся прочности) (400 очков).

Четвёртый, одиннадцатый и восемнадцатый танки, независимо от типа, появляются переливающиеся цветами. Если игрок попадает в такой танк, то на карте появляется бонус. В случае если «бонусным» танком является тяжёлый танк, достаточно одного попадания в него для появления бонуса. Если не взять бонус до появления нового переливающегося танка, то бонус исчезает. В некоторых версиях Tank 1990 вражеские танки так же могут подбирать бонусы. Например, если кто-то из них возьмёт «лопату», (при взятии игроком — возводит вокруг базы бетонную стену на какое-то время) кирпичная кладка, защищающая базу исчезает.

При игре в одиночку на карте находится не более четырёх танков противника одновременно; при игре вдвоём их не более шести. Всего же танков противника двадцать на уровне. 

Бонусы

Существует несколько бонусов:

    Танк («жизнь»). Прибавляет игроку одну жизнь.
    Пятиконечная звезда. Улучшает танк игрока. В некоторых вариациях попадается "Пистолет" (танк квалифицируется до тяжёлого).
    Ручная граната («бомба»). Взрывает танки противника на карте, за их уничтожение очки не начисляются — только 500 очков за взятие бонуса как такового.
    Часы. На некоторое время останавливает врагов и их стрельбу.
    Штыковая лопата. Временно делает кирпичную стену штаба бетонной, что защищает его от вражеских снарядов. Если кирпичное ограждение вокруг штаба было уничтожено, после прекращения действия лопаты (бетонная стена) вокруг штаба восстанавливается кирпичная стена.
    Каска («броня»). Временно делает танк игрока неуязвимым.
    Корабль. Позволяет проходить через воду. Также является альтернативой Каске.

Всего за один уровень возможно получить три бонуса. Взятие бонуса приносит 500 очков. Бонус появляется после попадания по мигающему танку.

Изначально разработчиками была заложена возможность ещё двух бонусов (место в CHR ROM это позволяло). По крайней мере, графика одного бонуса (в виде пистолета) присутствовала в оригинальной игре всегда, а графикой второго бонуса стала капля, которая могла появиться только при показе секретного сообщения от разработчика. Более того, алгоритм появления бонуса таков, что изначально позволял получать индекс бонуса из генератора псевдослучайных чисел, а затем выбирать из массива непосредственно в ПЗУ: .BYTE 0, 1, 2, 3, 4, 5, 4, 3. Два последних бонуса были заменены на, соответственно, ручную гранату и часы. Поэтому они статистически встречаются чаще.[3] 


*/
        