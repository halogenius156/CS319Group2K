package Modal;
import Controller.GameManager;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
public class Ball extends Map implements Movable {

    private float radius;

    public Ball(float posX, float posY, float radius, String url,GameManager manager)
    {
        super(posX,posY,url,manager);
        this.radius = radius;
        create();
    }

    @Override
    public void create()
    {
        this.setLayoutX(GameManager.toPixelPosX(getPosX()));
        this.setLayoutY(GameManager.toPixelPosY(getPosY()));

        //Create an JBox2D body defination for ball.
        BodyDef bd = new BodyDef();
        bd.type = BodyType.DYNAMIC;
        bd.position.set(getPosX(),getPosY());

        CircleShape cs = new CircleShape();
        cs.m_radius = radius ;

        // Create a fixture for ball
        FixtureDef fd = new FixtureDef();
        fd.shape = cs;
        fd.density = 1f;
        fd.friction = 0.3f;
        fd.restitution = 0.6f;

        /**
         * Virtual invisible JBox2D body of ball. Bodies have velocity and position.
         * Forces, torques, and impulses can be applied to these bodies.
         */
        Body body = manager.getWorld().createBody(bd);

        body.createFixture(fd);
        this.setUserData(body);

    }



}
