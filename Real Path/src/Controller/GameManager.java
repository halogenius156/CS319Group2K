package Controller;

import Modal.Ball;
import Modal.GameData;
import Modal.Goals;
import Modal.Headballer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import java.util.Random;

public class GameManager {

    private GameData data = new GameData();
    SoundManager soundManager = new SoundManager();

    public GameData getData() {
        return data;
    }

    public GamePhysics getPhysics() {
        return physics;
    }

    public InputManager getInManager() {
        return inManager;
    }

    private GamePhysics physics = new GamePhysics();
    private InputManager inManager = new InputManager();
    public static final Vec2 initialHeadballer1Pos = new Vec2(200, 49);
    public static final Vec2 initialHeadballer2Pos = new Vec2(1000, 49);
    public static final Vec2 initialBallPos = new Vec2(600, 600);

    private boolean isMuted = false;

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean mute) {
        isMuted = mute;
    }

    //Screen width and height
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;

    public static final int HEADSIZE = 48;
    private boolean finished = false;

    //Ball radius in pixel
    private int BALL_RADIUS = 32;

    public long getGameStartTime() {
        return GameStartTime;
    }

    public void setGameStartTime(long gameStartTime) {
        GameStartTime = gameStartTime;
    }

    private long GameStartTime;

    private World world;
    private Headballer headballer1;
    private Headballer headballer2;
    private Ball ball;

    public final float goalsHeights = 5;
    private final float goal1Width = 130;
    private final float goal2Width = 65;
    private final float goalsY = 300;
    private final float goal1X = 0;
    private final float goal2X = 1215;

    private int gameMode;
    private float gravity = -300.0f;
    private float groundFriction;
    private int selectedChar1;
    private int selectedChar2;
    private int selectedBall;
    private int selectedBackground;
    private String hb1Url;
    private String hb2Url;
    private String ballUrl;
    private String backUrl;
    private boolean goal = false;

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    private boolean pause = false;

    public boolean isPower() {
        return power;
    }

    private boolean power;


    public boolean goalScored() {
        Body ball = (Body) (this.getBall().getUserData());

        if (!goal) {
            if ((ball.getPosition().x < goal1Width) &&
                    (ball.getPosition().y < goalsY)) {
                data.updateScore(data.getScore1() + 0, data.getScore2() + 1);
                goal = true;


                return true;
            } else if ((ball.getPosition().x > goal2X - 60) &&
                    (ball.getPosition().y < goalsY)) {
                data.updateScore(data.getScore1() + 1, data.getScore2() + 0);
                goal = true;


                return true;
            }
            return false;
        }
        return false;
    }


    public void resetScene() {


        goal = false;

        Body hb1 = (Body) this.getHeadballer1().getUserData();
        Body hb2 = (Body) this.getHeadballer2().getUserData();
        Body ball = (Body) this.getBall().getUserData();


        Vec2 vel = new Vec2(0, 0);

        hb1.setTransform(initialHeadballer1Pos, 0);
        hb2.setTransform(initialHeadballer2Pos, 0);
        ball.setTransform(initialBallPos, 0);

        hb1.setLinearVelocity(vel);
        hb2.setLinearVelocity(vel);
        ball.setLinearVelocity(vel);

        if (getGameMode() == 2) {
            Random randomGenerator = new Random();
            int a = randomGenerator.nextInt(3) + 1;
            setSelectedBackground(a);
            a = randomGenerator.nextInt(2) + 1;
            setSelectedBall(a);

            getBall().setImage(getBallUrl());
            if (getSelectedBall() == 1) {
                ((Body) getBall().getUserData()).m_fixtureList.m_restitution = 1.0f;
                ((Body) getBall().getUserData()).setLinearDamping(0);
            } else if (getSelectedBall() == 2) {
                ((Body) getBall().getUserData()).m_fixtureList.m_restitution = 4.0f;
                ((Body) getBall().getUserData()).setLinearDamping(2);
            }

            if (getSelectedBackground() == 3) {
                Vec2 vec = new Vec2(0, 0);
                this.world.setGravity(vec);
            } else if (getSelectedBackground() != 3) {
                Vec2 vec = new Vec2(0, -300);
                this.world.setGravity(vec);
            }
            System.out.println(getGameMode());
        }


    }

    public void newGame() {

        this.setWorld(new World(new Vec2(0.0f, this.getGravity())));
        CollisionListener cl = new CollisionListener();
        this.world.setContactListener(cl);
        setHeadballer1(new Headballer(initialHeadballer1Pos.x, initialHeadballer1Pos.y, HEADSIZE, getHb1Url(), this));
        setHeadballer2(new Headballer(initialHeadballer2Pos.x, initialHeadballer2Pos.y, HEADSIZE, getHb2Url(), this));
        setBall(new Ball(initialBallPos.x, initialBallPos.y, getBALL_RADIUS(), getBallUrl(), this, getSelectedBall()));
        if (getSelectedBackground() == 2) {
            ((Body) (getBall().getUserData())).m_fixtureList.m_restitution = 4.0f;
        }
        //Add ground to the application, this is where balls will land
        physics.addGround(GameManager.WIDTH, 1.0f, this.world);

        //Add left and right walls so balls will not move outside the viewing area.
        physics.addWall(0, GameManager.HEIGHT, 1f, GameManager.HEIGHT, this.world); //Left wall
        physics.addWall(GameManager.WIDTH, GameManager.HEIGHT, 1f, GameManager.HEIGHT, this.world); //Right wall
        physics.addWall(0, GameManager.HEIGHT, GameManager.WIDTH, 1f, this.world); // Top wall
        physics.addGoals(this.world, goalsHeights,
                goal1Width, goal2Width, goalsY, goal1X, goal2X
        );
        GameStartTime = System.currentTimeMillis();
        if (!isMuted)
            soundManager.playGameSound();
    }

    public void stopSound(){
        soundManager.stop();
    }
    public int isFinished() {
        long tEnd2 = System.currentTimeMillis();
        long tDelta2 = tEnd2 - GameStartTime;
        double elapsedSeconds2 = tDelta2 / 1000.0;

        if (!finished) {
            if (data.getScore1() == data.getScoreLimit()) {
                //player1 won
                finished = true;
                return 1;
            } else if (data.getScore2() == data.getScoreLimit()) {
                //player2 won
                finished = true;
                return 2;
            } else if (elapsedSeconds2 >= data.getTimeLimit()) {
                finished = true;
                if (data.getScore1() > data.getScore2()) {
                    return 1;
                }
                if (data.getScore1() < data.getScore2()) {
                    return 2;
                }
                if (data.getScore1() == data.getScore2()) {
                    return 3;
                }
                return 0;
            }
            return 0;
        } else {
            return 0;
        }
    }

    public void endGame() {
        setPause(true);
    }

    public void setPowerups(boolean power) {
        this.power = power;
        if (power) {
            ((Body) getBall().getUserData()).setLinearVelocity(((Body) getBall().getUserData()).getLinearVelocity().mul(100));
            ((Body) getBall().getUserData()).m_fixtureList.m_restitution = 15;

            if (getSelectedBall() == 1) {

                ((Body) getBall().getUserData()).setLinearDamping(0);
            } else if (getSelectedBall() == 2) {

                ((Body) getBall().getUserData()).setLinearDamping(1);
            }

        } else {
            if (getSelectedBall() == 1) {
                ((Body) getBall().getUserData()).m_fixtureList.m_restitution = 1.0f;
                ((Body) getBall().getUserData()).setLinearDamping(0);
            } else if (getSelectedBall() == 2) {
                ((Body) getBall().getUserData()).m_fixtureList.m_restitution = 4.0f;
                ((Body) getBall().getUserData()).setLinearDamping(2);
            }
        }

    }


    public boolean isTouch(int hb) {
        if (hb == 1) {
            Body hb1 = (Body) getHeadballer1().getUserData();
            Body hb2 = (Body) getHeadballer2().getUserData();
            Body ball = (Body) getBall().getUserData();
            boolean touchGoal = (hb1.getWorldCenter().x < 178.0f && hb1.getWorldCenter().y > 300.0 && hb1.getWorldCenter().y < 353.01)
                    || (hb1.getWorldCenter().x > 1102 && hb1.getWorldCenter().y > 300.0 && hb1.getWorldCenter().y < 353.01);
            boolean touchBall = ((hb1.getWorldCenter().sub(ball.getWorldCenter())).length() < 78.4);
            boolean touchGround = (hb1.getWorldCenter().y < 49.01);
            boolean touchHeadBaller = ((hb1.getWorldCenter().sub(hb2.getWorldCenter())).length() < 96);
            if (touchGoal) {
                return true;
            } else if (touchGround) {
                return true;
            } else if (touchBall) {
                if (ball.getWorldCenter().y < hb1.getWorldCenter().y) {
                    return true;
                }
            } else if (touchHeadBaller) {
                if (hb2.getWorldCenter().y < hb1.getWorldCenter().y) {
                    return true;
                }
            } else {
                return false;
            }
        } else {
            Body hb1 = (Body) getHeadballer2().getUserData();
            Body hb2 = (Body) getHeadballer1().getUserData();
            Body ball = (Body) getBall().getUserData();
            boolean touchGoal = (hb1.getWorldCenter().x < 178.0f && hb1.getWorldCenter().y > 300.0 && hb1.getWorldCenter().y < 353.01)
                    || (hb1.getWorldCenter().x > 1102 && hb1.getWorldCenter().y > 300.0 && hb1.getWorldCenter().y < 353.01);
            boolean touchBall = ((hb1.getWorldCenter().sub(ball.getWorldCenter())).length() < 78.4);
            boolean touchGround = (hb1.getWorldCenter().y < 49.01);
            boolean touchHeadBaller = ((hb1.getWorldCenter().sub(hb2.getWorldCenter())).length() < 96);
            if (touchGoal) {
                return true;
            } else if (touchGround) {
                return true;
            } else if (touchBall) {
                if (ball.getWorldCenter().y < hb1.getWorldCenter().y) {
                    return true;
                }
            } else if (touchHeadBaller) {
                if (hb2.getWorldCenter().y < hb1.getWorldCenter().y) {
                    return true;
                }
            } else {
                return false;
            }

        }
        return false;
    }

    public int getGameMode() {
        return gameMode;
    }

    public void setGameMode(int gameMode) {
        this.gameMode = gameMode;
        if (getGameMode() == 2) {
            Random randomGenerator = new Random();
            int a = randomGenerator.nextInt(3) + 1;
            setSelectedBackground(a);
            a = randomGenerator.nextInt(2) + 1;
            setSelectedBall(a);
        }
    }

    public float getGravity() {
        return gravity;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;

    }

    public float getGroundFriction() {
        return groundFriction;
    }

    public void setGroundFriction(float groundFriction) {
        this.groundFriction = groundFriction;
    }

    public int getSelectedChar1() {
        return selectedChar1;
    }

    public void setSelectedChar1(int selectedChar1) {
        this.selectedChar1 = selectedChar1;
        if (selectedChar1 == 1) {
            setHb1Url("11.png");
        } else if (selectedChar1 == 2) {
            setHb1Url("22.png");
        } else if (selectedChar1 == 3) {
            setHb1Url("33.png");
        } else if (selectedChar1 == 4) {
            setHb1Url("44.png");
        } else {
            setHb1Url("44.png");
        }
    }

    public int getSelectedChar2() {
        return selectedChar2;
    }

    public void setSelectedChar2(int selectedChar2) {
        this.selectedChar2 = selectedChar2;
        if (selectedChar2 == 1) {
            setHb2Url("1.png");
        } else if (selectedChar2 == 2) {
            setHb2Url("2.png");
        } else if (selectedChar2 == 3) {
            setHb2Url("3.png");
        } else if (selectedChar2 == 4) {
            setHb2Url("4.png");
        } else {
            setHb2Url("4.png");
        }
    }

    public int getSelectedBall() {
        return selectedBall;
    }

    public void setSelectedBall(int selectedBall) {
        this.selectedBall = selectedBall;
        if (selectedBall == 1) {
            setBallUrl("ball.png");
        } else if (selectedBall == 2) {
            setBallUrl("ball2.png");
        }

    }

    public int getSelectedBackground() {
        return selectedBackground;
    }

    public void setSelectedBackground(int selectedBackground) {
        this.selectedBackground = selectedBackground;
        if (selectedBackground == 1) {
            setBackUrl("/Modal/backgrounds/stadium1.jpg");
            setGravity(-300.0f);
        } else if (selectedBackground == 2) {
            setBackUrl("/Modal/backgrounds/sahabuz_saha.png");
            setGravity(-300.0f);
        } else if (selectedBackground == 3) {
            setBackUrl("/Modal/backgrounds/uzay2.png");
            setGravity(0);
        }
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public Headballer getHeadballer1() {
        return headballer1;
    }

    public void setHeadballer1(Headballer headballer1) {
        this.headballer1 = headballer1;
    }

    public Headballer getHeadballer2() {
        return headballer2;
    }

    public void setHeadballer2(Headballer headballer2) {
        this.headballer2 = headballer2;
    }

    public Ball getBall() {
        return ball;
    }

    public void setBall(Ball ball) {
        this.ball = ball;
    }


    public static float toPixelPosX(float posX) {
        float x = WIDTH * posX / 1280f;
        return x;
    }

    //Convert a JavaFX pixel x coordinate to a JBox2D x coordinate
    public static float toPosX(float posX) {
        float x = (posX * 100.0f * 1.0f) / WIDTH;
        return x;
    }

    //Convert a JBox2D y coordinate to a JavaFX pixel y coordinate
    public static float toPixelPosY(float posY) {
        float y = HEIGHT - (1.0f * HEIGHT) * posY / 720f;
        return y;
    }

    //Convert a JavaFX pixel y coordinate to a JBox2D y coordinate
    public static float toPosY(float posY) {
        float y = 100.0f - ((posY * 100 * 1.0f) / HEIGHT);
        return y;
    }

    //Convert a JBox2D width to pixel width
    public static float toPixelWidth(float width) {
        return WIDTH * width / 100.0f;
    }

    //Convert a JBox2D height to pixel height
    public static float toPixelHeight(float height) {
        return HEIGHT * height / 100.0f;
    }

    public String getHb1Url() {
        return hb1Url;
    }

    public void setHb1Url(String hb1Url) {
        this.hb1Url = hb1Url;
    }

    public String getHb2Url() {
        return hb2Url;
    }

    public void setHb2Url(String hb2Url) {
        this.hb2Url = hb2Url;
    }

    public String getBallUrl() {
        return ballUrl;
    }

    public void setBallUrl(String ballUrl) {
        this.ballUrl = ballUrl;
    }

    public String getBackUrl() {
        return backUrl;
    }

    public void setBackUrl(String backUrl) {
        this.backUrl = backUrl;
    }

    public int getBALL_RADIUS() {
        return BALL_RADIUS;
    }

    public void setBALL_RADIUS(int BALL_RADIUS) {
        this.BALL_RADIUS = BALL_RADIUS;
    }

}
